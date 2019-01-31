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
package yt.kratos.net.frontend.hanlder.command;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLNonTransientException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.packet.EOFPacket;
import yt.kratos.mysql.packet.FieldPacket;
import yt.kratos.mysql.packet.ResultSetHeaderPacket;
import yt.kratos.mysql.packet.RowDataPacket;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.mysql.proto.Fields;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.route.RouteResultset;
import yt.kratos.net.route.RouteResultsetNode;
import yt.kratos.util.PacketUtil;
import yt.kratos.util.StringUtil;

/**
 * @ClassName: ExplainHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月25日 下午6:39:15
 *
 */
public class ExplainHandler {
	  private static final Logger logger = LoggerFactory.getLogger(ExplainHandler.class);
	    private static final RouteResultsetNode[] EMPTY_ARRAY = new RouteResultsetNode[0];
	    private static final int FIELD_COUNT = 2;
	    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
	    static {
	        fields[0] = PacketUtil.getField("DATA_NODE", Fields.FIELD_TYPE_VAR_STRING);
	        fields[1] = PacketUtil.getField("SQL", Fields.FIELD_TYPE_VAR_STRING);
	    }

	    public static void handle(String stmt, FrontendConnection c, int offset) {
	/*        ChannelHandlerContext ctx = c.getCtx();
	        ByteBuf buffer = ctx.alloc().buffer();
	        
	        stmt = stmt.substring(offset);

	        RouteResultset rrs = getRouteResultset(c, stmt);
	        if (rrs == null)
	            return;

	        // write header
	        ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
	        byte packetId = header.packetId;
	        buffer = header.writeBuf(buffer, ctx);

	        // write fields
	        for (FieldPacket field : fields) {
	            field.packetId = ++packetId;
	            buffer = field.writeBuf(buffer, ctx);
	        }

	        // write eof
	        EOFPacket eof = new EOFPacket();
	        eof.packetId = ++packetId;
	        buffer = eof.writeBuf(buffer, ctx);

	        // write rows
	        RouteResultsetNode[] rrsn = (rrs != null) ? rrs.getNodes() : EMPTY_ARRAY;
	        for (RouteResultsetNode node : rrsn) {
	            RowDataPacket row = getRow(node, c.getCharset());
	            row.packetId = ++packetId;
	            buffer = row.writeBuf(buffer, ctx);
	        }

	        // write last eof
	        EOFPacket lastEof = new EOFPacket();
	        lastEof.packetId = ++packetId;
	        buffer = lastEof.writeBuf(buffer, ctx);

	        // post write
	        c.write(buffer);*/

	    }

	    private static RowDataPacket getRow(RouteResultsetNode node, String charset) {
	        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
	        row.add(StringUtil.encode(node.getName(), charset));
	        row.add(StringUtil.encode(node.getStatement(), charset));
	        return row;
	    }

	    private static RouteResultset getRouteResultset(FrontendConnection c, String stmt) {
	    /*    String db = c.getSchema();
	        if (db == null) {
	            c.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "No database selected");
	            return null;
	        }
	        SchemaConfig schema = CobarServer.getInstance().getConfig().getSchemas().get(db);
	        if (schema == null) {
	            c.writeErrMessage(ErrorCode.ER_BAD_DB_ERROR, "Unknown database '" + db + "'");
	            return null;
	        }
	        try {
	            return ServerRouter.route(schema, stmt, c.getCharset(), c);
	        } catch (SQLNonTransientException e) {
	            StringBuilder s = new StringBuilder();
	            logger.warn(s.append(c).append(stmt).toString(), e);
	            String msg = e.getMessage();
	            c.writeErrMessage(ErrorCode.ER_PARSE_ERROR, msg == null ? e.getClass().getSimpleName() : msg);
	            return null;
	        }*/
	    	return null;
	    }
}
