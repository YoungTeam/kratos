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
import yt.kratos.mysql.MySQLMsg;
import yt.kratos.util.BufferUtil;

/**
 * @ClassName: ErrorPacket
 * @Description: MySQL 错误命令包
 * @author YoungTeam
 * @date 2019年1月14日 下午5:12:50
 *
 */
public class ErrorPacket  extends MySQLPacket {
	   public static final byte FIELD_COUNT = (byte) 0xff;
	    private static final byte SQLSTATE_MARKER = (byte) '#';
	    private static final byte[] DEFAULT_SQLSTATE = "HY000".getBytes();

	    public byte fieldCount = FIELD_COUNT;
	    public int errno;
	    public byte mark = SQLSTATE_MARKER;
	    public byte[] sqlState = DEFAULT_SQLSTATE;
	    public byte[] message;

		public void read(BinaryPacket bin) {
		    packetLength = bin.packetLength;
		    packetId = bin.packetId;
		    MySQLMsg mm = new MySQLMsg(bin.data);
		    fieldCount = mm.read();
		    errno = mm.readUB2();
		    if (mm.hasRemaining() && (mm.read(mm.position()) == SQLSTATE_MARKER)) {
		        mm.read();
		        sqlState = mm.readBytes(5);
		    }
		    message = mm.readBytes();
		}
		
		public void read(byte[] data) {
			MySQLMsg mm = new MySQLMsg(data);
		    packetLength = mm.readUB3();
		    packetId = mm.read();
		    fieldCount = mm.read();
		    errno = mm.readUB2();
		    if (mm.hasRemaining() && (mm.read(mm.position()) == SQLSTATE_MARKER)) {
		        mm.read();
		        sqlState = mm.readBytes(5);
		    }
		    message = mm.readBytes();
		}
		
		@Override
		public void write(ChannelHandlerContext ctx) {
		    int size = calcPacketSize();
		    // default 256 , no need to check and auto expand
		    ByteBuf buffer = ctx.alloc().buffer();
		    BufferUtil.writeUB3(buffer, size);
		    buffer.writeByte(packetId);
		    buffer.writeByte(fieldCount);
		    BufferUtil.writeUB2(buffer, errno);
		    buffer.writeByte(mark);
		    buffer.writeBytes(sqlState);
		    if (message != null) {
		        buffer.writeBytes(message);
		    }
		    ctx.writeAndFlush(buffer);
		}
	    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
        int size = 9;// 1 + 2 + 1 + 5
        if (message != null) {
            size += message.length;
        }
        return size;
	}

	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
        return "MySQL Error Packet";
	}

}
