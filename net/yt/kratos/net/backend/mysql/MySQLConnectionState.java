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

/**
 * @ClassName: MySQLConnectionState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月15日 下午6:46:57
 *
 */
public interface MySQLConnectionState {
    // 后端连接尚未初始化
    int BACKEND_NOT_AUTHED=0;
    // 后端连接初始化成功
    int BACKEND_AUTHED=1;

    // must 连续
    int RESULT_SET_FIELD_COUNT = 2;
    int RESULT_SET_FIELDS = 3;
    int RESULT_SET_EOF = 4;
    int RESULT_SET_ROW = 5;
    int RESULT_SET_LAST_EOF = 6;
}
