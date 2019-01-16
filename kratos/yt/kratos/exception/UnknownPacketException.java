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
package yt.kratos.exception;

/**
 * @ClassName: UnknownPacketException
 * @Description: 未知数据包异常
 * @author YoungTeam
 * @date 2019年1月16日 下午6:31:48
 *
 */
public class UnknownPacketException  extends RuntimeException{
    /**
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = -3172890694394286441L;

	public UnknownPacketException() {
        super();
    }

    public UnknownPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPacketException(String message) {
        super(message);
    }

    public UnknownPacketException(Throwable cause) {
        super(cause);
    }
}
