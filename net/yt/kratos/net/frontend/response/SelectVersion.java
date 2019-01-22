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
package yt.kratos.net.frontend.response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import yt.kratos.config.Versions;
import yt.kratos.mysql.packet.EOFPacket;
import yt.kratos.mysql.packet.FieldPacket;
import yt.kratos.mysql.packet.ResultSetHeaderPacket;
import yt.kratos.mysql.packet.RowDataPacket;
import yt.kratos.mysql.proto.Fields;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.util.PacketUtil;

/**
 * @ClassName: SelectVersion
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月20日 下午2:40:20
 *
 */
public class SelectVersion {
	 private static final int FIELD_COUNT = 1;
	    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
	    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
	    private static final EOFPacket eof = new EOFPacket();
	    static {
	        int i = 0;
	        byte packetId = 0;
	        header.packetId = ++packetId;
	        fields[i] = PacketUtil.getField("VERSION()", Fields.FIELD_TYPE_VAR_STRING);
	        fields[i++].packetId = ++packetId;
	        eof.packetId = ++packetId;
	    }

	    public static void response(FrontendConnection c) {
	        ChannelHandlerContext ctx = c.getCtx();
	        ByteBuf buffer = ctx.alloc().buffer();
	        buffer = header.writeBuf(buffer, ctx);
	        for (FieldPacket field : fields) {
	            buffer = field.writeBuf(buffer, ctx);
	        }
	        buffer = eof.writeBuf(buffer, ctx);
	        byte packetId = eof.packetId;
	        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
	        row.add(Versions.SERVER_VERSION.getBytes());
	        row.packetId = ++packetId;
	        buffer = row.writeBuf(buffer, ctx);
	        EOFPacket lastEof = new EOFPacket();
	        lastEof.packetId = ++packetId;
	        buffer = lastEof.writeBuf(buffer, ctx);
	        ctx.writeAndFlush(buffer);
	    }
}
