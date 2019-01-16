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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.exception.ErrorPacketException;
import yt.kratos.exception.UnknownPacketException;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.HandshakeInitialPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.MySQLConnectionState;

/**
 * @ClassName: MySQLAuthHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月15日 下午6:41:42
 *
 */
public class MySQLAuthHandler extends ChannelInboundHandlerAdapter{
	public static final String HANDLER_NAME = "MySQLAuthHandler";
	   
    private static final Logger logger = LoggerFactory.getLogger(MySQLAuthHandler.class);
    private int state = MySQLConnectionState.BACKEND_NOT_AUTHED;

    private MySQLConnection conn;
    
    public MySQLAuthHandler(MySQLConnection conn) {
    	this.conn = conn;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        switch (state) {
            case (MySQLConnectionState.BACKEND_NOT_AUTHED):
                // init source's ctx here
            	this.conn.setCtx(ctx);
                // 处理服务器握手包并发送auth包
            	HandshakeInitialPacket hsi = new HandshakeInitialPacket();	        
	            hsi.read((BinaryPacket) msg);
            	this.conn.authenticate(hsi);
                // 推进连接状态
                state = MySQLConnectionState.BACKEND_AUTHED;
                break;
            case (MySQLConnectionState.BACKEND_AUTHED):
                authOk(ctx, msg);
                break;
            default:
                break;
        }
    }

    private void authOk(ChannelHandlerContext ctx, Object msg) {
    	
/*    	 if (listener != null) {
             listener.connectionAcquired(source);
         }*/
         
        BinaryPacket bin = (BinaryPacket) msg;
        switch (bin.data[0]) {
            case OKPacket.FIELD_COUNT:
                //afterSuccess();
                this.conn.setAuthenticated(true);
                break;
            case ErrorPacket.FIELD_COUNT:
                ErrorPacket err = new ErrorPacket();
                err.read(bin);
                throw new ErrorPacketException("Auth not Okay");
            default:
                throw new UnknownPacketException(bin.toString());
        }
        // to wake up the start up thread
        //this.co nn.countDown();
        // replace the commandHandler of Authenticator
        //ctx.pipeline().replace(this, "BackendCommandHandler", new BackendCommandHandler(source));

    }


 
}
