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

import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.frontend.FrontendConnection;

/**
 * @ClassName: UseHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月22日 下午11:48:10
 *
 */
public class UseHandler {
	   public static void handle(String sql, FrontendConnection c, int offset) {
	        String schema = sql.substring(offset).trim();
	        int length = schema.length();
	        if (length > 0) {
	            if (schema.charAt(0) == '`' && schema.charAt(length - 1) == '`') {
	                schema = schema.substring(1, length - 2);
	            }
	        }

	        // 表示当前连接已经指定了schema
	        if (c.getSchema() != null) {
	            if (c.getSchema().equals(schema)) {
	                c.writeOk();
	            } else {
	                c.writeErrMessage(ErrorCode.ER_DBACCESS_DENIED_ERROR, "Not allowed to change the database!");
	            }
	            return;
	        }
	        c.setSchema(schema);
	        c.writeOk();

	    }
}
