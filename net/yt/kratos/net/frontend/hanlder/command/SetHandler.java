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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.mysql.proto.Isolations;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.frontend.response.CharacterSet;
import yt.kratos.parse.ServerParseSet;

/**
 * @ClassName: SetHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月25日 下午6:45:19
 *
 */
public class SetHandler {
	   	private static final Logger logger = LoggerFactory.getLogger(SetHandler.class);
	    private static final byte[] AC_OFF = new byte[] { 7, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };

	    public static void handle(String stmt, FrontendConnection c, int offset) {
	        ChannelHandlerContext ctx = c.getCtx();
	        int rs = ServerParseSet.parse(stmt, offset);
	        switch (rs & 0xff) {
	            case ServerParseSet.AUTOCOMMIT_ON:
	                if (c.isAutocommit()) {
	                	c.writeOk();
	                } else {
	                    c.commit();
	                    c.setAutocommit(true);
	                }
	                break;
	            case ServerParseSet.AUTOCOMMIT_OFF: {
	                if (c.isAutocommit()) {
	                    c.setAutocommit(false);
	                }
	                c.writeOk();
	                break;
	            }
	            case ServerParseSet.TX_READ_UNCOMMITTED: {
	                c.setTxIsolation(Isolations.READ_UNCOMMITTED);
	                c.writeOk();
	                break;
	            }
	            case ServerParseSet.TX_READ_COMMITTED: {
	                c.setTxIsolation(Isolations.READ_COMMITTED);
	                c.writeOk();
	                break;
	            }
	            case ServerParseSet.TX_REPEATED_READ: {
	                c.setTxIsolation(Isolations.REPEATED_READ);
	                c.writeOk();
	                break;
	            }
	            case ServerParseSet.TX_SERIALIZABLE: {
	                c.setTxIsolation(Isolations.SERIALIZABLE);
	                c.writeOk();
	                break;
	            }
	            case ServerParseSet.NAMES:
	                String charset = stmt.substring(rs >>> 8).trim();
	                if (c.setCharset(charset)) {
	                    c.writeOk();
	                } else {
	                    c.writeErrMessage(ErrorCode.ER_UNKNOWN_CHARACTER_SET, "Unknown charset '" + charset + "'");
	                }
	                break;
	            case ServerParseSet.CHARACTER_SET_CLIENT:
	            case ServerParseSet.CHARACTER_SET_CONNECTION:
	            case ServerParseSet.CHARACTER_SET_RESULTS:
	                CharacterSet.response(stmt, c, rs);
	                break;
	            default:
	                StringBuilder s = new StringBuilder();
	                logger.warn(s.append(c).append(stmt).append(" is not executed").toString());
	                c.writeOk();
	        }
	    }
}
