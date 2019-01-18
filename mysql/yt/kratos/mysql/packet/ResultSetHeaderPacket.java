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
 * @ClassName: ResultSetHeaderPacket
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月18日 下午5:35:03
 *
 */
public class ResultSetHeaderPacket extends MySQLPacket{
    public int fieldCount;
    public long extra;

    public void read(byte[] data) {
    	MySQLMsg mm = new MySQLMsg(data);
        this.packetLength = mm.readUB3();
        this.packetId = mm.read();
        this.fieldCount = (int) mm.readLength();
        if (mm.hasRemaining()) {
            this.extra = mm.readLength();
        }
    }

    @Override
    public ByteBuf writeBuf(ByteBuf buffer,ChannelHandlerContext ctx) {
        int size = calcPacketSize();
        BufferUtil.writeUB3(buffer, size);
        buffer.writeByte(packetId);
        BufferUtil.writeLength(buffer, fieldCount);
        if (extra > 0) {
            BufferUtil.writeLength(buffer, extra);
        }
        return buffer;
    }
    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
		// TODO Auto-generated method stub
        int size = BufferUtil.getLength(fieldCount);
        if (extra > 0) {
            size += BufferUtil.getLength(extra);
        }
        return size;
	}

	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
		return "MySQL ResultSetHeader Packet";
	}

}
