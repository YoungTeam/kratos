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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.Kratos;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.HeartbeatPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.util.TimeUtil;

/**
 * @ClassName: Heartbeat
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月28日 下午2:20:42
 *
 */
public class Heartbeat {
    	private static final Logger logger = LoggerFactory.getLogger("heartbeat");
    	
	    public static void response(FrontendConnection c, byte[] data) {
	        HeartbeatPacket hp = new HeartbeatPacket();
	        hp.read(data);
	        if (Kratos.me.isOnline()) {
	            OKPacket ok = new OKPacket();
	            ok.packetId = 1;
	            ok.affectedRows = hp.id;
	            ok.serverStatus = 2;
	            ok.write(c.getCtx());
	            if (logger.isInfoEnabled()) {
	            	logger.info(responseMessage("OK", c, hp.id));
	            }
	        } else {
	            ErrorPacket error = new ErrorPacket();
	            error.packetId = 1;
	            error.errno = ErrorCode.ER_SERVER_SHUTDOWN;
	            error.message = String.valueOf(hp.id).getBytes();
	            error.write(c.getCtx());
	            if (logger.isInfoEnabled()) {
	            	logger.info(responseMessage("ERROR", c, hp.id));
	            }
	        }
	    }

	    private static String responseMessage(String action, FrontendConnection c, long id) {
	        return new StringBuilder("RESPONSE:").append(action)
	                                             .append(", id=")
	                                             .append(id)
	                                             .append(", host=")
	                                             .append(c.getHost())
	                                             .append(", port=")
	                                             .append(c.getPort())
	                                             .append(", time=")
	                                             .append(TimeUtil.currentTimeMillis())
	                                             .toString();
	    }
}
