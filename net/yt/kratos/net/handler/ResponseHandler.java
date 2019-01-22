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
package yt.kratos.net.handler;

import java.util.List;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.net.route.RouteResultset;


/**
 * @ClassName: ResponseHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月20日 下午2:48:55
 *
 */
public interface ResponseHandler {
    // 执行sql
    void execute(RouteResultset rrs);

    // fieldListResponse
    void fieldListResponse(List<BinaryPacket> fieldList);

    // errorResponse
    void errorResponse(BinaryPacket bin);

    // okResponse
    void okResponse(BinaryPacket bin);

    // rowRespons
    void rowResponse(BinaryPacket bin);

    // lastEofResponse
    void lastEofResponse(BinaryPacket bin);
}
