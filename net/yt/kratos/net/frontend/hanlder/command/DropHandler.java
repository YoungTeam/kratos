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
package yt.kratos.net.frontend.hanlder.command;

import yt.kratos.net.frontend.FrontendConnection;
import yt.kratos.net.frontend.response.DropSchema;
import yt.kratos.parse.ServerParseDrop;

/**
 * @ClassName: DropHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年2月1日 下午2:45:12
 *
 */
public class DropHandler {
	 public static void handle(String stmt, FrontendConnection c, int offset) {
		 
	        switch (ServerParseDrop.parse(stmt, offset)) {
	            case ServerParseDrop.DATABASE:
	                //ShowDatabases.response(c);
	                break;
	    		case ServerParseDrop.TABLE:
	    			//ShowTables.response(c, stmt);
	    			break;         
	    		case ServerParseDrop.SCHEMA:
	    			DropSchema.response(c,stmt);
	    			break;        
	            default:
	            	//ShowDatabases.response(c);
	                //c.execute(stmt, ServerParse.SHOW);
	                break;
	        }
	    }
}
