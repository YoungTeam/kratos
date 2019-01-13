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
package yt.kratos.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.util.ByteUtil;


/**
 * @ClassName: MySQLPacketDecoder
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月13日 下午10:48:30
 *
 */
public class MySQLPacketDecoder extends ByteToMessageDecoder{
    private static final Logger logger = LoggerFactory.getLogger(MySQLPacketDecoder.class);

    private final int packetHeaderSize = 4;
    private final int maxPacketSize = 16 * 1024 * 1024;

    /**
     * MySql外层结构解包
     *
     * @param ctx
     * @param in
     * @param out
     *
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 4 bytes:3 length + 1 packetId
        if (in.readableBytes() < packetHeaderSize) {
            return;
        }
        in.markReaderIndex();
        int packetLength = ByteUtil.readUB3(in);
        // 过载保护
        if (packetLength > maxPacketSize) {
            throw new IllegalArgumentException("Packet size over the limit " + maxPacketSize);
        }
        byte packetId = in.readByte();
        if (in.readableBytes() < packetLength) {
            // 半包回溯
            in.resetReaderIndex();
            return;
        }
        
        BinaryPacket packet = new BinaryPacket();
        packet.packetLength = packetLength;
        packet.packetId = packetId;
        // data will not be accessed any more,so we can use this array safely
        //packet.data = in.readBytes(packetLength).array();
        packet.data = new byte[packetLength];
       // byte[] body = new byte[header.getBodyLength()];
        in.readBytes(packet.data);
        
        if (packet.data == null || packet.data.length == 0) {
            logger.error("get data errorMessage,packetLength=" + packet.packetLength);
        }
        out.add(packet);
    }
}
