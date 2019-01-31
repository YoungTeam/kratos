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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.MySQLConnectionState;
import yt.kratos.net.handler.ResponseHandler;
import yt.kratos.net.session.FrontendSession;

/**
 * @ClassName: MySQLCommandHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月16日 下午8:17:57
 *
 */
public class MySQLCommandHandler   extends ChannelInboundHandlerAdapter{
	
    private static final Logger logger = LoggerFactory.getLogger(MySQLCommandHandler.class);
    
	private MySQLConnection conn;

    
    public MySQLCommandHandler(MySQLConnection conn) {
        this.conn = conn;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BinaryPacket bin = (BinaryPacket) msg;
        
        boolean finished = this.getResponseHandler().handleResponse(this.conn,bin);
        if(finished){
        	//remove this cmd;
        	this.conn.pollCommand();
        	// fire the next cmd
            this.conn.fireCmd();
        }
    }
    
    private ResponseHandler getResponseHandler() {
        FrontendSession session = (FrontendSession)this.conn.getSession();
        return session.getResponseHandler();
    }
}
