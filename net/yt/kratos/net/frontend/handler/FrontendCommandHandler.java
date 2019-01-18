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
package yt.kratos.net.frontend.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.MySQLPacket;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;


/**
 * @ClassName: FrontendCommandHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月14日 下午10:56:40
 *
 */
public class FrontendCommandHandler  extends ChannelInboundHandlerAdapter {
	  private static final Logger logger = LoggerFactory.getLogger(ChannelHandlerAdapter.class);
	    protected FrontendConnection conn;

	    public FrontendCommandHandler(FrontendConnection conn) {
	        this.conn = conn;
	    }
	    
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	        BinaryPacket bin = (BinaryPacket) msg;
	        byte type = bin.data[0];
	        switch (type) {
	            case MySQLPacket.COM_INIT_DB:
	                // just init the frontend
	                this.conn.initDB(bin);
	                break;
	            case MySQLPacket.COM_QUERY:
	            	this.conn.query(bin);
	                break;
/*	            case MySQLPacket.COM_PING:
	                // todo ping , last access time update
	            	this.conn.ping();
	                break;
	            case MySQLPacket.COM_QUIT:
	            	this.conn.close();
	                break;
	            case MySQLPacket.COM_PROCESS_KILL:
	            	this.conn.kill(bin.data);
	                break;
	            case MySQLPacket.COM_STMT_PREPARE:
	                // todo prepare支持,参考MyCat
	            	this.conn.stmtPrepare(bin.data);
	                break;
	            case MySQLPacket.COM_STMT_EXECUTE:
	            	this.conn.stmtExecute(bin.data);
	                break;
	            case MySQLPacket.COM_STMT_CLOSE:
	            	this.conn.stmtClose(bin.data);
	                break;
	            case MySQLPacket.COM_HEARTBEAT:
	            	this.conn.heartbeat(bin.data);
	                break;*/
	            default:
	            	this.conn.writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Unknown command");
	                break;
	        }	    	
	    }
	  
}
