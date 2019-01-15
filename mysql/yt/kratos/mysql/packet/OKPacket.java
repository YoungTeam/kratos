/*
 * Copyright 2019 YoungTeam@Sogou Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import yt.kratos.mysql.MySQLMsg;
import yt.kratos.util.BufferUtil;


/**
 * @ClassName: OKPacket
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月14日 下午11:28:53
 *
 */
public class OKPacket extends MySQLPacket {
	 public static final byte FIELD_COUNT = 0x00;

	public static final byte[] OK = new byte[] { 7, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0 };
	public static final byte[] AUTH_OK = new byte[] { 7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0 };
	public byte fieldCount = FIELD_COUNT;
	public long affectedRows;
	public long insertId;
	public int serverStatus;
	public int warningCount;
	public byte[] message;

	public void read(BinaryPacket bin) {
		packetLength = bin.packetLength;
		packetId = bin.packetId;
		MySQLMsg mm = new MySQLMsg(bin.data);
		fieldCount = mm.read();
		affectedRows = mm.readLength();
		insertId = mm.readLength();
		serverStatus = mm.readUB2();
		warningCount = mm.readUB2();
		if (mm.hasRemaining()) {
			this.message = mm.readBytesWithLength();
		}
	}
			
	public void write(ChannelHandlerContext ctx) {
		// default init 256,so it can avoid buff extract
		ByteBuf buffer = ctx.alloc().buffer();
		BufferUtil.writeUB3(buffer, calcPacketSize());
		buffer.writeByte(packetId);
		buffer.writeByte(fieldCount);
		BufferUtil.writeLength(buffer, affectedRows);
		BufferUtil.writeLength(buffer, insertId);
		BufferUtil.writeUB2(buffer, serverStatus);
		BufferUtil.writeUB2(buffer, warningCount);
		if (message != null) {
			BufferUtil.writeWithLength(buffer, message);
		}
		ctx.writeAndFlush(buffer);
	}
	
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
        int i = 1;
        i += BufferUtil.getLength(affectedRows);
        i += BufferUtil.getLength(insertId);
        i += 4;
        if (message != null) {
            i += BufferUtil.getLength(message);
        }
        return i;
	}

	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
	     return "MySQL OK Packet";
	}

}
