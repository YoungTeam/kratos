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
package yt.kratos.net.backend;

import io.netty.channel.Channel;
import yt.kratos.net.AbstractConnection;
import yt.kratos.net.session.Session;

/**
 * @ClassName: BackendConnection
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月17日 下午5:46:06
 *
 */
public abstract class BackendConnection extends AbstractConnection{
    
    protected Session session;
    protected Channel ch;
    public BackendConnection(){
    	super();
    }
    
	public Session getSession() {
		return session;
	}

	public Channel getCh() {
		return ch;
	}

	public void setCh(Channel ch) {
		this.ch = ch;
	}

	public  abstract void setSession(Session session);

	
	public abstract void recycle();
	
    @Override
    public String toString() {
        return new StringBuilder().append("[thread=")
                                  .append(Thread.currentThread().getName())
                                  .append(",class=")
                                  .append(getClass().getSimpleName())
                                  .append(",ch=")
                                  .append(ch.id())
                                  .append(']')
                                  .toString();
    }
}
