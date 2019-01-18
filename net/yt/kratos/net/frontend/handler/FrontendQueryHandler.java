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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.frontend.hanlder.command.SelectHandler;
import yt.kratos.net.handler.QueryHandler;
import yt.kratos.parse.ServerParse;

/**
 * @ClassName: FrontendQueryHandler
 * @Description: 前端查询请求处理Handler
 * @author YoungTeam
 * @date 2019年1月17日 下午6:12:39
 *
 */
public class FrontendQueryHandler implements QueryHandler{
	 private static final Logger logger = LoggerFactory.getLogger(FrontendQueryHandler.class);

	    private final FrontendConnection conn;

	    public FrontendQueryHandler(FrontendConnection conn) {
	        this.conn = conn;
	    }

	/* 
	* @see yt.kratos.net.handler.QueryHandler#query(java.lang.String)
	*/ 
	@Override
	public void query(String sql) {
		// TODO Auto-generated method stub
		FrontendConnection c = this.conn;
        if (logger.isDebugEnabled()) {
        	logger.debug(new StringBuilder().append(c).append(sql).toString());
        }
        
        int rs = ServerParse.parse(sql);
        switch (rs & 0xff) {
        case ServerParse.EXPLAIN:
            //ExplainHandler.handle(sql, c, rs >>> 8);
            break;
        case ServerParse.SET:
            //SetHandler.handle(sql, c, rs >>> 8);
            break;
        case ServerParse.SHOW:
            //ShowHandler.handle(sql, c, rs >>> 8);
            break;
        case ServerParse.SELECT:
            SelectHandler.handle(sql, c, rs >>> 8);
            break;
        case ServerParse.START:
            //StartHandler.handle(sql, c, rs >>> 8);
            break;
        case ServerParse.BEGIN:
            //BeginHandler.handle(sql, c);
            break;
        case ServerParse.SAVEPOINT:
            //SavepointHandler.handle(sql, c);
            break;
        case ServerParse.KILL:
            //KillHandler.handle(sql, rs >>> 8, c);
            break;
        case ServerParse.KILL_QUERY:
            c.writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Unsupported command");
            break;
        case ServerParse.USE:
            //UseHandler.handle(sql, c, rs >>> 8);
            break;
        case ServerParse.COMMIT:
            //c.commit();
            break;
        case ServerParse.ROLLBACK:
            //c.rollback();
            break;
        default:
            //c.execute(sql, rs);
        }		
	}

}
