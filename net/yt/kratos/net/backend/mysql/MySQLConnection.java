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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.net.AbstractConnection;
import yt.kratos.net.backend.mysql.cmd.Command;

/**
 * @ClassName: BackendConnection
 * @Description: 后端服务链接类
 * @author YoungTeam
 * @date 2019年1月15日 下午4:04:52
 *
 */
public class MySQLConnection  extends AbstractConnection{
	
    private static final Logger logger = LoggerFactory.getLogger(MySQLConnection.class);
    // 当前连接所属的连接池
    public MySQLDataPool mySqlDataPool;
    // 后端连接同步latch
    public CountDownLatch syncLatch;
    // FrontendConnection
    //public FrontendConnection frontend;
    // 当前后端连接堆积的command,通过队列来实现线程间的无锁化
    private ConcurrentLinkedQueue<Command> cmdQue;
    
    public MySQLConnection(MySQLDataPool mySqlDataPool){
    	this.mySqlDataPool = mySqlDataPool;
        this.syncLatch = new CountDownLatch(1);
        this.cmdQue = new ConcurrentLinkedQueue<Command>();
    }
    
    public void countDown() {
    	//等待所有数据库链接初始化状态
        if (!mySqlDataPool.isInited()) {
            mySqlDataPool.countDown();
        }
        syncLatch.countDown();
        // for gc
        syncLatch = null;
    }
}
