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
package yt.kratos.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.Capabilities;
import yt.kratos.config.Versions;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.HandshakeInitialPacket;
import yt.kratos.mysql.packet.HandshakeResponsePacket;
import yt.kratos.net.frontend.IdGenerator;
import yt.kratos.util.CharsetUtil;
import yt.kratos.util.RandomUtil;

/**
 * @ClassName: MessageHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月8日 下午7:52:12
 *
 */
public class MessageHandler extends ChannelInboundHandlerAdapter{
 		private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
 	    private static final IdGenerator ID_GENERATOR = new IdGenerator();
 		
	   @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		   	BinaryPacket bin = (BinaryPacket) msg;
		   	HandshakeResponsePacket responsePacket = new HandshakeResponsePacket();
		   	responsePacket.read(bin);
		   	
		   	System.out.println(responsePacket.user);
		   	System.out.println(responsePacket.password);
	        // check password
/*	        if (!checkPassword(authPacket.password, authPacket.user)) {
	            failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + authPacket.user + "'");
	            return;
	        }
	        source.setUser(authPacket.user);
	        source.setSchema(authPacket.database);
	        source.setHost(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
	        source.setPort(((InetSocketAddress) ctx.channel().remoteAddress()).getPort());
	        success(ctx);*/

	    }

	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    	
            // 生成认证数据
            byte[] rand1 = RandomUtil.randomBytes(8);
            byte[] rand2 = RandomUtil.randomBytes(12);

            // 保存认证数据
            byte[] seed = new byte[rand1.length + rand2.length];
            System.arraycopy(rand1, 0, seed, 0, rand1.length);
            System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);
            //this.seed = seed;

            // 发送握手数据包
            HandshakeInitialPacket hs = new HandshakeInitialPacket();
            hs.packetId = 0;
            hs.protocolVersion = Versions.PROTOCOL_VERSION;
            hs.serverVersion = Versions.SERVER_VERSION;
            hs.connectionId = ID_GENERATOR.getId();
            hs.seed = rand1;
            hs.serverCapabilities = getServerCapabilities();
            int charsetIndex =   CharsetUtil.getDBIndex("utf8");
            hs.serverCharsetIndex = (byte) (charsetIndex & 0xff);
            hs.serverStatus = 2;
            hs.restOfScrambleBuff = rand2;
            hs.write(ctx);
	    	
	    }
	    
	    protected int getServerCapabilities() {
	        int flag = 0;
	        flag |= Capabilities.CLIENT_LONG_PASSWORD;
	        flag |= Capabilities.CLIENT_FOUND_ROWS;
	        flag |= Capabilities.CLIENT_LONG_FLAG;
	        flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
	        // flag |= Capabilities.CLIENT_NO_SCHEMA;
	        // flag |= Capabilities.CLIENT_COMPRESS;
	        flag |= Capabilities.CLIENT_ODBC;
	        // flag |= Capabilities.CLIENT_LOCAL_FILES;
	        flag |= Capabilities.CLIENT_IGNORE_SPACE;
	        flag |= Capabilities.CLIENT_PROTOCOL_41;
	        flag |= Capabilities.CLIENT_INTERACTIVE;
	        // flag |= Capabilities.CLIENT_SSL;
	        flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
	        flag |= Capabilities.CLIENT_TRANSACTIONS;
	        // flag |= ServerDefs.CLIENT_RESERVED;
	        flag |= Capabilities.CLIENT_SECURE_CONNECTION;
	        return flag;
	    }

}
