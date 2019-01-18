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
 * @ClassName: EOFPacket
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月18日 下午5:42:59
 *
 */
public class EOFPacket extends MySQLPacket {
	   public static final byte FIELD_COUNT = (byte) 0xfe;

	    public byte fieldCount = FIELD_COUNT;
	    public int warningCount;
	    public int status = 2;

	    public void read(byte[] data) {
	        MySQLMsg mm = new MySQLMsg(data);
	        packetLength = mm.readUB3();
	        packetId = mm.read();
	        fieldCount = mm.read();
	        warningCount = mm.readUB2();
	        status = mm.readUB2();
	    }

	    public void read(BinaryPacket bin){
	        packetLength = bin.packetLength;
	        packetId = bin.packetId;
	        MySQLMsg mm = new MySQLMsg(bin.data);
	        fieldCount = mm.read();
	        warningCount = mm.readUB2();
	        status = mm.readUB2();
	    }
	    
	    
	    @Override
	    public ByteBuf writeBuf(ByteBuf buffer,ChannelHandlerContext ctx) {
	        int size = calcPacketSize();
	        BufferUtil.writeUB3(buffer, size);
	        buffer.writeByte(packetId);
	        buffer.writeByte(fieldCount);
	        BufferUtil.writeUB2(buffer, warningCount);
	        BufferUtil.writeUB2(buffer, status);
	        return buffer;
	    }

	    public boolean hasStatusFlag(long flag) {
	        return ((this.status & flag) == flag);
	    }	    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
		// TODO Auto-generated method stub
		return 5;// 1+2+2;
	}

	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
		  return "MySQL EOF Packet";
	}

}
