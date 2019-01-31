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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicBoolean;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.util.StringUtil;


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
	protected final AtomicBoolean isClosed;
    
    private volatile boolean isRunning;
    
	public AbstractConnection(){
	    this.isClosed = new AtomicBoolean(false);
	    this.isRunning = false;
	}
	
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
	
	public boolean setCharset(String charset) {
		this.charset = charset;
		return true;
	}
	public int getCharsetIndex() {
		return charsetIndex;
	}
	public boolean setCharsetIndex(int charsetIndex) {
		this.charsetIndex = charsetIndex;
		return true;
	}
	
    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }
	
    public boolean isAlive() {
        return this.ctx.channel().isActive();
    }
    
    public void writeOk() {
        ByteBuf byteBuf = ctx.alloc().buffer(OKPacket.OK.length).writeBytes(OKPacket.OK);
        ctx.writeAndFlush(byteBuf);
    }
	
    public void writeErrMessage(byte id, int errno, String msg) {
        ErrorPacket err = new ErrorPacket();
        err.packetId = id;
        err.errno = errno;
        err.message = StringUtil.encode(msg, charset);
        err.write(ctx);
    }	
   
	
	protected  boolean close(){
        if (isClosed.get()) {
            return false;
        } else {
        	this.ctx.close();
        	//if (closeSocket()) {
            return isClosed.compareAndSet(false, true);
            /*} else {
                return false;
            }*/
        }
		  //this.ctx.close();
	}

	protected void ping(BinaryPacket bin) {
		// TODO Auto-generated method stub
		this.writeOk();
	}

	public boolean isClosed() {
        return isClosed.get();
    }
	 
}
