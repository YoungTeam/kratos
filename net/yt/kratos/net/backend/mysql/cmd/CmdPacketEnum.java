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
package yt.kratos.net.backend.mysql.cmd;

import yt.kratos.mysql.packet.CommandPacket;
import yt.kratos.mysql.packet.MySQLPacket;


/**
 * @ClassName: CmdPacketEnum
 * @Description: 一些常用的不会更改的语句
 * @author YoungTeam
 * @date 2019年1月15日 下午5:00:29
 *
 */
public class CmdPacketEnum {
	 	public static final CommandPacket _READ_UNCOMMITTED = new CommandPacket();
	    public static final CommandPacket _READ_COMMITTED = new CommandPacket();
	    public static final CommandPacket _REPEATED_READ = new CommandPacket();
	    public static final CommandPacket _SERIALIZABLE = new CommandPacket();
	    public static final CommandPacket _AUTOCOMMIT_ON = new CommandPacket();
	    public static final CommandPacket _AUTOCOMMIT_OFF = new CommandPacket();
	    public static final CommandPacket _COMMIT = new CommandPacket();
	    public static final CommandPacket _ROLLBACK = new CommandPacket();

	    static {
	        _READ_UNCOMMITTED.packetId = 0;
	        _READ_UNCOMMITTED.command = MySQLPacket.COM_QUERY;
	        _READ_UNCOMMITTED.arg = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED"
	                .getBytes();
	        _READ_COMMITTED.packetId = 0;
	        _READ_COMMITTED.command = MySQLPacket.COM_QUERY;
	        _READ_COMMITTED.arg = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED".getBytes();
	        _REPEATED_READ.packetId = 0;
	        _REPEATED_READ.command = MySQLPacket.COM_QUERY;
	        _REPEATED_READ.arg = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ".getBytes();
	        _SERIALIZABLE.packetId = 0;
	        _SERIALIZABLE.command = MySQLPacket.COM_QUERY;
	        _SERIALIZABLE.arg = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE".getBytes();
	        _AUTOCOMMIT_ON.packetId = 0;
	        _AUTOCOMMIT_ON.command = MySQLPacket.COM_QUERY;
	        _AUTOCOMMIT_ON.arg = "SET autocommit=1".getBytes();
	        _AUTOCOMMIT_OFF.packetId = 0;
	        _AUTOCOMMIT_OFF.command = MySQLPacket.COM_QUERY;
	        _AUTOCOMMIT_OFF.arg = "SET autocommit=0".getBytes();
	        _COMMIT.packetId = 0;
	        _COMMIT.command = MySQLPacket.COM_QUERY;
	        _COMMIT.arg = "commit".getBytes();
	        _ROLLBACK.packetId = 0;
	        _ROLLBACK.command = MySQLPacket.COM_QUERY;
	        _ROLLBACK.arg = "rollback".getBytes();
	    }
}
