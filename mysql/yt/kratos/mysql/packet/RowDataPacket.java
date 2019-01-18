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

import java.util.ArrayList;
import java.util.List;

import yt.kratos.mysql.MySQLMsg;
import yt.kratos.util.BufferUtil;


/**
 * @ClassName: RowDataPacket
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月18日 下午5:43:16
 *
 */
public class RowDataPacket extends MySQLPacket {
    private static final byte NULL_MARK = (byte) 251;

    public final int fieldCount;
    public final List<byte[]> fieldValues;

    public RowDataPacket(int fieldCount) {
        this.fieldCount = fieldCount;
        this.fieldValues = new ArrayList<byte[]>(fieldCount);
    }

    public void add(byte[] value) {
        fieldValues.add(value);
    }

    public void read(byte[] data) {
        MySQLMsg mm = new MySQLMsg(data);
        packetLength = mm.readUB3();
        packetId = mm.read();
        for (int i = 0; i < fieldCount; i++) {
            fieldValues.add(mm.readBytesWithLength());
        }
    }
  
    @Override
    public void write(ChannelHandlerContext ctx) {
        ByteBuf buffer = ctx.alloc().buffer();
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.writeByte(packetId);
        for (int i = 0; i < fieldCount; i++) {
            byte[] fv = fieldValues.get(i);
            if (fv == null || fv.length == 0) {
                buffer.writeByte(RowDataPacket.NULL_MARK);
            } else {
                BufferUtil.writeLength(buffer, fv.length);
                buffer.writeBytes(fv);
            }
        }
        ctx.writeAndFlush(buffer);
    }

    @Override
    public ByteBuf writeBuf(ByteBuf buffer,ChannelHandlerContext ctx) {
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.writeByte(packetId);
        for (int i = 0; i < fieldCount; i++) {
            byte[] fv = fieldValues.get(i);
            if (fv == null || fv.length == 0) {
                buffer.writeByte(RowDataPacket.NULL_MARK);
            } else {
                BufferUtil.writeLength(buffer, fv.length);
                buffer.writeBytes(fv);
            }
        }
        return buffer;
    }    
    
	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#calcPacketSize()
	*/ 
	@Override
	public int calcPacketSize() {
        int size = 0;
        for (int i = 0; i < fieldCount; i++) {
            byte[] v = fieldValues.get(i);
            size += (v == null || v.length == 0) ? 1 : BufferUtil.getLength(v);
        }
        return size;
	}

	/* 
	* @see yt.kratos.mysql.packet.MySQLPacket#getPacketInfo()
	*/ 
	@Override
	protected String getPacketInfo() {
		// TODO Auto-generated method stub
        return "MySQL RowData Packet";
	}
	
	

}
