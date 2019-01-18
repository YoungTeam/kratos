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
package yt.kratos.net.backend.mysql.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import yt.kratos.mysql.packet.BinaryPacket;
import yt.kratos.mysql.packet.ErrorPacket;
import yt.kratos.mysql.packet.MySQLPacket;
import yt.kratos.net.backend.mysql.MySQLConnState;
import yt.kratos.net.backend.mysql.MySQLConnection;
import yt.kratos.net.backend.mysql.cmd.CmdType;
import yt.kratos.net.backend.mysql.cmd.Command;
import yt.kratos.parse.ServerParse;

/**
 * @ClassName: MySQLCommandHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月16日 下午8:17:57
 *
 */
public class MySQLCommandHandler   extends ChannelInboundHandlerAdapter{
	private MySQLConnection conn;
    // 是否在select
    private volatile boolean selecting;
    private volatile int selectState;
    
    public MySQLCommandHandler(MySQLConnection conn) {
        this.conn = conn;
        this.selecting = false;
        this.selectState = MySQLConnState.RESULT_SET_FIELD_COUNT;
        //fieldList = new LinkedList<BinaryPacket>();*/
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BinaryPacket bin = (BinaryPacket) msg;
        if (processCmd(ctx, bin)) {
            // fire the next cmd
            //conn.fireCmd();
        }
    }
    
    /**
     * 
    * @Title: processCmd
    * @Description: Cmd命令处理
    * @return boolean    返回类型
    * @throws
     */
    private boolean processCmd(ChannelHandlerContext ctx, BinaryPacket bin) {
        Command cmd = this.conn.peekCommand();
        if (cmd.getSqlType() == ServerParse.SELECT || cmd.getSqlType() == ServerParse.SHOW) {
            selecting = true;
        } else {
            selecting = false;
        }
        // if handle successful , the command can be remove
        if (handleResponse(bin, cmd.getType())) {
            source.pollCommand();
            return true;
        } else {
            return false;
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
                getResponseHandler().errorResponse(bin);
                logger.error("handleResultSet errorMessage:" + new String(err.message));
                break;
            case EOFPacket.FIELD_COUNT:
                EOFPacket eof = new EOFPacket();
                eof.read(bin);
                if (selectState == MySQLConnState.RESULT_SET_FIELDS) {
                    // logger.info("eof");
                    // 推进状态 需要步进两次状态,先到field_eof,再到row
                    selectStateStep();
                    selectStateStep();
                    // 给FieldList增加eof
                    addToFieldList(bin);
                    getResponseHandler().fieldListResponse(fieldList);
                } else {
                    if (eof.hasStatusFlag(MySQLPacket.SERVER_MORE_RESULTS_EXISTS)) {
                        // 重置为select的初始状态,但是还是处在select mode下
                        selectState = MySQLConnState.RESULT_SET_FIELD_COUNT;
                    } else {
                        // 重置,且告诉上层当前select已经处理完毕
                        resetSelect();
                        result = true;
                    }
                    getResponseHandler().lastEofResponse(bin);
                }
                break;
            default:
                switch (selectState) {
                    case MySQLConnState.RESULT_SET_FIELD_COUNT:
                        selectStateStep();
                        addToFieldList(bin);
                        break;
                    case MySQLConnState.RESULT_SET_FIELDS:
                        addToFieldList(bin);
                        break;
                    case MySQLConnState.RESULT_SET_ROW:
                        getResponseHandler().rowResponse(bin);
                        break;
                }
        }
        return result;
    }    
    
}
