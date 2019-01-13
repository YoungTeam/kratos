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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.server.KratosServer;
import yt.kratos.util.DateUtil;

/**
 * @ClassName: Start
 * @Description: Kratos服务启动类
 * @author YoungTeam
 * @date 2019年1月11日 下午3:56:02
 *
 */
public class Start {
	private static final Logger LOGGER = LoggerFactory.getLogger(Start.class);
	public static void main(String[] args){
        try {
            // init
        	KratosServer server = KratosServer.getInstance();
        	server.startup();
            //server.beforeStart();

            // startup
            //server.startup();
        } catch (Throwable e) {
        	LOGGER.error(DateUtil.now() + " startup error", e);
            System.exit(-1);
        }
	}
}
