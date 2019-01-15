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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.Capabilities;
import yt.kratos.exception.UnknownCharsetException;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.HandshakeInitialPacket;
import yt.kratos.mysql.packet.HandshakeResponsePacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.MySQLConnectionState;
import yt.kratos.util.CharsetUtil;
import yt.kratos.util.SecurityUtil;

/**
 * @ClassName: MySQLAuthHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月15日 下午6:41:42
 *
 */
public class MySQLAuthHandler extends ChannelInboundHandlerAdapter{
    private static final Logger logger = LoggerFactory.getLogger(MySQLAuthHandler.class);

    private static final long CAPABILITY_FLAGS = getServerCapabilities();
    private static final long MAX_PACKET_SIZE = 1024 * 1024 * 16;

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
                this.handleHandshake(ctx, msg);
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
        BinaryPacket bin = (BinaryPacket) msg;
        switch (bin.data[0]) {
            case OKPacket.FIELD_COUNT:
                afterSuccess();
                break;
            case ErrorPacket.FIELD_COUNT:
                ErrorPacket err = new ErrorPacket();
                err.read(bin);
                throw new ErrorPacketException("Auth not Okay");
            default:
                throw new UnknownPacketException(bin.toString());
        }
        // to wake up the start up thread
        this.conn.countDown();
        // replace the commandHandler of Authenticator
        ctx.pipeline().replace(this, "BackendCommandHandler", new BackendCommandHandler(source));

    }
    
    private void handleHandshake(ChannelHandlerContext ctx, Object msg) {
        // 发送握手数据包
        HandshakeInitialPacket hsi = new HandshakeInitialPacket();
    
        hsi.read((BinaryPacket) msg);
        this.conn.setId(hsi.connectionId);
        int ci = hsi.serverCharsetIndex & 0xff;//获取服务器字符集
        if ((this.conn.charset = CharsetUtil.getCharset(ci)) != null) {
            this.conn.charsetIndex = ci;
        } else {
            throw new UnknownCharsetException("charset:" + ci);
        }
        try {
        	this.sendAuthResponse(hsi, ctx);
        } catch (Exception e) {
            logger.error("auth packet errorMessage", e);
        }
    }
    
    private void sendAuthResponse(HandshakeInitialPacket hsi, ChannelHandlerContext ctx)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	HandshakeResponsePacket hsp = new HandshakeResponsePacket();
    	hsp.packetId = 1;
    	hsp.capabilityFlags = CAPABILITY_FLAGS;
    	hsp.maxPacketSize = MAX_PACKET_SIZE;
    	hsp.charsetIndex = this.conn.charsetIndex;
        // todo config
    	hsp.user = "";
        //ap.user = SystemConfig.UserName;
        String passwd = "";//SystemConfig.PassWord;
        if (passwd != null && passwd.length() > 0) {
            byte[] password = passwd.getBytes(this.conn.charset);
            byte[] seed = hsi.seed;
            byte[] restOfScramble = hsi.restOfScrambleBuff;
            byte[] authSeed = new byte[seed.length + restOfScramble.length];
            System.arraycopy(seed, 0, authSeed, 0, seed.length);
            System.arraycopy(restOfScramble, 0, authSeed, seed.length, restOfScramble.length);
            hsp.password = SecurityUtil.scramble411(password, authSeed);
        }
        // todo config
        hsp.database = "";
        hsp.write(ctx);
    }
    /**
     * 与MySQL连接时的一些特性指定
     */
    private static long getServerCapabilities() {
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
        // flag |= Capabilities.CLIENT_RESERVED;
        flag |= Capabilities.CLIENT_SECURE_CONNECTION;
        // client extension
        // 不允许MULTI协议
        // flag |= Capabilities.CLIENT_MULTI_STATEMENTS;
        // flag |= Capabilities.CLIENT_MULTI_RESULTS;
        return flag;
    }
}
