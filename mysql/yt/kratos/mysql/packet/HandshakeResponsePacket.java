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
import yt.kratos.config.Capabilities;
import yt.kratos.mysql.MySQLMsg;
import yt.kratos.util.BufferUtil;

/**
 * From client to server during initial handshake.
 * 
 * <pre>
 * Bytes                        Name
 * -----                        ----
4              			capability flags, CLIENT_PROTOCOL_41 always set
4             			 	max-packet size
1              			character set
string[23]     		reserved (all [0])
string[NUL]    		username

  if capabilities & CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA {

lenenc-int     		length of auth-response
string[n]      		auth-response

  } else if capabilities & CLIENT_SECURE_CONNECTION {

1              			length of auth-response
string[n]      		auth-response
  } else {
string[NUL]    		auth-response
  }
  if capabilities & CLIENT_CONNECT_WITH_DB {
string[NUL]    		database
  }
  if capabilities & CLIENT_PLUGIN_AUTH {
string[NUL]    		auth plugin name
  }
  if capabilities & CLIENT_CONNECT_ATTRS {
lenenc-int    		 length of all key-values
lenenc-str    		 key
lenenc-str    		 value
   if-more data in 'length of all key-values', more keys and value pairs
  }
 * 
 * @see https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse
 * </pre>
 * 
 */

/**
 * @ClassName: HandshakeResponsePacket
 * @Description: Client返回Server的握手response，包含登录信息
 * @author YoungTeam
 * @date 2019年1月13日 下午11:58:18
 *
 */
public class HandshakeResponsePacket extends MySQLPacket{
    private static final byte[] FILLER = new byte[23];

    public long capabilityFlags;
    public long maxPacketSize;
    public int charsetIndex;
    public byte[] extra;// from FILLER(23)
    public String user;
    public byte[] password;
    public String database;
    
    public void read(BinaryPacket bin) {
        packetLength = bin.packetLength;
        packetId = bin.packetId;
        MySQLMsg mm = new MySQLMsg(bin.data);
        this.capabilityFlags = mm.readUB4();
        maxPacketSize = mm.readUB4();
        charsetIndex = (mm.read() & 0xff);
        int current = mm.position();
        int len = (int) mm.readLength();
        if (len > 0 && len < FILLER.length) {
            byte[] ab = new byte[len];
            System.arraycopy(mm.bytes(), mm.position(), ab, 0, len);
            this.extra = ab;
        }
        mm.position(current + FILLER.length);
        user = mm.readStringWithNull();
        password = mm.readBytesWithLength();
        if (((this.capabilityFlags & Capabilities.CLIENT_CONNECT_WITH_DB) != 0) && mm.hasRemaining()) {
            database = mm.readStringWithNull();
        }
    }

    public void write(ChannelHandlerContext ctx) {
        // default init 256,so it can avoid buff extract
        ByteBuf buffer = ctx.alloc().buffer();
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.writeByte(packetId);
        BufferUtil.writeUB4(buffer, this.capabilityFlags);
        BufferUtil.writeUB4(buffer, maxPacketSize);
        buffer.writeByte((byte) charsetIndex);
        buffer.writeBytes(FILLER);
        if (user == null) {
            buffer.writeByte((byte) 0);
        } else {
            byte[] userData = user.getBytes();
            BufferUtil.writeWithNull(buffer, userData);
        }
        if (password == null) {
            buffer.writeByte((byte) 0);
        } else {
            BufferUtil.writeWithLength(buffer, password);
        }
        if (database == null) {
            buffer.writeByte((byte) 0);
        } else {
            byte[] databaseData = database.getBytes();
            BufferUtil.writeWithNull(buffer, databaseData);
        }
        ctx.writeAndFlush(buffer);
    }
    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
		// TODO Auto-generated method stub
        int size = 32;// 4+4+1+23;
        size += (user == null) ? 1 : user.length() + 1;
        size += (password == null) ? 1 : BufferUtil.getLength(password);
        size += (database == null) ? 1 : database.length() + 1;
        return size;
	}
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
		return "MySQL Handshake Response Packet";
	}
}
