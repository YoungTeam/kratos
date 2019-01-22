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


/**
 * @ClassName: RouteResultset
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月20日 下午11:56:51
 *
 */
public class RouteResultset {
	 private String statement; // 原始语句
	    private int sqlType;
	    private RouteResultsetNode node; // 路由数据结点

	    public String getStatement() {
	        return statement;
	    }

/*	    public int getNodeCount(){
	        if(nodes == null){
	            return 0;
	        }else{
	            return nodes.length;
	        }
	    }*/

	    public void setStatement(String statement) {
	        this.statement = statement;
	    }

	    public int getSqlType() {
	        return sqlType;
	    }

	    public void setSqlType(int sqlType) {
	        this.sqlType = sqlType;
	    }

	    public RouteResultsetNode getNode() {
	        return node;
	    }

	    public void setNode(RouteResultsetNode node) {
	        this.node = node;
	    }
}
