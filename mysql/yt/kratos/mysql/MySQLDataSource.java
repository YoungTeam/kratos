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
package yt.kratos.mysql;

import yt.kratos.config.datasource.DataSource;
import yt.kratos.mysql.config.DatabaseConfig;
import yt.kratos.mysql.pool.MySQLConnectionPool;
import yt.kratos.net.backend.BackendConnection;


/**
 * @ClassName: MySQLDataSource
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月21日 下午5:02:35
 *
 */
public class MySQLDataSource extends DataSource{
	
	private String host = "127.0.0.1";
	private int port = 3306;
    private String user = "root";
    private String password = "youngteam";
    private String database = "roc";
	private int initSize = 20;
	private int maxPoolSize = 50;
    
    private MySQLConnectionPool dataPool;

    public MySQLDataSource(String schema) {
    	super(schema);
    }

    public void init (){
    	this.dataPool= new MySQLConnectionPool(this,this.initSize,this.maxPoolSize);
    	this.dataPool.init();
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public int getInitSize() {
		return initSize;
	}

	public void setInitSize(int initSize) {
		this.initSize = initSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public MySQLConnectionPool getDataPool() {
		return dataPool;
	}
	
	public void setDataPool(MySQLConnectionPool dataPool) {
		this.dataPool = dataPool;
	}
    
    
/*    public BackendConnection getBackend() {
        return dataPool.getBackendConnection();
    }

    public void recycle(BackendConnection backend){
        //dataPool.putBackend(backend);
    }*/
}
