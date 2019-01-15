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
package yt.kratos.net.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.net.AbstractConnection;
import yt.kratos.util.StringUtil;

/**
 * @ClassName: FrontendConnection
 * @Description: 客户端链接类
 * @author YoungTeam
 * @date 2019年1月14日 上午11:14:55
 *
 */
public class FrontendConnection extends AbstractConnection{
	  private static final Logger logger = LoggerFactory.getLogger(FrontendConnection.class);
	
    public void writeErrMessage(int errno, String msg) {
        logger.warn(String.format("[FrontendConnection]ErrorNo=%d,ErrorMsg=%s", errno, msg));
        writeErrMessage((byte) 1, errno, msg);
    }
	
    public void writeErrMessage(byte id, int errno, String msg) {
        ErrorPacket err = new ErrorPacket();
        err.packetId = id;
        err.errno = errno;
        err.message = StringUtil.encodeString(msg, charset);
        err.write(ctx);
    }
    

    

}
