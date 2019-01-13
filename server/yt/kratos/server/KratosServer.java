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
package yt.kratos.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.Kratos;
import yt.kratos.net.codec.MySQLPacketDecoder;
import yt.kratos.server.handler.MessageHandler;

/**
 * @ClassName: KratosServer
 * @Description: KratosServer 服务具体业务
 * @author YoungTeam
 * @date 2019年1月3日 下午8:34:30
 *
 */
public class KratosServer {
	 	private static final Logger LOGGER = LoggerFactory.getLogger(KratosServer.class);
		private static final KratosServer INSTANCE = new KratosServer();
		
		/***
		 * 
		* @Title: getInstance
		* @Description: TODO
		* @return KratosServer    返回类型
		* @throws
		 */
		public static final KratosServer getInstance() {
			return INSTANCE;
		}
		
		public void startup(){
			EventLoopGroup boss = new NioEventLoopGroup();
	        EventLoopGroup worker = new NioEventLoopGroup();
	        try {
	            ServerBootstrap server = new ServerBootstrap();
	            server.group(boss, worker).channel(NioServerSocketChannel.class)
	                    .childHandler(new ChannelInitializer<Channel>() {

	                        @Override
	                        protected void initChannel(Channel ch) throws Exception {
	                        	ch.pipeline().addLast("input cmd", new MySQLPacketDecoder());//.addLast(new MessageHandler());
	                            ch.pipeline().addLast("test msg",new MessageHandler());
	                        }

	                    });

	            ChannelFuture sync = server.bind(Kratos.SERVER_PORT).sync();
	            sync.channel().closeFuture().sync();
	        } catch (InterruptedException e) {

	            e.printStackTrace();
	        } finally {
	            boss.shutdownGracefully();
	            worker.shutdownGracefully();
	        }

		}
}
