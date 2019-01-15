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
 * @ClassName: Versions
 * @Description: 版本说明
 * @author YoungTeam
 * @date 2019年1月12日 上午12:37:23
 *
 */
public interface Versions {
    /** 协议版本 */
    public static byte PROTOCOL_VERSION = 10;

    /** 服务器版本 */
    public static byte[] SERVER_VERSION = "kratos-1.0.0".getBytes();
}
