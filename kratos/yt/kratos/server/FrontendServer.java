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
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.Kratos;
import yt.kratos.config.SocketConfig;
import yt.kratos.mysql.MySQLDataSource;
import yt.kratos.mysql.config.DatabaseConfig;
import yt.kratos.mysql.pool.MySQLConnectionPool;
import yt.kratos.net.frontend.handler.factory.FrontendHandlerFactory;

/**
 * @ClassName: KratosServer
 * @Description: KratosServer 服务具体业务
 * @author YoungTeam
 * @date 2019年1月3日 下午8:34:30
 *
 */
public class FrontendServer {
	 	private static final Logger logger = LoggerFactory.getLogger(FrontendServer.class);
		private static final FrontendServer instance = new FrontendServer();
		
		/***
		 * 
		* @Title: getInstance
		* @Description: TODO
		* @return KratosServer    返回类型
		* @throws
		 */
		public static final FrontendServer getInstance() {
			return instance;
		}
		
		public void startup(){

			
			EventLoopGroup boss = new NioEventLoopGroup();
	        EventLoopGroup worker = new NioEventLoopGroup();
	        try {
	            ServerBootstrap server = new ServerBootstrap();
	            server.group(boss, worker).channel(NioServerSocketChannel.class)
	            		//设置标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
	             		.option(ChannelOption.SO_BACKLOG, 1024)
	                    .childHandler(
	                    		new FrontendHandlerFactory()
	                    )
	                     
	                    //.option(ChannelOption.SO_KEEPALIVE, true)
	                    //设置ByteBuf对象的创建方式，PooledByteBufAllocator：可以重复利用之前分配的内存空间
	                    .option(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT)
	                    //链接超时毫秒数
		                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, SocketConfig.CONNECT_TIMEOUT_MILLIS)
		                .childOption(ChannelOption.TCP_NODELAY, true)
		                .childOption(ChannelOption.SO_KEEPALIVE, true)
		                //控制读取操作将阻塞多少毫秒
		                .option(ChannelOption.SO_TIMEOUT, SocketConfig.SO_TIMEOUT);	
	            

	            ChannelFuture sync = server.bind(Kratos.SERVER_PORT).sync();
	            sync.channel().closeFuture().sync();
	        } catch (InterruptedException e) {
	        	logger.error("服务监听失败",e);
	        } finally {
	            boss.shutdownGracefully();
	            worker.shutdownGracefully();
	        }

		}
}
