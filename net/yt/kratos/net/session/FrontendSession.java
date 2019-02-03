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
package yt.kratos.net.session;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.Kratos;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.backend.BackendConnection;
import yt.kratos.net.backend.mysql.handler.response.MySQLNodeHandler;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.handler.ResponseHandler;
import yt.kratos.net.route.RouteResultset;
import yt.kratos.net.route.RouteResultsetNode;

/**
 * @ClassName: MySQLSession
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月17日 下午5:57:52
 *
 */
public class FrontendSession implements Session{
	  
    private static final Logger logger = LoggerFactory.getLogger(FrontendSession.class);
    
    // 事务是否被打断
    private volatile boolean txInterrupted;
    // 被打断保存的信息
    private volatile String txInterrputMsg = "";
    
	private FrontendConnection conn;
	
	private ResponseHandler responseHandler;
	
	//路由与后端链接map
	private final ConcurrentHashMap<RouteResultsetNode, BackendConnection> target;
	
	public FrontendSession(FrontendConnection conn){
		this.conn = conn;
		this.responseHandler = new MySQLNodeHandler(this);
		this.target = new ConcurrentHashMap<RouteResultsetNode, BackendConnection>();
	}
	
	/* 
	* @see yt.kratos.net.session.Session#getConnection()
	*/ 
	@Override
	public FrontendConnection getFrontendConnection() {
		// TODO Auto-generated method stub
		return this.conn;
	}

	/* 
	* @see yt.kratos.net.session.Session#getTargetCount()
	*/ 
	@Override
	public int getTargetCount() {
		// TODO Auto-generated method stub
		return 0;
	}

    public void execute(String sql, int type) {
        // 状态检查
        if (this.txInterrupted) {
            this.conn.writeErrMessage(ErrorCode.ER_YES, "Transaction errorMessage, need to rollback." + txInterrputMsg);
            return;
        }
        
        RouteResultset rrs = route(this.conn.getSchema(),sql, type);
        try {
			this.responseHandler.execute(rrs);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
       // this.responseHandler.execute(rrs);
        
/*        RouteResultset rrs = route(sql, type);
        if (rrs.getNodeCount() == 0) {
            writeErrMessage(ErrorCode.ER_PARSE_ERROR, "parse sql and 0 node get");
            return;
        } else if (rrs.getNodeCount() == 1) {
            responseHandler = singleNodeExecutor;
            singleNodeExecutor.execute(rrs);
        } else {
            responseHandler = multiNodeExecutor;
            multiNodeExecutor.execute(rrs);
        }*/
    }
	
    private RouteResultset route(String schema,String sql, int type) {
        RouteResultset routeResultset = null;//LancelotStmtParser.parser(sql,type);
        if(routeResultset == null){
            routeResultset = new RouteResultset();
            //RouteResultsetNode nodes = new RouteResultsetNode();
            RouteResultsetNode node = new RouteResultsetNode("1",sql,type);
            routeResultset.setNode(node);
        }
        return routeResultset;
    }
    
    public BackendConnection getBackendConnection(RouteResultsetNode key) {
        BackendConnection backend = target.get(key);
        if (backend == null) {
        	
            backend = Kratos.DataSource().get(this.conn.getSchema()).getDataPool().getBackendConnection();//  this.conn.getDataSource().getDataPool().getBackendConnection();
            backend.setSession(this);//后端链接绑定Session
            target.put(key, backend);
            logger.info("Get BackendConnection"+backend);
        }
        return backend;
    }    

    
	/* 
	* @see yt.kratos.net.session.Session#commit()
	*/ 
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.session.Session#rollback()
	*/ 
	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.session.Session#cancel(yt.kratos.net.frontend.FrontendConnection)
	*/ 
	@Override
	public void cancel(FrontendConnection frontConn) {
		// TODO Auto-generated method stub
		
	}

	/* 
	* @see yt.kratos.net.session.Session#terminate()
	*/ 
	@Override
	public void terminate() {
		// TODO Auto-generated method stub
        // 终止所有正在执行的任务
        //kill();

        // 等待所有任务结束，包括还未执行的，执行中的，执行完的。
/*        try {
            singleNodeExecutor.terminate();
            multiNodeExecutor.terminate();
            commitExecutor.terminate();
            rollbackExecutor.terminate();
        } catch (InterruptedException e) {
            for (RouteResultsetNode rrn : target.keySet()) {
                Channel c = target.remove(rrn);
                if (c != null) {
                    c.close();
                }
            }
            
            logger.warn("termination interrupted: " + this.conn, e);
        }*/

        // 清理绑定的资源
        //clear(false);
	}
	
	private void clear(){
		//释放可能这一次前端请求关联的后端多个节点全部释放（目前我们其实是1对1）
		for(Iterator it = this.target.keySet().iterator();it.hasNext();){
			RouteResultsetNode rrn = (RouteResultsetNode)it.next();
			BackendConnection backConn = target.remove(rrn); //清空链接组
			
			if(backConn == null || backConn.isClosed()){
				continue;
			}
			
		//	if(backConn.is)
			
		}
		//this.target = 
	}

    public ResponseHandler getResponseHandler() {
        return this.responseHandler;
    }

	/* 
	* @see yt.kratos.net.session.Session#release()
	*/ 
	@Override
	public void release() {
		// TODO Auto-generated method stub
        for(BackendConnection backend : target.values()){
            // recycle本身做了alive判断
            backend.recycle();
        }
        logger.debug("session has been released");
        target.clear();
	}
}
