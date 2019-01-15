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
package yt.kratos.config;


/**
 * @ClassName: SystemConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月14日 下午2:38:28
 *
 */
public interface SystemConfig {
	   public static final int BackendInitialSize = 10;
	    public static final int BackendMaxSize = 20;
	    public static final int BackendInitialWaitTime = 60;
	    public static final String MySqlHost = "127.0.0.1";
	    public static final int ServerPort =8090;
	    public static final int MySqlPort = 3306;
	    public static final String UserName = "root";
	    public static final String PassWord = "123123123";

	    public static final String Database = "";
	    public static final int IdleCheckInterval=5000;
	    public static final int BackendConnectRetryTimes=3;

	    public static String DEFAULT_CHARSET = "utf8";
	    public static int DEFAULT_TX_ISOLATION = Isolations.REPEATED_READ;
}
