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
package yt.kratos.net.session;

import yt.kratos.net.backend.BackendConnection;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.route.RouteResultsetNode;


/**
 * @ClassName: Session
 * @Description: 前后端会话
 * @author YoungTeam
 * @date 2019年1月17日 下午5:55:45
 *
 */
public interface Session {
    /**
     * 取得源端连接
     */
    FrontendConnection getFrontendConnection();

    BackendConnection getBackendConnection(RouteResultsetNode key);
    /**
     * 取得当前目标端数量
     */
    int getTargetCount();

    /**
     * 开启一个会话执行
     */
    //void execute(RouteResultset rrs, int type);

    /**
     * 提交一个会话执行
     */
    void commit();

    /**
     * 回滚一个会话执行
     */
    void rollback();

    /**
     * 取消一个正在执行中的会话
     * 
     * @param sponsor 如果发起者为null，则表示由自己发起。
     */
    void cancel(FrontendConnection frontConn);

    /**
     * 终止会话，必须在关闭源端连接后执行该方法。
     */
    void terminate();
    
    void release();
}
