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
package yt.kratos.net.frontend.handler.factory;
import alchemystar.lancelot.common.net.codec.MySqlPacketDecoder;
import alchemystar.lancelot.common.net.handler.frontend.FrontendAuthenticator;
import alchemystar.lancelot.common.net.handler.frontend.FrontendConnection;
import alchemystar.lancelot.common.net.handler.frontend.FrontendGroupHandler;
import alchemystar.lancelot.common.net.handler.frontend.FrontendTailHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @ClassName: FrontendHandlerFactory
 * @Description: 前端handler工厂
 * @author YoungTeam
 * @date 2019年1月13日 下午10:50:08
 *
 */
public class FrontendHandlerFactory extends ChannelInitializer<SocketChannel> {

	/* 
	* @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	*/ 
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
       // FrontendConnection source = factory.getConnection();
        //FrontendGroupHandler groupHandler = new FrontendGroupHandler(source);
        FrontendAuthenticator authHandler = new FrontendAuthenticator(source);
        //FrontendTailHandler tailHandler = new FrontendTailHandler(source);
        // 心跳handler
        //ch.pipeline().addLast(new IdleStateHandler(10, 10, 10));
        // decode mysql packet depend on it's length
        //ch.pipeline().addLast(new MySqlPacketDecoder());
        //ch.pipeline().addLast(groupHandler);
        ch.pipeline().addLast(authHandler);
        //ch.pipeline().addLast(tailHandler);
	}

}
