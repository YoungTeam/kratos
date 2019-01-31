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
 * From client to server when the client do heartbeat between cobar cluster.
 * 
 * <pre>
 * Bytes         Name
 * -----         ----
 * 1             command
 * n             id
 * 
 * @ClassName: HeartbeatPacket
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月28日 下午2:26:13
 *
 */
public class HeartbeatPacket extends MySQLPacket {
	 public byte command;
	    public long id;

	    public void read(byte[] data) {
	        MySQLMsg mm = new MySQLMsg(data);
	        packetLength = mm.readUB3();
	        packetId = mm.read();
	        command = mm.read();
	        id = mm.readLength();
	    }

	    @Override
	    public void write(ChannelHandlerContext ctx) {
	        int size = calcPacketSize();
		    ByteBuf buffer = ctx.alloc().buffer();
		    BufferUtil.writeUB3(buffer, size);
		    buffer.writeByte(packetId);
		    buffer.writeByte(command);
		    BufferUtil.writeLength(buffer, id);
		    ctx.writeAndFlush(buffer);
	    }

	    @Override
	    public int calcPacketSize() {
	        return 1 + BufferUtil.getLength(id);
	    }

	    @Override
	    protected String getPacketInfo() {
	        return "Kratos Heartbeat Packet";
	    }
}
