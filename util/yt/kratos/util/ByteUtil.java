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

import io.netty.buffer.ByteBuf;
import yt.kratos.mysql.MySQLMsg;



/**
 * @ClassName: ByteUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月13日 下午11:28:07
 *
 */
public class ByteUtil {
	   public static int readUB2(ByteBuf data) {
	        int i = data.readByte() & 0xff;
	        i |= (data.readByte() & 0xff) << 8;
	        return i;
	    }

	    public static int readUB3(ByteBuf data) {
	        int i = data.readByte() & 0xff;
	        i |= (data.readByte() & 0xff) << 8;
	        i |= (data.readByte() & 0xff) << 16;
	        return i;
	    }

	    public static long readUB4(ByteBuf data) {
	        long l = data.readByte() & 0xff;
	        l |= (data.readByte() & 0xff) << 8;
	        l |= (data.readByte() & 0xff) << 16;
	        l |= (data.readByte() & 0xff) << 24;
	        return l;
	    }

	    public static long readLong(ByteBuf data) {
	        long l = (long) (data.readByte() & 0xff);
	        l |= (long) (data.readByte() & 0xff) << 8;
	        l |= (long) (data.readByte() & 0xff) << 16;
	        l |= (long) (data.readByte() & 0xff) << 24;
	        l |= (long) (data.readByte() & 0xff) << 32;
	        l |= (long) (data.readByte() & 0xff) << 40;
	        l |= (long) (data.readByte() & 0xff) << 48;
	        l |= (long) (data.readByte() & 0xff) << 56;
	        return l;
	    }

	    /**
	     * this is for the String
	     * @param data
	     * @return
	     */
	    public static long readLength(ByteBuf data) {
	        int length = data.readByte() & 0xff;
	        switch (length) {
	            case 251:
	                return MySQLMsg.NULL_LENGTH;
	            case 252:
	                return readUB2(data);
	            case 253:
	                return readUB3(data);
	            case 254:
	                return readLong(data);
	            default:
	                return length;
	        }
	    }


	    public static int decodeLength(byte[] src) {
	        int length = src.length;
	        if (length < 251) {
	            return 1 + length;
	        } else if (length < 0x10000L) {
	            return 3 + length;
	        } else if (length < 0x1000000L) {
	            return 4 + length;
	        } else {
	            return 9 + length;
	        }
	    }

	    public static int decodeLength(long length) {
	        if (length < 251) {
	            return 1;
	        } else if (length < 0x10000L) {
	            return 3;
	        } else if (length < 0x1000000L) {
	            return 4;
	        } else {
	            return 9;
	        }
	    }
}
