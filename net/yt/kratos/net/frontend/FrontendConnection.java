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
import yt.kratos.net.frontend.response.Ping;
import yt.kratos.net.handler.QueryHandler;
import yt.kratos.net.session.FrontendSession;
import yt.kratos.util.CharsetUtil;
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
    // update by the ResponseHandler
    protected String user;
    protected String host;
    protected int port;
    protected String charset;
    protected int charsetIndex;
    protected QueryHandler queryHandler;
    
	private FrontendSession session;    
	private long lastInsertId;
	
    private volatile int txIsolation;
	private volatile boolean autocommit = true;
	  
    public FrontendConnection(){
    	 super();
    	 this.session = new FrontendSession(this);
    }
    
    public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getCharset() {
		return charset;
	}

	public boolean setCharset(String charset) {
        int ci = CharsetUtil.getIndex(charset);
        if (ci > 0) {
            this.charset = charset;
            this.charsetIndex = ci;
            return true;
        } else {
            return false;
        }
	}

	public int getCharsetIndex() {
		return charsetIndex;
	}

	public boolean setCharsetIndex(int ci) {
        String charset = CharsetUtil.getCharset(ci);
        if (charset != null) {
            this.charset = charset;
            this.charsetIndex = ci;
            return true;
        } else {
            return false;
        }
	}

	public int getTxIsolation() {
		return txIsolation;
	}

	public void setTxIsolation(int txIsolation) {
		this.txIsolation = txIsolation;
	}

	public QueryHandler getQueryHandler() {
		return queryHandler;
	}

	public void setQueryHandler(QueryHandler queryHandler) {
		this.queryHandler = queryHandler;
	}
	
	public long getLastInsertId() {
		return lastInsertId;
	}

	public void setLastInsertId(long lastInsertId) {
		this.lastInsertId = lastInsertId;
	}
	
    public FrontendSession getSession() {
		return session;
	}

	public boolean isAutocommit() {
        return autocommit;
    }

    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
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
    
    @Override
    public void ping(BinaryPacket bin) {
        Ping.response(this);
    }
    
    
	
    public void writeErrMessage(int errno, String msg) {
        logger.warn(String.format("[FrontendConnection]ErrorNo=%d,ErrorMsg=%s", errno, msg));
        writeErrMessage((byte) 1, errno, msg);
    }
    
    /**
     * 提交事务
     */
    public void commit() {
        if (schema == null) {
            writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "No database selected");
            return;
        } else {
            // 如果是自动提交,没有事务,直接返回okay,不做任何操作
            if(isAutocommit()){
                writeOk();
            }else {
                session.commit();
            }
        }
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        if (schema == null) {
            writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "No database selected");
            return;
        } else {
            // 如果是自动提交,没有事务,直接返回okay,不做任何操作
            if (isAutocommit()) {
                writeOk();
            } else {
                session.rollback();

            }
        }
    }
    
	public void kill(byte[] data){
		  writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Unknown command");
	}
    
    public void heartbeat(byte[] data) {
    	this.writeOk();
    }
	
    @Override
    public boolean close() {
    	  logger.info("close connection:"+this.id);
    	  if(super.close()){
    		//todo 这里用线程池释放session
        	  session.terminate();  
        	  return true;
    	  }
    	  
    	  return false;
    }
    
    public void execute(String sql, int type) {
        if (schema == null) {
            writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "No database selected");
            return;
        } else {
            session.execute(sql, type);
        }
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("[thread=")
                                  .append(Thread.currentThread().getName())
                                  .append(",class=")
                                  .append(getClass().getSimpleName())
                                  .append(",host=")
                                  .append(host)
                                  .append(",port=")
                                  .append(port)
                                  .append(",schema=")
                                  .append(schema)
                                  .append(']')
                                  .toString();
    }
}
