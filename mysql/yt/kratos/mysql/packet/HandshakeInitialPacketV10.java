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

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import yt.kratos.mysql.MySQLMsg;
import yt.kratos.mysql.proto.Capabilities;
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
public class HandshakeInitialPacketV10 extends MySQLPacket{
	private static final byte[] FILLER_10 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static final byte[] DEFAULT_AUTH_PLUGIN_NAME = "mysql_native_password".getBytes();
	
    public byte protocolVersion;
    public byte[] serverVersion;
    public long connectionId;
    public byte[] seed;
    public int serverCapabilities;
    public byte serverCharsetIndex;
    public int serverStatus;
    public byte[] restOfScrambleBuff;
    public byte[] authPluginName = DEFAULT_AUTH_PLUGIN_NAME;
    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
        int size = 1; // protocol version
        size += (serverVersion.length + 1); // server version
        size += 4; // connection id
        size += seed.length;
        size += 1; // [00] filler
        size += 2; // capability flags (lower 2 bytes)
        size += 1; // character set
        size += 2; // status flags
        size += 2; // capability flags (upper 2 bytes)
        size += 1;
        size += 10; // reserved (all [00])
        if((serverCapabilities & Capabilities.CLIENT_SECURE_CONNECTION) != 0) {
        	// restOfScrambleBuff.length always to be 12
        	if(restOfScrambleBuff.length <= 13) {
        		size += 13;
        	} else {
        		size += restOfScrambleBuff.length;
        	}
        }
        if((serverCapabilities & Capabilities.CLIENT_PLUGIN_AUTH) != 0) {
        	size += (authPluginName.length + 1); // auth-plugin name
        }
        return size;
	}

	/*
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
	      return "MySQL HandshakeV10 Packet";
	}

/*    public void read(BinaryPacket bin) {
        packetLength = bin.packetLength;
        packetId = bin.packetId;
        MySQLMsg mm = new MySQLMsg(bin.data);
        protocolVersion = mm.read();
        serverVersion = mm.readBytesWithNull();
        this.connectionId = mm.readUB4();
        seed = mm.readBytesWithNull();
        mm.move(1);
        serverCapabilities = mm.readUB2();
        serverCharsetIndex = mm.read();
        serverStatus = mm.readUB2();
        mm.move(13);
        restOfScrambleBuff = mm.readBytesWithNull();
    }*/
    
    public void write(final ChannelHandlerContext ctx) {
    	final ByteBuf buffer = ctx.alloc().buffer();
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.writeByte(this.packetId);
        buffer.writeByte(protocolVersion);
        BufferUtil.writeWithNull(buffer, serverVersion);
        BufferUtil.writeUB4(buffer, this.connectionId);
        BufferUtil.writeWithNull(buffer, this.seed);
        //buffer.writeByte((byte)0); // [00] filler
        BufferUtil.writeUB2(buffer, serverCapabilities); // capability flags (lower 2 bytes)
        buffer.writeByte(serverCharsetIndex);
        BufferUtil.writeUB2(buffer, serverStatus);
        BufferUtil.writeUB2(buffer, (serverCapabilities >> 16)); // capability flags (upper 2 bytes)
        if((serverCapabilities & Capabilities.CLIENT_PLUGIN_AUTH) != 0) {
        	if(restOfScrambleBuff.length <= 13) {
        		buffer.writeByte((byte) (seed.length + 13));
        	} else {
        		buffer.writeByte((byte) (seed.length + restOfScrambleBuff.length));
        	}
        } else {
        	buffer.writeByte((byte) 0);
        }
        buffer.writeBytes(FILLER_10);
        if((serverCapabilities & Capabilities.CLIENT_SECURE_CONNECTION) != 0) {
        	buffer.writeBytes(restOfScrambleBuff);
        	// restOfScrambleBuff.length always to be 12
        	if(restOfScrambleBuff.length < 13) {
        		for(int i = 13 - restOfScrambleBuff.length; i > 0; i--) {
        			buffer.writeByte((byte)0);
        		}
        	}
        }
        if((serverCapabilities & Capabilities.CLIENT_PLUGIN_AUTH) != 0) {
        	BufferUtil.writeWithNull(buffer, authPluginName);
        }
        ctx.writeAndFlush(buffer);	
    	
/*        // default init 256,so it can avoid buff extract
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
        ctx.writeAndFlush(buffer);*/

    }
}
