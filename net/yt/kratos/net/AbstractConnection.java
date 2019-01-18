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
package yt.kratos.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.net.codec.MySQLPacketDecoder;
import io.netty.channel.ChannelHandlerContext;


/**
 * @ClassName: AbstractConnection
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月14日 下午2:23:33
 *
 */
public abstract class AbstractConnection {
	   
	protected ChannelHandlerContext ctx;
	protected long id; //connection id
	protected String charset;
    protected int charsetIndex;
	 
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public int getCharsetIndex() {
		return charsetIndex;
	}
	public void setCharsetIndex(int charsetIndex) {
		this.charsetIndex = charsetIndex;
	}
	 

	 
}
