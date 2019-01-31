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
package yt.kratos.net.frontend;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.SystemConfig;
import yt.kratos.net.AbstractConnection;
import yt.kratos.net.ConnectionFactory;
import yt.kratos.net.frontend.handler.FrontendQueryHandler;

/**
 * @ClassName: FrontendConnectionFactory
 * @Description: 前端链接工厂类
 * @author YoungTeam
 * @date 2019年1月14日 下午2:24:49
 *
 */
public class FrontendConnectionFactory implements ConnectionFactory{

    private static final Logger logger = LoggerFactory.getLogger(FrontendConnectionFactory.class);
    /**
     * MySql ConnectionId Generator
     */
    private static final AtomicInteger ACCEPT_SEQ = new AtomicInteger(0);
    

    public FrontendConnectionFactory(){
    	
    }

    /* 
	* @see yt.kratos.net.ConnectionFactory#getConnection()
	*/ 
	@Override
	public AbstractConnection getConnection() {
			
			//MySQLDataSource dataSource = new MySQLDataSource(pool);
		 	FrontendConnection connection = new FrontendConnection();
	        connection.setQueryHandler(new FrontendQueryHandler(connection));
	        //connection.setId(ACCEPT_SEQ.getAndIncrement());
	        connection.setId(genConnectionId());
	        logger.info("connection Id="+connection.getId());
	        connection.setCharset(SystemConfig.DEFAULT_CHARSET);
	        //connection.setTxIsolation(SystemConfig.DEFAULT_TX_ISOLATION);
	        return connection;
	}
	
	private final int genConnectionId() {
        int current;
        int next;
        do {
            current = ACCEPT_SEQ.get();
            next = current >= 2147483647?0:current + 1;
        } while(!ACCEPT_SEQ.compareAndSet(current, next));

        return next;
    }

}
