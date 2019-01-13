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
package yt.kratos.mysql.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import yt.kratos.util.BufferUtil;

/**
 * From server to client during initial handshake.
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 1              			[0a] protocol version
 * string[NUL]    	server version
 * 4              			connection id
 * string[8]      		auth-plugin-data-part-1
 * 1              			[00] filler
 * 2              			capability flags (lower 2 bytes)
 *   if more data in the packet:
 * 1              			character set
 * 2              			status flags
 * 2              			capability flags (upper 2 bytes)
 *   if capabilities & CLIENT_PLUGIN_AUTH {
 * 1              			length of auth-plugin-data
 *  } else {
 * 1              			[00]
 *   }
 * string[10]     	reserved (all [00])
 *   if capabilities & CLIENT_SECURE_CONNECTION {
 * string[$len]   	auth-plugin-data-part-2 ($len=MAX(13, length of auth-plugin-data - 8))
 *   if capabilities & CLIENT_PLUGIN_AUTH {
 * string[NUL]    	auth-plugin name
 *   }
 * @see https://dev.mysql.com/doc/internals/en/initial-handshake.html
 * </pre>
 */

/**
 * @ClassName: HandshakePacket
 * @Description: MySQL建立连接后服务端发向客户端的握手包
 * @author YoungTeam
 * @date 2019年1月11日 下午7:28:36
 *
 */
public class HandshakeInitialPacket extends MySQLPacket{
	 private static final byte[] FILLER_13 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
    public byte protocolVersion;
    public byte[] serverVersion;
    public long connectionId;
    public byte[] seed;
    public int serverCapabilities;
    public byte serverCharsetIndex;
    public int serverStatus;
    public byte[] restOfScrambleBuff;
    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
		int size = 1;
        size += serverVersion.length;// n
        size += 5;// 1+4
        size += seed.length;// 8
        size += 19;// 1+2+1+2+13
        size += restOfScrambleBuff.length;// 12
        size += 1;// 1
        return size;
	}

	/*
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
		return "MySQL Handshake Packet";
	}

    public void write(final ChannelHandlerContext ctx) {
        // default init 256,so it can avoid buff extract
        final ByteBuf buffer = ctx.alloc().buffer();
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.writeByte(this.packetId);
        buffer.writeByte(this.protocolVersion);
        BufferUtil.writeWithNull(buffer, this.serverVersion);
        BufferUtil.writeUB4(buffer, this.connectionId);
        BufferUtil.writeWithNull(buffer, this.seed);
        BufferUtil.writeUB2(buffer, this.serverCapabilities);
        buffer.writeByte(this.serverCharsetIndex);
        BufferUtil.writeUB2(buffer, this.serverStatus);
        buffer.writeBytes(FILLER_13);
        // buffer.position(buffer.position() + 13);
        BufferUtil.writeWithNull(buffer, restOfScrambleBuff);
        // just io , so we don't use thread pool
        ctx.writeAndFlush(buffer);

    }
}
