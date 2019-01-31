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
package yt.kratos.net.backend.mysql.handler.response;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.kratos.exception.ErrorPacketException;
import yt.kratos.exception.UnknownPacketException;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.CommandPacket;
import yt.kratos.mysql.packet.EOFPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.MySQLPacket;
import yt.kratos.mysql.packet.OKPacket;
import yt.kratos.mysql.proto.ServerStatus;
import yt.kratos.net.backend.BackendConnection;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.MySQLConnectionState;
import yt.kratos.net.backend.mysql.cmd.CmdType;
import yt.kratos.net.backend.mysql.cmd.Command;
import yt.kratos.net.handler.ResponseHandler;
import yt.kratos.net.route.RouteResultset;
import yt.kratos.net.route.RouteResultsetNode;
import yt.kratos.net.session.FrontendSession;

/**
 * @ClassName: MySQLNodeHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月20日 下午11:45:04
 *
 */
public class MySQLNodeHandler extends ResponseHandler{
    private static final Logger logger = LoggerFactory.getLogger(MySQLNodeHandler.class);
    
	private RouteResultset rrs;
    private volatile int selectState;
    // 保存fieldCount,field以及fieldEof信息
    private List<BinaryPacket> fieldList;

	public MySQLNodeHandler(FrontendSession session){
		super(session);
        this.selectState = MySQLConnectionState.RESULT_SET_FIELD_COUNT;
        this.fieldList = new LinkedList<BinaryPacket>();
	}
	
	/* 
	* @see yt.kratos.net.handler.ResponseHandler#execute(yt.kratos.net.handler.RouteResultset)
	*/ 
	@Override
	public void execute(RouteResultset rrs) throws UnsupportedEncodingException{
		// TODO Auto-generated method stub
/*	     if (rrs.getNodes() == null || rrs.getNodes().length == 0) {
	            session.writeErrMessage(ErrorCode.ERR_SINGLE_EXECUTE_NODES, "SingleNode executes no nodes");
	            return;
	        }
	        if (rrs.getNodes().length > 1) {
	            session.writeErrMessage(ErrorCode.ERR_SINGLE_EXECUTE_NODES, "SingleNode executes too many nodes");
	            return;
	        }*/
	        // 当前RouteResultset对应的Backend
		
			this.rrs = rrs;
	        MySQLConnection backend = (MySQLConnection)this.getBackendConnection(rrs);
	        backend.setRunning(true);
	        RouteResultsetNode node = rrs.getNode();
            CommandPacket packet = new CommandPacket();
            packet.packetId = 0;
            packet.command = MySQLPacket.COM_QUERY;
            packet.arg = node.getStatement().getBytes();//(this.session.getConnection().getCharset());
            
            Command cmd = new Command(packet, CmdType.FRONTEND_TYPE,node.getSqlType());
           // Command command = session.getConnection().get.getSource().getFrontendCommand(node.getStatement(), node.getSqlType());
	        backend.postCommand(cmd);
	        // fire it
	        backend.fireCmd();
	}

	@Override
	public  boolean handleResponse(BackendConnection conn,BinaryPacket bin) throws  UnknownPacketException{
			MySQLConnection mConn =  (MySQLConnection)conn;
			Command cmd = mConn.peekCommand();
	        
			boolean finished = false;
	        int type = bin.data[0];
	        
	        try{
			        switch (type) {
			            case OKPacket.FIELD_COUNT: { //非Select命令
			            	
			                if (cmd.getType() == CmdType.BACKEND_TYPE) {
			                     logger.debug("backend command okay");
			                } else {
			                	logger.debug("frontend command okay");
			                	this.okResponse(bin);
			                }
			                
			                finished = true;
			                break;
			            }
			            case ErrorPacket.FIELD_COUNT: { //错误信息处理
			            	ErrorPacket err = new ErrorPacket();
			                err.read(bin);
			                
			                logger.warn(this.rrs.getStatement()+"Error");
			                if (cmd.getType() == CmdType.BACKEND_TYPE) {
			                    throw new ErrorPacketException("Command errorMessage,message=" + new String(err.message));
			                } else {
			                	this.errorResponse(bin);
			                }
			                finished = true;
			                break;
			            }
			            default: // HEADER|FIELDS|FIELD_EOF|ROWS|LAST_EOF
			            	finished = handleResultSet(bin,cmd.getType());
			        }	        	
	        }catch(UnknownPacketException ex){
	        	throw new UnknownPacketException(bin.toString());	
	        }
	        
	        return finished;
			/*if (cmd.getSqlType() == ServerParse.SELECT || cmd.getSqlType() == ServerParse.SHOW) {
	            selecting = true;
	        } else {
	            selecting = false;
	        }*/
	        // if handle successful , the command can be remove
/*	        if (handleResponse(bin, cmd.getType())) {
	        	this.conn.pollCommand();
	            return true;
	        } else {
	            return false;
	        }*/
	}

    private void addToFieldList(BinaryPacket bin) {
        fieldList.add(bin);
    }

    private void resetSelect() {
        selectState = MySQLConnectionState.RESULT_SET_FIELD_COUNT;
        fieldList.clear();
    }

    // select状态的推进
    private void selectStateStep() {
        selectState++;
        // last_eof和field_count合并为同一状态
        if (selectState == 6) {
            selectState = 2;
        }
    }	
	
    private boolean handleResultSet(BinaryPacket bin, CmdType cmdType) {
        boolean result = false;
        int type = bin.data[0];
        switch (type) {
            case ErrorPacket.FIELD_COUNT:
                // 重置状态,且告诉上层当前select已经处理完毕
                resetSelect();
                result = true;
                ErrorPacket err = new ErrorPacket();
                err.read(bin);
                // write(bin,cmdType);
                this.errorResponse(bin);
                logger.error("handleResultSet errorMessage:" + new String(err.message));
                break;
            case EOFPacket.FIELD_COUNT:
                EOFPacket eof = new EOFPacket();
                eof.read(bin);
                if (selectState == MySQLConnectionState.RESULT_SET_FIELDS) {
                    // logger.info("eof");
                    // 推进状态 需要步进两次状态,先到field_eof,再到row
                    selectStateStep();
                    selectStateStep();
                    // 给FieldList增加eof
                    addToFieldList(bin);
                    this.fieldListResponse(fieldList);
                } else {
                    if (eof.hasStatusFlag(ServerStatus.SERVER_MORE_RESULTS_EXISTS)) {
                        // 重置为select的初始状态,但是还是处在select mode下
                        selectState = MySQLConnectionState.RESULT_SET_FIELD_COUNT;
                    } else {
                        // 重置,且告诉上层当前select已经处理完毕
                        resetSelect();
                        result = true;
                    }
                    this.lastEofResponse(bin);
                }
                break;
            default:
                switch (selectState) {
                    case MySQLConnectionState.RESULT_SET_FIELD_COUNT:
                        selectStateStep();
                        addToFieldList(bin);
                        break;
                    case MySQLConnectionState.RESULT_SET_FIELDS:
                        addToFieldList(bin);
                        break;
                    case MySQLConnectionState.RESULT_SET_ROW:
                    	this.rowResponse(bin);
                        break;
                }
        }
        return result;
    }


}
