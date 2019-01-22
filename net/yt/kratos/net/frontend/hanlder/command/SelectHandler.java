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
import yt.kratos.net.frontend.response.SelectVersion;
import yt.kratos.net.frontend.response.SelectVersionComment;
import yt.kratos.parse.ServerParse;
import yt.kratos.parse.ServerParseSelect;
import yt.kratos.util.ParseUtil;

/**
 * @ClassName: SelectHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月17日 下午6:24:18
 *
 */
public class SelectHandler {
	 public static void handle(String stmt, FrontendConnection c, int offs) {
	        int offset = offs;
	        switch (ServerParseSelect.parse(stmt, offs)) {
	            case ServerParseSelect.VERSION_COMMENT:
	                SelectVersionComment.response(c);
	                break;
	            case ServerParseSelect.DATABASE:
	                //SelectDatabase.response(c);
	                break;
	            case ServerParseSelect.USER:
	                //SelectUser.response(c);
	                break;
	            case ServerParseSelect.VERSION:
	                SelectVersion.response(c);
	                break;
	            case ServerParseSelect.LAST_INSERT_ID:
	                offset = ParseUtil.move(stmt, 0, "select".length());
	                loop: for (; offset < stmt.length(); ++offset) {
	                    switch (stmt.charAt(offset)) {
	                        case ' ':
	                            continue;
	                        case '/':
	                        case '#':
	                            offset = ParseUtil.comment(stmt, offset);
	                            continue;
	                        case 'L':
	                        case 'l':
	                            break loop;
	                    }
	                }
	                offset = ServerParseSelect.indexAfterLastInsertIdFunc(stmt, offset);
	                offset = ServerParseSelect.skipAs(stmt, offset);
	                //SelectLastInsertId.response(c, stmt, offset);
	                break;
	            case ServerParseSelect.IDENTITY:
	                offset = ParseUtil.move(stmt, 0, "select".length());
	                loop: for (; offset < stmt.length(); ++offset) {
	                    switch (stmt.charAt(offset)) {
	                        case ' ':
	                            continue;
	                        case '/':
	                        case '#':
	                            offset = ParseUtil.comment(stmt, offset);
	                            continue;
	                        case '@':
	                            break loop;
	                    }
	                }
	                int indexOfAtAt = offset;
	                offset += 2;
	                offset = ServerParseSelect.indexAfterIdentity(stmt, offset);
	                String orgName = stmt.substring(indexOfAtAt, offset);
	                offset = ServerParseSelect.skipAs(stmt, offset);
	                //SelectIdentity.response(c, stmt, offset, orgName);
	                break;
	            default:
	                c.execute(stmt, ServerParse.SELECT);
	                break;
	        }
	    }

}
