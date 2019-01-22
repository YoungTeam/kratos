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
package yt.kratos.net.frontend;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yt.kratos.mysql.MySQLMsg;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.mysql.proto.ErrorCode;
import yt.kratos.net.AbstractConnection;
import yt.kratos.net.backend.pool.MySQLDataSource;
import yt.kratos.net.handler.QueryHandler;
import yt.kratos.net.session.FrontendSession;
import yt.kratos.util.StringUtil;

/**
 * @ClassName: FrontendConnection
 * @Description: 客户端链接类
 * @author YoungTeam
 * @date 2019年1月14日 上午11:14:55
 *
 */
public class FrontendConnection extends AbstractConnection{
	private static final Logger logger = LoggerFactory.getLogger(FrontendConnection.class);
	
	
	protected String schema;
    protected QueryHandler queryHandler;
	private FrontendSession session;    
    private MySQLDataSource dataSrouce;
	
    public FrontendConnection(){
    	  this.session = new FrontendSession(this);
    }
    
    public QueryHandler getQueryHandler() {
		return queryHandler;
	}

	public void setQueryHandler(QueryHandler queryHandler) {
		this.queryHandler = queryHandler;
	}

	public MySQLDataSource getDataSrouce() {
		return dataSrouce;
	}

	public void setDataSrouce(MySQLDataSource dataSrouce) {
		this.dataSrouce = dataSrouce;
	}

	// initDB的同时 bind BackendConnecton
    public void initDB(BinaryPacket bin) {
        MySQLMsg mm = new MySQLMsg(bin.data);
        // to skip the packet type
        mm.position(1);
        String db = mm.readString();

        // 检查schema是否已经设置
        if (schema != null) {
            if (schema.equals(db)) {
                writeOk();
            } else {
                writeErrMessage(ErrorCode.ER_DBACCESS_DENIED_ERROR, "Not allowed to change the database!");
            }
            return;
        }
        if (db == null) {
            writeErrMessage(ErrorCode.ER_BAD_DB_ERROR, "Unknown database '" + db + "'");
            return;
        } else {
            this.schema = db;
            writeOk();
            return;
        }

    }	
        
    public void query(BinaryPacket bin) {
        if (queryHandler != null) {
            // 取得语句
            MySQLMsg mm = new MySQLMsg(bin.data);
            mm.position(1);
            String sql = null;
            try {
                sql = mm.readString(charset);
            } catch (UnsupportedEncodingException e) {
                writeErrMessage(ErrorCode.ER_UNKNOWN_CHARACTER_SET, "Unknown charset '" + charset + "'");
                return;
            }
            if (sql == null || sql.length() == 0) {
                writeErrMessage(ErrorCode.ER_NOT_ALLOWED_COMMAND, "Empty SQL");
                return;
            }

            // 执行查询
            queryHandler.query(sql);
        } else {
            writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Query unsupported!");
        }
    }    
    
    public void writeOk() {
        ByteBuf byteBuf = ctx.alloc().buffer(OKPacket.OK.length).writeBytes(OKPacket.OK);
        ctx.writeAndFlush(byteBuf);
    }
	
    public void writeErrMessage(int errno, String msg) {
        logger.warn(String.format("[FrontendConnection]ErrorNo=%d,ErrorMsg=%s", errno, msg));
        writeErrMessage((byte) 1, errno, msg);
    }
	
    public void writeErrMessage(byte id, int errno, String msg) {
        ErrorPacket err = new ErrorPacket();
        err.packetId = id;
        err.errno = errno;
        err.message = StringUtil.encodeString(msg, charset);
        err.write(ctx);
    }
    	
    public void close() {
    	  logger.info("close connection:"+this.id);
        //logger.info("close connection,host:{},port:{}", host, port);
        //session.close();
        ctx.close();
    }
    
    public void execute(String sql, int type) {
        if (schema == null) {
            writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "No database selected");
            return;
        } else {
            session.execute(sql, type);
        }
    }
}
