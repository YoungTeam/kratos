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
 * @ClassName: SocketConfig
 * @Description: 
 * @author YoungTeam
 * @date 2019年1月14日 下午4:02:51
 *
 */
public interface SocketConfig {
    int Frontend_Socket_Recv_Buf = 4 * 1024 * 1024;
    int Frontend_Socket_Send_Buf = 1024 * 1024;
    int Backend_Socket_Recv_Buf = 4 * 1024 * 1024;
    int Backend_Socket_Send_Buf = 1024 * 1024;// mysql 5.6
    int CONNECT_TIMEOUT_MILLIS = 5000;
    int SO_TIMEOUT =  10 * 60;
}
