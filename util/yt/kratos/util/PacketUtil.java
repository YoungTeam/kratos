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
package yt.kratos.util;

import java.io.UnsupportedEncodingException;

import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.FieldPacket;
import yt.kratos.mysql.packet.ResultSetHeaderPacket;
import yt.kratos.mysql.proto.ErrorCode;


/**
 * @ClassName: PacketUtil
 * @Description: 
 * @author YoungTeam
 * @date 2019年1月18日 下午5:39:32
 *
 */
public class PacketUtil {
	   private static final String CODE_PAGE_1252 = "Cp1252";

	    public static final ResultSetHeaderPacket getHeader(int fieldCount) {
	        ResultSetHeaderPacket packet = new ResultSetHeaderPacket();
	        packet.packetId = 1;
	        packet.fieldCount = fieldCount;
	        return packet;
	    }

	    public static byte[] encode(String src, String charset) {
	        if (src == null) {
	            return null;
	        }
	        try {
	            return src.getBytes(charset);
	        } catch (UnsupportedEncodingException e) {
	            return src.getBytes();
	        }
	    }

	    public static final FieldPacket getField(String name, String orgName, int type) {
	        FieldPacket packet = new FieldPacket();
	        packet.charsetIndex = CharsetUtil.getIndex(CODE_PAGE_1252);
	        packet.name = encode(name, CODE_PAGE_1252);
	        packet.orgName = encode(orgName, CODE_PAGE_1252);
	        packet.type = (byte) type;
	        return packet;
	    }

	    public static final FieldPacket getField(String name, int type) {
	        FieldPacket packet = new FieldPacket();
	        packet.charsetIndex = CharsetUtil.getIndex(CODE_PAGE_1252);
	        packet.name = encode(name, CODE_PAGE_1252);
	        packet.type = (byte) type;
	        return packet;
	    }

	    public static final ErrorPacket getShutdown() {
	        ErrorPacket error = new ErrorPacket();
	        error.packetId = 1;
	        error.errno = ErrorCode.ER_SERVER_SHUTDOWN;
	        error.message = "The server has been shutdown".getBytes();
	        return error;
	    }

	    public static final FieldPacket getField(BinaryPacket src, String fieldName) {
	        FieldPacket field = new FieldPacket();
	        field.read(src);
	        field.name = encode(fieldName, CODE_PAGE_1252);
	        field.packetLength = field.calcPacketSize();
	        return field;
	    }

}
