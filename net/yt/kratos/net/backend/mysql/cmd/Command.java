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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import yt.kratos.mysql.packet.CommandPacket;


/**
 * @ClassName: Command
 * @Description: MySql Command包装类
 * @author YoungTeam
 * @date 2019年1月15日 下午4:43:44
 *
 */
public class Command {
    // command的比特buffer
    private CommandPacket cmdPacket;
    // command的Type
    private CmdType type;
    // sqlType,select|update|set|delete
    private int sqlType;

    public Command() {
    }

    public Command(CommandPacket cmdPacket, CmdType type, int sqlType) {
        this.cmdPacket = cmdPacket;
        this.type = type;
        this.sqlType = sqlType;
    }

    public ByteBuf getCmdByteBuf(ChannelHandlerContext ctx) {
        return cmdPacket.getByteBuf(ctx);
    }

    public CommandPacket getCmdPacket() {
        return cmdPacket;
    }

    public void setCmdPacket(CommandPacket cmdPacket) {
        this.cmdPacket = cmdPacket;
    }

    public CmdType getType() {
        return type;
    }

    public void setType(CmdType type) {
        this.type = type;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

}
