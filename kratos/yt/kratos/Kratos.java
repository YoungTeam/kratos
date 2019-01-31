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
package yt.kratos;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.datasource.DataSourceConfig;
import yt.kratos.server.FrontendServer;
import yt.kratos.util.DateUtil;

/**
 * @ClassName: Kratos
 * @Description: Kratos全局配置类
 * @author YoungTeam
 * @date 2019年1月8日 下午7:55:31
 *
 */
public class Kratos {
	
	private static final Logger logger = LoggerFactory.getLogger(Kratos.class);
	public static final Kratos me = new  Kratos();
	
	public static int SERVER_PORT = 1506;
    private final AtomicBoolean isOnline;
	
	public static DataSourceConfig DataSource(){
		return DataSourceConfig.me;
	}
	
	public Kratos(){
	     this.isOnline = new AtomicBoolean(true);
	}

    public boolean isOnline() {
        return isOnline.get();
    }

    public void offline() {
        isOnline.set(false);
    }

    public void online() {
        isOnline.set(true);
    }	
	
	public static void main(String[] args){
        try {
        	DataSourceConfig.me.init();
            // init        	
        	FrontendServer server = FrontendServer.getInstance();
        	server.startup();
        	
        } catch (Throwable e) {
        	logger.error(DateUtil.now() + " startup error", e);
            System.exit(-1);
        }
	}	

}
