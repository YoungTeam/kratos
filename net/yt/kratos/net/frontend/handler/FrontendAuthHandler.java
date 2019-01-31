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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.Versions;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.HandshakeInitialPacket;
import yt.kratos.mysql.packet.HandshakeResponsePacket;
import yt.kratos.mysql.packet.MySQLPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.mysql.packet.QuitPacket;
import yt.kratos.mysql.proto.Capabilities;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.util.CharsetUtil;
import yt.kratos.util.RandomUtil;
import yt.kratos.util.SecurityUtil;

/**
 * @ClassName: FrontendAuthHandler
 * @Description:  前端登录验证处理Handler
 * @author YoungTeam
 * @date 2019年1月11日 下午4:08:12
 *
 */
public class FrontendAuthHandler extends ChannelInboundHandlerAdapter{
		private static final Logger logger = LoggerFactory.getLogger(FrontendAuthHandler.class);
		private byte[] seed;
 	    protected FrontendConnection conn;

 	    public FrontendAuthHandler(FrontendConnection conn) {
 	        this.conn = conn;
 	    }
 	    
	   @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		   	BinaryPacket bin = (BinaryPacket) msg;
		   	if(bin.calcPacketSize() == QuitPacket.QUIT.length && bin.data[4] == MySQLPacket.COM_QUIT){
		   		this.conn.close();
		   		return;
		   	}
		   	
		   	HandshakeResponsePacket responsePacket = new HandshakeResponsePacket();
		   	responsePacket.read(bin);
		   	
		   	//check user
		   	if(!this.checkUser(responsePacket.user,"")){
		   		this.failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + responsePacket.user + "'");
		   		return;
		   	}

	        // check password
	        if (!checkPassword(responsePacket.password, responsePacket.user)) {
	            failure(ErrorCode.ER_ACCESS_DENIED_ERROR, "Access denied for user '" + responsePacket.user + "'");
	            return;
	        }

	        this.conn.setUser(responsePacket.user);
	        this.conn.setSchema(responsePacket.database);
	        this.conn.setHost(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
	        this.conn.setPort(((InetSocketAddress) ctx.channel().remoteAddress()).getPort());
	        success(ctx);
	    }
	   
	   private boolean checkUser(String user,String host){
		   return true;
	   }

	    protected boolean checkPassword(byte[] password, String user) {
	        // todo config
	        String pass = "123456";

	        // check null
	        if (pass == null || pass.length() == 0) {
	            if (password == null || password.length == 0) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	        if (password == null || password.length == 0) {
	            return false;
	        }

	        // encrypt
	        byte[] encryptPass = null;
	        try {
	            encryptPass = SecurityUtil.scramble411(pass.getBytes(), seed);
	        } catch (NoSuchAlgorithmException e) {
	            logger.warn(conn.toString(), e);
	            return false;
	        }
	        if (encryptPass != null && (encryptPass.length == password.length)) {
	            int i = encryptPass.length;
	            while (i-- != 0) {
	                if (encryptPass[i] != password[i]) {
	                    return false;
	                }
	            }
	        } else {
	            return false;
	        }

	        return true;
	    }
	   
	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    	// ctx bind
	        this.conn.setCtx(ctx);
	        
            // 生成认证数据
            byte[] rand1 = RandomUtil.randomBytes(8);
            byte[] rand2 = RandomUtil.randomBytes(12);

            // 保存认证数据
            byte[] seed = new byte[rand1.length + rand2.length];
            System.arraycopy(rand1, 0, seed, 0, rand1.length);
            System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);
            this.seed = seed;

            // 发送握手数据包
            HandshakeInitialPacket hs = new HandshakeInitialPacket();
            hs.packetId = 0;
            hs.protocolVersion = Versions.PROTOCOL_VERSION;
            hs.serverVersion = Versions.SERVER_VERSION.getBytes();
            hs.connectionId = this.conn.getId();
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

	    private void success(final ChannelHandlerContext ctx) {
	        // AUTH_OK , process command
	        ctx.pipeline().replace(this, "frontCommandHandler", new FrontendCommandHandler(this.conn));
	        // AUTH_OK is stable
	        ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(OKPacket.AUTH_OK);
	        // just io , no need thread pool
	        ctx.writeAndFlush(byteBuf);
	    }
	    
	    private void failure(int errno, String info) {
	        logger.error(this.conn.toString() + info);
	        this.conn.writeErrMessage((byte) 2, errno, info);
	    }
}
