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
package yt.kratos.net.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: RouteResultsetNode
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月21日 下午4:06:54
 *
 */
public class RouteResultsetNode {
    private static final Logger logger = LoggerFactory.getLogger(RouteResultsetNode.class);

    private  String name; // 数据节点名称,对每一个连接 需唯一
    private  String statement; // 执行的语句
    private  int sqlType;

    public RouteResultsetNode(String name, String statement, int sqlType) {
        this.name = name;
        this.statement = statement;
        this.sqlType = sqlType;
    }

    public String getName() {
        return name;
    }

    public String getStatement() {
        return statement;
    }

    public int getSqlType() {
        return sqlType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RouteResultsetNode) {
            RouteResultsetNode rrn = (RouteResultsetNode) obj;
            if (equals(name, rrn.getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
