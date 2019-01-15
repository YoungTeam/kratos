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
 * @ClassName: CommandPacket
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月15日 下午4:57:52
 *
 */
public class CommandPacket extends MySQLPacket {
	   public byte command;
	    public byte[] arg;

	    public void read(byte[] data) {
	        MySQLMsg mm = new MySQLMsg(data);
	        packetLength = mm.readUB3();
	        packetId = mm.read();
	        command = mm.read();
	        arg = mm.readBytes();
	    }

	    public ByteBuf getByteBuf(ChannelHandlerContext ctx){
	        ByteBuf buffer = ctx.alloc().buffer();
	        BufferUtil.writeUB3(buffer, calcPacketSize());
	        buffer.writeByte(packetId);
	        buffer.writeByte(command);
	        buffer.writeBytes(arg);
	        return buffer;
	    }

	    public void write(ChannelHandlerContext ctx) {
	        ctx.writeAndFlush(getByteBuf(ctx));
	    }

	    @Override
	    public int calcPacketSize() {
	        return 1 + arg.length;
	    }

	    @Override
	    protected String getPacketInfo() {
	        return "MySQL Command Packet";
	    }

}
