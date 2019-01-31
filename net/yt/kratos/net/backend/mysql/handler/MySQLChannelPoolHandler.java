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
package yt.kratos.net.backend.mysql.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.MySQLConnectionFactory;
import yt.kratos.net.codec.MySQLPacketDecoder;

/**
 * @ClassName: MySQLChannelPoolHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月16日 下午5:50:03
 *
 */
public class MySQLChannelPoolHandler implements ChannelPoolHandler {
	private static final Logger logger = LoggerFactory.getLogger(MySQLChannelPoolHandler.class);
	MySQLConnectionFactory factory;
	
	public MySQLChannelPoolHandler(MySQLConnectionFactory factory){
		this.factory =factory;
	}
	/* 使用完channel需要释放才能放入连接池
	* @see io.netty.channel.pool.ChannelPoolHandler#channelReleased(io.netty.channel.Channel)
	*/ 
	@Override
	public void channelReleased(Channel ch) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* 获取连接池中的channel
	* @see io.netty.channel.pool.ChannelPoolHandler#channelAcquired(io.netty.channel.Channel)
	*/ 
	@Override
	public void channelAcquired(Channel ch) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* 当channel不足时会创建，但不会超过限制的最大channel数
	* @see io.netty.channel.pool.ChannelPoolHandler#channelCreated(io.netty.channel.Channel)
	*/ 
	@Override
	public void channelCreated(Channel ch) throws Exception {
        MySQLConnection connection = (MySQLConnection)factory.getConnection();
        connection.setCh(ch);
        //FrontendAuthHandler authHandler = new FrontendAuthHandler(conn);
        MySQLInitHandler initHandler = new MySQLInitHandler(connection);
        MySQLAuthHandler authHandler = new MySQLAuthHandler(connection);
        //BackendTailHandler tailHandler = new BackendTailHandler(connection);*/
        ch.pipeline().addLast(new MySQLPacketDecoder());
        ch.pipeline().addLast(MySQLInitHandler.HANDLER_NAME, initHandler);
        ch.pipeline().addLast(authHandler);
        
        logger.info("Create a new MySQLConnection"+connection);
	}

}
