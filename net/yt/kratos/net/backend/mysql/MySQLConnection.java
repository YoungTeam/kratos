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

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.exception.UnknownCharsetException;
import yt.kratos.mysql.packet.HandshakeInitialPacket;
import yt.kratos.mysql.packet.HandshakeResponsePacket;
import yt.kratos.mysql.proto.Capabilities;
import yt.kratos.net.AbstractConnection;
import yt.kratos.net.backend.BackendConnection;
import yt.kratos.net.backend.mysql.cmd.Command;
import yt.kratos.util.CharsetUtil;
import yt.kratos.util.SecurityUtil;

/**
 * @ClassName: BackendConnection
 * @Description: 后端服务链接类
 * @author YoungTeam
 * @date 2019年1月15日 下午4:04:52
 *
 */
public class MySQLConnection  extends BackendConnection{
	
    private static final Logger logger = LoggerFactory.getLogger(MySQLConnection.class);
    
    private static final long CAPABILITY_FLAGS = initCapabilities();
    private static final long MAX_PACKET_SIZE = 1024 * 1024 * 16;
    private static long initCapabilities() {
        int flag = 0;
        flag |= Capabilities.CLIENT_LONG_PASSWORD;
        flag |= Capabilities.CLIENT_FOUND_ROWS;
        flag |= Capabilities.CLIENT_LONG_FLAG;
        flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
        // flag |= Capabilities.CLIENT_NO_SCHEMA;
        // flag |= Capabilities.CLIENT_COMPRESS;
        flag |= Capabilities.CLIENT_ODBC;
        // flag |= Capabilities.CLIENT_LOCAL_FILES;
        flag |= Capabilities.CLIENT_IGNORE_SPACE;
        flag |= Capabilities.CLIENT_PROTOCOL_41;
        flag |= Capabilities.CLIENT_INTERACTIVE;
        // flag |= Capabilities.CLIENT_SSL;
        flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
        flag |= Capabilities.CLIENT_TRANSACTIONS;
        // flag |= Capabilities.CLIENT_RESERVED;
        flag |= Capabilities.CLIENT_SECURE_CONNECTION;
        // client extension
        // 不允许MULTI协议
        // flag |= Capabilities.CLIENT_MULTI_STATEMENTS;
        // flag |= Capabilities.CLIENT_MULTI_RESULTS;
        return flag;
    }
    // 当前连接所属的连接池
    public MySQLDataPool mySqlDataPool;
    // 后端连接同步latch
    public CountDownLatch syncLatch;
    private boolean isAuthenticated;
    // FrontendConnection
    //public FrontendConnection frontend;
    // 当前后端连接堆积的command,通过队列来实现线程间的无锁化
    private ConcurrentLinkedQueue<Command> cmdQue;
    
    public MySQLConnection(MySQLDataPool mySqlDataPool){
    	this.mySqlDataPool = mySqlDataPool;
        this.syncLatch = new CountDownLatch(1);
        this.cmdQue = new ConcurrentLinkedQueue<Command>();
    }
    
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    /**
     * 
    * @Title: postCommand
    * @Description: 插入新的命令到队尾
    * @return void    返回类型
    * @throws
     */
    public void postCommand(Command command) {
        cmdQue.offer(command);
    }

    /**
     * 
    * @Title: peekCommand
    * @Description: 从队列头提取命令但不移除
    * @return Command    返回类型
    * @throws
     */
    public Command peekCommand() {
        return cmdQue.peek();
    }

    /**
     * 
    * @Title: pollCommand
    * @Description: 从队列头提取命令并移除
    * @return Command    返回类型
    * @throws
     */
    public Command pollCommand() {
        return cmdQue.poll();
    }
    
    public void authenticate(HandshakeInitialPacket hsi){
    	  this.id = hsi.connectionId;
          int ci = hsi.serverCharsetIndex & 0xff;//获取服务器字符集
          if ((this.charset = CharsetUtil.getCharset(ci)) != null) {
              this.charsetIndex = ci;
          } else {
              throw new UnknownCharsetException("charset:" + ci);
          }
          
			HandshakeResponsePacket hsp = new HandshakeResponsePacket();
			hsp.packetId = 1;
			hsp.capabilityFlags = CAPABILITY_FLAGS;
			hsp.maxPacketSize = MAX_PACKET_SIZE;
			hsp.charsetIndex = this.charsetIndex;
			  // todo config
			hsp.user = this.mySqlDataPool.getDbConfig().getUser();
			//ap.user = SystemConfig.UserName;
			String passwd = this.mySqlDataPool.getDbConfig().getPassword();
			try {
				hsp.password = passwd(passwd,hsi);
			} catch (NoSuchAlgorithmException e) {
					logger.error("auth packet errorMessage", e);
					throw new RuntimeException(e.getMessage());
			}
			// todo config
			hsp.database = "";
			hsp.write(ctx);
    }
    
    private static byte[] passwd(String pass, HandshakeInitialPacket hsi) throws NoSuchAlgorithmException {
        if (pass == null || pass.length() == 0) {
            return null;
        }
        byte[] passwd = pass.getBytes();
        int sl1 = hsi.seed.length;
        int sl2 = hsi.restOfScrambleBuff.length;
        byte[] seed = new byte[sl1 + sl2];
        System.arraycopy(hsi.seed, 0, seed, 0, sl1);
        System.arraycopy(hsi.restOfScrambleBuff, 0, seed, sl1, sl2);
        return SecurityUtil.scramble411(passwd, seed);
    }
    
    public void countDown() {
    	//等待所有数据库链接初始化成功状态
        if (!mySqlDataPool.isInited()) {
            mySqlDataPool.countDown();
        }
        
        syncLatch.countDown();
        // for gc
        syncLatch = null;
    }
}
