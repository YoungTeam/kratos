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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import yt.kratos.mysql.MySQLDataSource;
import yt.kratos.net.codec.MySQLPacketDecoder;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.frontend.FrontendConnectionFactory;
import yt.kratos.net.frontend.handler.FrontendAuthHandler;
import yt.kratos.net.frontend.handler.FrontendCollectHandler;
import yt.kratos.net.frontend.handler.FrontendExceptionHandler;

/**
 * @ClassName: FrontendHandlerFactory
 * @Description: 前端handler工厂
 * @author YoungTeam
 * @date 2019年1月13日 下午10:50:08
 *
 */
public class FrontendHandlerFactory extends ChannelInitializer<SocketChannel> {

    private FrontendConnectionFactory factory;
    
    public FrontendHandlerFactory() {
        factory = new FrontendConnectionFactory();
    }
    
	/* 
	* @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	*/ 
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
        FrontendConnection conn = (FrontendConnection)factory.getConnection();
        FrontendCollectHandler collectHandler = new FrontendCollectHandler(conn);
        FrontendAuthHandler authHandler = new FrontendAuthHandler(conn);
        FrontendExceptionHandler exceptionHandler = new FrontendExceptionHandler(conn);
        // 心跳handler
        //ch.pipeline().addLast(new IdleStateHandler(10, 10, 10));
        // decode mysql packet depend on it's length
        ch.pipeline().addLast(new MySQLPacketDecoder());
        ch.pipeline().addLast(collectHandler);
        ch.pipeline().addLast(authHandler);
        ch.pipeline().addLast(exceptionHandler);
	}

}
