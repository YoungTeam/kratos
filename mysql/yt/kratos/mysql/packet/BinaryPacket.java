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
package yt.kratos.mysql.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import yt.kratos.util.BufferUtil;

/**
 * @ClassName: BinaryPacket
 * @Description:  MySql包 外层结构
 * @author YoungTeam
 * @date 2019年1月13日 下午11:29:50
 *
 */
public class BinaryPacket  extends MySQLPacket {
    public static final byte OK = 1;
    public static final byte ERROR = 2;
    public static final byte HEADER = 3;
    public static final byte FIELD = 4;
    public static final byte FIELD_EOF = 5;
    public static final byte ROW = 6;
    public static final byte PACKET_EOF = 7;

    public byte[] data;

    @Override
    public int calcPacketSize() {
        return data == null ? 0 : data.length;
    }

    @Override
    public void write(ChannelHandlerContext ctx) {
        ByteBuf byteBuf = ctx.alloc().buffer();
        BufferUtil.writeUB3(byteBuf, packetLength);
        byteBuf.writeByte(packetId);
        byteBuf.writeBytes(data);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Binary Packet";
    }
}
