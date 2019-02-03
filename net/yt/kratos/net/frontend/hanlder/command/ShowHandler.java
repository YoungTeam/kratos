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
import yt.kratos.net.frontend.response.ShowDatabases;
import yt.kratos.net.frontend.response.ShowTables;
import yt.kratos.parse.ServerParse;
import yt.kratos.parse.ServerParseShow;



/**
 * @ClassName: ShowHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月25日 下午6:15:28
 *
 */
public class ShowHandler {
    public static void handle(String stmt, FrontendConnection c, int offset) {
        switch (ServerParseShow.parse(stmt, offset)) {
            case ServerParseShow.DATABASES:
                ShowDatabases.response(c);
                break;
    		case ServerParseShow.TABLES:
    			ShowTables.response(c, stmt);
    			break;
            case ServerParseShow.FULLTABLES:
                //ShowFullTables.response(c, stmt,type);
            	 c.execute(stmt, ServerParse.SHOW);
                break;                
            default:
                c.execute(stmt, ServerParse.SHOW);
                break;
        }
    }
}
