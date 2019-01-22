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
package yt.kratos.net.handler.node;

import java.util.List;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.net.backend.BackendConnection;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.handler.ResponseHandler;
import yt.kratos.net.route.RouteResultset;
import yt.kratos.net.route.RouteResultsetNode;
import yt.kratos.net.session.FrontendSession;

/**
 * @ClassName: MySQLNodeHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月20日 下午11:45:04
 *
 */
public class MySQLNodeHandler implements ResponseHandler{

	FrontendSession session;
	public MySQLNodeHandler(FrontendSession session){
		this.session = session;
	}
	
	/* 
	* @see yt.kratos.net.handler.ResponseHandler#execute(yt.kratos.net.handler.RouteResultset)
	*/ 
	@Override
	public void execute(RouteResultset rrs) {
		// TODO Auto-generated method stub
/*	     if (rrs.getNodes() == null || rrs.getNodes().length == 0) {
	            session.writeErrMessage(ErrorCode.ERR_SINGLE_EXECUTE_NODES, "SingleNode executes no nodes");
	            return;
	        }
	        if (rrs.getNodes().length > 1) {
	            session.writeErrMessage(ErrorCode.ERR_SINGLE_EXECUTE_NODES, "SingleNode executes too many nodes");
	            return;
	        }*/
	        // 当前RouteResultset对应的Backend
	        BackendConnection backend = getBackend(rrs);
	        RouteResultsetNode node = rrs.getNodes()[0];
	        
	       // Command command = session.getConnection().get.getSource().getFrontendCommand(node.getStatement(), node.getSqlType());
	        backend.postCommand(command);
	        // fire it
	        backend.fireCmd();
	}

	/* 
	* @see yt.kratos.net.handler.ResponseHandler#fieldListResponse(java.util.List)
	*/ 
	@Override
	public void fieldListResponse(List<BinaryPacket> fieldList) {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.handler.ResponseHandler#errorResponse(yt.kratos.mysql.packet.BinaryPacket)
	*/ 
	@Override
	public void errorResponse(BinaryPacket bin) {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.handler.ResponseHandler#okResponse(yt.kratos.mysql.packet.BinaryPacket)
	*/ 
	@Override
	public void okResponse(BinaryPacket bin) {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.handler.ResponseHandler#rowResponse(yt.kratos.mysql.packet.BinaryPacket)
	*/ 
	@Override
	public void rowResponse(BinaryPacket bin) {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.handler.ResponseHandler#lastEofResponse(yt.kratos.mysql.packet.BinaryPacket)
	*/ 
	@Override
	public void lastEofResponse(BinaryPacket bin) {
		// TODO Auto-generated method stub
		
	}

	
    private MySQLConnection getMySQLConnection(RouteResultset rrs) {
        return session.getTarget(rrs.getNode());
    }


}
