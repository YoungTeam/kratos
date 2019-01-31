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

import io.netty.channel.ChannelHandlerContext;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.frontend.handler.FrontendCollectHandler;
import yt.kratos.util.StringUtil;

/**
 * @ClassName: KillHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月25日 下午7:18:17
 *
 */
public class KillHandler {
	   public static void handle(String stmt, int offset, FrontendConnection c) {
	        ChannelHandlerContext ctx = c.getCtx();
	        String id = stmt.substring(offset).trim();
	        if (StringUtil.isEmpty(id)) {
	            c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "NULL connection id");
	        } else {
	            // get value
	            long value = 0;
	            try {
	                value = Long.parseLong(id);
	            } catch (NumberFormatException e) {
	                c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "Invalid connection id:" + id);
	                return;
	            }

	            // kill myself
	            if (value == c.getId()) {
	                getOkPacket().write(ctx);
	                return;
	            }

	            // get connection and close it
	            FrontendConnection fc = FrontendCollectHandler.FrontendConnectionMap.get(value);

	            if (fc != null) {
	                fc.close();
	                getOkPacket().write(ctx);
	            } else {
	                c.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD, "Unknown connection id:" + id);
	            }
	        }
	    }

	    private static OKPacket getOkPacket() {
	        OKPacket packet = new OKPacket();
	        packet.packetId = 1;
	        packet.affectedRows = 0;
	        packet.serverStatus = 2;
	        return packet;
	    }
}
