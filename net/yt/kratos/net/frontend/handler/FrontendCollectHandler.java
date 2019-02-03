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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;



/**
 * @ClassName: FrontendCollectHandler
 * @Description: 前端链接收集器
 * @author YoungTeam
 * @date 2019年1月14日 下午2:44:11
 *
 */
public class FrontendCollectHandler extends ChannelInboundHandlerAdapter {
		private static final Logger logger = LoggerFactory.getLogger(FrontendCollectHandler.class);
	  
		public static ConcurrentHashMap<Long, FrontendConnection> FrontendConnectionMap = new ConcurrentHashMap<Long,FrontendConnection>();
		protected FrontendConnection conn;

	    public FrontendCollectHandler(FrontendConnection conn) {
	        this.conn = conn;
	    }
	    
  		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
  			FrontendConnectionMap.put(this.conn.getId(), this.conn);
  			logger.info("connection success! total number is {}", FrontendConnectionMap.size());
		    ctx.fireChannelActive();
		}
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			FrontendConnectionMap.remove(this.conn.getId());
			logger.info("connection failed! total number is {}", FrontendConnectionMap.size());
			this.conn.close();    
		    ctx.fireChannelInactive();
		}

		/**
		* @Title: exceptionCaught
		* @Description: TODO
		* @return void    返回类型
		* @throws
		*/ 
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			// TODO Auto-generated method stub
	        logger.error("Exception caught",cause);
	        FrontendCollectHandler.FrontendConnectionMap.remove(this.conn.getId());
	        this.conn.writeErrMessage(ErrorCode.ERR_EXCEPTION_CAUGHT,cause.getMessage());
	        ctx.close();
		}
	    
}
