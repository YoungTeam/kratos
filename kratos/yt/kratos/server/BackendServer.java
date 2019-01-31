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
package yt.kratos.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.config.DatabaseConfig;
import yt.kratos.mysql.pool.MySQLConnectionPool;

/**
 * @ClassName: BackendServer
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月15日 下午3:16:09
 *
 */
public class BackendServer {
	private static final Logger logger = LoggerFactory.getLogger(BackendServer.class);
	private static final BackendServer instance = new BackendServer();
	
	
	public static final BackendServer getInstance() {
		return instance;
	}
	
	public void startup(){
		
    	DatabaseConfig dbConfig = new DatabaseConfig();
    	dbConfig.setHost("127.0.0.1");
    	dbConfig.setUser("root");
    	dbConfig.setPassword("youngteam");
    /*	MySQLConnectionPool pool = new MySQLConnectionPool(dbConfig,20,100);
    	pool.init();*/
	}
}
