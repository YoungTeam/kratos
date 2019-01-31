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
package yt.kratos.net.handler;

import java.io.UnsupportedEncodingException;
import java.util.List;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.net.backend.BackendConnection;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.route.RouteResultset;
import yt.kratos.net.session.FrontendSession;


/**
 * @ClassName: ResponseHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月20日 下午2:48:55
 *
 */
public abstract class ResponseHandler {
	protected FrontendSession session;
	
	public ResponseHandler(FrontendSession  session){
		this.session = session;
	}
    //前端传过来的查询请求
    public abstract void execute(RouteResultset rrs) throws UnsupportedEncodingException;
    //处理后端返回的数据
    public abstract boolean handleResponse(BackendConnection conn,BinaryPacket bin);
    
    public void fieldListResponse(List<BinaryPacket> fieldList) {
        writeFiledList(fieldList);
    }
    
    /**
     * 
    * @Title: okResponse
    * @Description: 执行成功Response
    * @return void    返回类型
    * @throws
     */
    protected void okResponse(BinaryPacket bin) {
        OKPacket ok = new OKPacket();
        session.getFrontendConnection().setLastInsertId(ok.insertId);
        bin.write(session.getFrontendConnection().getCtx());
        if (session.getFrontendConnection().isAutocommit()) {
            session.release();
        }
    }
    
    /**
    * @Title: errorResponse
    * @Description: 执行失败Response
    * @return void    返回类型
    * @throws
     */
    protected void errorResponse(BinaryPacket bin) {
        bin.write(session.getFrontendConnection().getCtx());
        if (session.getFrontendConnection().isAutocommit()) {
            session.release();
        }
    }

    /**
     * 
    * @Title: writeFiledList
    * @Description:  返回 字段头 Response
    * @return void    返回类型
    * @throws
     */
    private void writeFiledList(List<BinaryPacket> fieldList) {
        for (BinaryPacket bin : fieldList) {
            bin.write(session.getFrontendConnection().getCtx());
        }
        fieldList.clear();
    }

    /**
     * 
    * @Title: rowResponse
    * @Description: 返回行数据 Response
    * @return void    返回类型
    * @throws
     */
    protected void rowResponse(BinaryPacket bin) {
        bin.write(session.getFrontendConnection().getCtx());
    }

    /**
     * 
    * @Title: lastEofResponse
    * @Description: 返回结束标记 Response
    * @return void    返回类型
    * @throws
     */
    protected void lastEofResponse(BinaryPacket bin) {
        bin.write(session.getFrontendConnection().getCtx());
        if (session.getFrontendConnection().isAutocommit()) {
            session.release();
        }
    }  
    
    
    protected BackendConnection getBackendConnection(RouteResultset rrs) {
        return this.session.getBackendConnection(rrs.getNode());
    }
}
