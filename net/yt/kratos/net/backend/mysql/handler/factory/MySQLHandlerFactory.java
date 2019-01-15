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
package yt.kratos.net.backend.mysql.handler.factory;

import io.netty.channel.socket.SocketChannel;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.MySQLConnectionFactory;
import yt.kratos.net.backend.mysql.handler.MySQLAuthHandler;
import yt.kratos.net.backend.mysql.handler.MySQLInitHandler;
import yt.kratos.net.codec.MySQLPacketDecoder;
import yt.kratos.net.frontend.handler.FrontendAuthHandler;



/**
 * @ClassName: MySQLHandlerFactory
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月15日 下午5:33:38
 *
 */
public class MySQLHandlerFactory {
    private MySQLConnectionFactory factory;

    public MySQLHandlerFactory(MySQLConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        MySQLConnection connection = (MySQLConnection)factory.getConnection();
        //FrontendAuthHandler authHandler = new FrontendAuthHandler(conn);
        MySQLInitHandler initHandler = new MySQLInitHandler(connection);
        MySQLAuthHandler authHandler = new MySQLAuthHandler(connection);
        //BackendTailHandler tailHandler = new BackendTailHandler(connection);*/
        ch.pipeline().addLast(new MySQLPacketDecoder());
        //ch.pipeline().addLast("BackendHeadHandler", firstHandler);
        ch.pipeline().addLast(authHandler);
        ch.pipeline().addLast(tailHandler);
    }
}
