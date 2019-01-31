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
import yt.kratos.mysql.packet.EOFPacket;
import yt.kratos.mysql.packet.FieldPacket;
import yt.kratos.mysql.packet.ResultSetHeaderPacket;
import yt.kratos.mysql.packet.RowDataPacket;
import yt.kratos.mysql.proto.Fields;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.util.LongUtil;
import yt.kratos.util.PacketUtil;
import yt.kratos.util.ParseUtil;

/**
 * @ClassName: SelectIdentity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月25日 下午6:18:21
 *
 */
public class SelectIdentity {
	  private static final int FIELD_COUNT = 1;
	    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
	    static {
	        byte packetId = 0;
	        header.packetId = ++packetId;
	    }

	    public static void response(FrontendConnection c, String stmt, int aliasIndex, final String orgName) {
	        ChannelHandlerContext ctx = c.getCtx();
	        String alias = ParseUtil.parseAlias(stmt, aliasIndex);
	        if (alias == null) {
	            alias = orgName;
	        }

	        ByteBuf buffer = ctx.alloc().buffer();

	        // write header
	        buffer = header.writeBuf(buffer, ctx);

	        // write fields
	        byte packetId = header.packetId;
	        FieldPacket field = PacketUtil.getField(alias, orgName, Fields.FIELD_TYPE_LONGLONG);
	        field.packetId = ++packetId;
	        buffer = field.writeBuf(buffer, ctx);

	        // write eof
	        EOFPacket eof = new EOFPacket();
	        eof.packetId = ++packetId;
	        buffer = eof.writeBuf(buffer, ctx);

	        // write rows
	        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
	        row.add(LongUtil.toBytes(c.getLastInsertId()));
	        row.packetId = ++packetId;
	        buffer = row.writeBuf(buffer, ctx);

	        // write last eof
	        EOFPacket lastEof = new EOFPacket();
	        lastEof.packetId = ++packetId;
	        buffer = lastEof.writeBuf(buffer, ctx);

	        // post write
	        ctx.writeAndFlush(buffer);
	    }
}
