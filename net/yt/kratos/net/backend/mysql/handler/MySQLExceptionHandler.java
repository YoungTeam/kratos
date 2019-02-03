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
package yt.kratos.net.backend.mysql.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.net.backend.mysql.MySQLConnection;

/**
 * @ClassName: MySQLExceptionHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年2月3日 下午5:55:59
 *
 */
public class MySQLExceptionHandler extends ChannelHandlerAdapter{
	  private static final Logger logger = LoggerFactory.getLogger(MySQLExceptionHandler.class);

	    protected MySQLConnection conn;

	    public MySQLExceptionHandler(MySQLConnection conn) {
	        this.conn = conn;
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        logger.error("backend exception caught",cause);
	        // discard and close
	        this.conn.discard();
	    }
}
