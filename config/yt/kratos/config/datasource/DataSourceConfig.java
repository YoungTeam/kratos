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
package yt.kratos.config.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import yt.kratos.mysql.MySQLDataSource;

/**
 * @ClassName: DataNodeConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月24日 下午4:43:37
 *
 */
public class DataSourceConfig {

		public static final DataSourceConfig me = new  DataSourceConfig();
		private HashMap<String,DataSource> DATASOURCE_MAP = new HashMap<String,DataSource>();

		public DataSourceConfig(){

		}
		
		public void init(){
			MySQLDataSource dbSource = new MySQLDataSource("pix");
			dbSource.setHost("127.0.0.1");
			dbSource.setUser("root");
			dbSource.setPassword("youngteam");
			dbSource.setDatabase("pix");
			dbSource.setInitSize(1);
			dbSource.setMaxPoolSize(2);
	    	dbSource.init();
	    	
	    	DATASOURCE_MAP.put(dbSource.getSchema(), dbSource);
			
		}
	
		public MySQLDataSource get(String schema){
			return (MySQLDataSource)DATASOURCE_MAP.get(schema);
		}
		
		public List<String> getSchemas(){
			List<String> schemas =  new ArrayList<String>();
			for(Iterator it = DATASOURCE_MAP.keySet().iterator();it.hasNext();){
				schemas.add(this.get((String)it.next()).getSchema());
			}
			return schemas;
		}
}
