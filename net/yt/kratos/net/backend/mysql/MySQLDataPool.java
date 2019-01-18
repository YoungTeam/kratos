/*
 * Copyright 2019 YoungTeam@Sogou Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yt.kratos.net.backend.mysql;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.SocketConfig;
import yt.kratos.config.SystemConfig;
import yt.kratos.mysql.config.DatabaseConfig;
import yt.kratos.net.backend.mysql.handler.MySQLChannelPoolHandler;
import yt.kratos.net.backend.mysql.handler.factory.MySQLHandlerFactory;



/**
 * @ClassName: MySQLDataPool
 * @Description: Mysql 后端数据连接池
 * @author YoungTeam
 * @date 2019年1月15日 下午5:18:41
 *
 */
public class MySQLDataPool {
	private static final Logger logger = LoggerFactory.getLogger(MySQLDataPool.class);

	private DatabaseConfig dbConfig;
    // 当前连接池中空闲的连接数
    private int idleCount;
    // 最大连接数
    private final int maxPoolSize;
    // 初始化连接数
    private int initSize;
    // 连接池
    private FixedChannelPool channelPool;
    
    
    // Backend Loop Group
    private EventLoopGroup backendGroup;
    // Backend Bootstrap
    private Bootstrap bootstrap;
    // Backend Connection Factory
    private MySQLConnectionFactory factory;
    // 线程间同步的闩锁
    private CountDownLatch latch;
    // get/put的锁
    //private final ReentrantLock lock;
    // 当前连接池是否被初始化成功的标识
    private final AtomicBoolean initialized;
    // data pool的command allocator
    private ByteBufAllocator allocator;
    
    public MySQLDataPool(DatabaseConfig dbConfig,int initSize, int maxPoolSize) {
    	this.dbConfig = dbConfig;
        this.maxPoolSize = maxPoolSize;
        this.initSize = initSize;
        this.idleCount = 0;
        this.backendGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.latch = new CountDownLatch(initSize);//初始化链接同步latch
       // lock = new ReentrantLock();
        initialized = new AtomicBoolean(false);
        allocator = new UnpooledByteBufAllocator(false);
        //this.init();
    }
    
    public void init(){
       this.factory = new MySQLConnectionFactory(this);
        // 采用PooledBuf来减少GC
        this.bootstrap.group(backendGroup).channel(NioSocketChannel.class).handler(new MySQLHandlerFactory(factory))
                		.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.bootstrap.option(ChannelOption.SO_RCVBUF, SocketConfig.Backend_Socket_Recv_Buf);
        this.bootstrap.option(ChannelOption.SO_SNDBUF, SocketConfig.Backend_Socket_Send_Buf);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,SocketConfig.CONNECT_TIMEOUT_MILLIS);
        this.bootstrap.option(ChannelOption.SO_TIMEOUT,SocketConfig.SO_TIMEOUT);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        this.bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK,
                1024 * 1024);
        
        InetSocketAddress remoteaddress = InetSocketAddress.createUnresolved(this.dbConfig.getHost(), this.dbConfig.getPort());
        this.bootstrap.remoteAddress(remoteaddress);
         this.channelPool = new FixedChannelPool(bootstrap, new MySQLChannelPoolHandler(this.factory), this.maxPoolSize);
         
         //初始化连接数         
         for(int i=0;i<this.initSize;i++){
        	 this.channelPool.acquire();
         }
         
         try {
			this.latch.await();
			logger.info("MySQL Backend connection(count:{}) succed ",this.initSize);
			this.latch = null; //for gc
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			   logger.error("MySQL Backend connection failed ", e);
		}
         
        this.initIdleCheck();//心跳检测链接状态
        
    }
    
    /**
     * 
    * @Title: initIdleCheck
    * @Description: 初始化心跳检测
    * @return void    返回类型
    * @throws
     */
    private void initIdleCheck(){
    	  this.backendGroup.scheduleAtFixedRate(new IdleCheckTask(), SystemConfig.IdleCheckInterval, SystemConfig
         .IdleCheckInterval,TimeUnit.MILLISECONDS);
    }

    /**
     * IdleCheck
     */
    private class IdleCheckTask implements Runnable {
        public void run() {
            idleCheck();
        }

        private void idleCheck() {
            // 伪代码 类Druid的最小锁时间实现
            // 在这个地方拿出连接
            // 心跳command用当前allocate(Unpooled)来分配,防内存泄露
            // postCommand(HeartBeat)
            // fireCmd
            // markInHeartBeat
            // 在commonHandler中
            // if(marInHeartBeat) then read okay
            // then recycle
            // heartBeat完成,锁只在一处
            logger.info("we now do idle check");
        }
    }    
    
    
    public boolean isInited() {
        return initialized.get();
    }
      
    public DatabaseConfig getDbConfig() {
		return dbConfig;
	}

	public void setDbConfig(DatabaseConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

	/**
     * 
    * @Title: countDown
    * @Description: connection连接成功，同步通知主线程poll
    * @return void    返回类型
    * @throws
     */
    public void countDown() {
        this.latch.countDown();
    }
    
    public static void main(String[] args){
    	DatabaseConfig dbConfig = new DatabaseConfig();
    	dbConfig.setHost("127.0.0.1");
    	dbConfig.setUser("root");
    	dbConfig.setPassword("youngteam");
    	MySQLDataPool pool = new MySQLDataPool(dbConfig,20,100);
    	pool.init();
    }
}
