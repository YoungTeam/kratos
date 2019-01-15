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
package yt.kratos.net.frontend.handler;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.config.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;


/**
 * @ClassName: FrontendExceptionHandler
 * @Description: FrontendExceptionHandler 做exception的操作
 * @author YoungTeam
 * @date 2019年1月15日 下午8:19:34
 *
 */
public class FrontendExceptionHandler extends ChannelHandlerAdapter{
	  private static final Logger logger = LoggerFactory.getLogger(FrontendExceptionHandler.class);

	    protected FrontendConnection conn;

	    public FrontendExceptionHandler(FrontendConnection conn) {
	        this.conn = conn;
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        logger.error("Exception caught",cause);
	        FrontendCollectHandler.FrontendConnectionMap.remove(this.conn.getId());
	        this.conn.writeErrMessage(ErrorCode.ERR_EXCEPTION_CAUGHT,cause.getMessage());
	        ctx.close();
	    }
}
