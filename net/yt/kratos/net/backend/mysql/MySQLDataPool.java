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
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @ClassName: MySQLDataPool
 * @Description: Mysql 后端数据连接池
 * @author YoungTeam
 * @date 2019年1月15日 下午5:18:41
 *
 */
public class MySQLDataPool {
	private static final Logger logger = LoggerFactory.getLogger(MySQLDataPool.class);

    // 当前连接池中空闲的连接数
    private int idleCount;
    // 最大连接数
    private final int maxPoolSize;
    // 初始化连接数
    private int initSize;
    // 连接池
    private final MySQLConnection[] items;
    // Backend Loop Group
    private EventLoopGroup backendGroup;
    // Backend Bootstrap
    private Bootstrap bootstrap;
    // Backend Connection Factory
    private MySQLConnectionFactory factory;
    // 线程间同步的闩锁
    private CountDownLatch latch;
    // get/put的锁
    private final ReentrantLock lock;
    // 当前连接池是否被初始化成功的标识
    private final AtomicBoolean initialized;
    // data pool的command allocator
    private ByteBufAllocator allocator;
    
    public MySQLDataPool(int initSize, int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.initSize = initSize;
        this.idleCount = 0;
        this.items = new MySQLConnection[maxPoolSize];
        this.backendGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        latch = new CountDownLatch(initSize);
        lock = new ReentrantLock();
        initialized = new AtomicBoolean(false);
        allocator = new UnpooledByteBufAllocator(false);
    }
    
    public void init(){
        //this.factory = new MySQLConnectionFactory(this);
    	this.factory = new MySQLConnectionFactory(this);
        // 采用PooledBuf来减少GC
        this.bootstrap.group(backendGroup).channel(NioSocketChannel.class).handler(new MySQLHandlerFactory(factory))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        setOption(b);
/*        initBackends();
        initIdleCheck();
        markInit();*/
    }
    
    
    public boolean isInited() {
        return initialized.get();
    }
    
    /**
     * 
    * @Title: countDown
    * @Description: 同步pool中所有链接状态
    * @return void    返回类型
    * @throws
     */
    public void countDown() {
        this.latch.countDown();
    }
}
