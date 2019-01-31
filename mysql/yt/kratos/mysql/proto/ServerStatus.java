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
package yt.kratos.mysql.proto;

/**
 * @ClassName: ServerStatus
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月25日 上午11:34:09
 *
 */
public interface ServerStatus {
	
	/**
	 * a transaction is active
	 */
	public static final int SERVER_STATUS_IN_TRANS = 1;
	
	/**
	 *  auto-commit is enabled
	 */
	public static final int SERVER_STATUS_AUTOCOMMIT = 2;	
	
    /**
     * MORE RESULTS
     */
    public static final int SERVER_MORE_RESULTS_EXISTS=8;
    
    public static final int SERVER_STATUS_NO_GOOD_INDEX_USED=1;
    
    public static final int SERVER_STATUS_NO_INDEX_USED=20;
}
