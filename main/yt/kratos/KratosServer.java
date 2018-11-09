/**  
* @Title: KratosServer.java
* @Package yt.kratos
* @Description: TODO(用一句话描述该文件做什么)
* @author YoungTeam (yangting@sogou-inc.com)
* @date 2018年11月5日 下午3:50:16
* @version V1.0  
*/ 
package yt.kratos;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @ClassName: KratosServer
 * @Description: Kratos服务启动入口
 * @author YoungTeam
 * @date 2018年11月5日 下午3:50:16
 *
 */
public class KratosServer {

    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final Logger LOGGER = LoggerFactory.getLogger(KratosServer.class);
    
	private static final KratosServer INSTANCE = new KratosServer();
	
	/***
	 * 
	* @Title: getInstance
	* @Description: TODO
	* @return KratosServer    返回类型
	* @throws
	 */
	public static final KratosServer getInstance() {
		return INSTANCE;
	}
	
	public void startup(){
		
	}
	
	public static void main(String[] args){
		try{
		
			KratosServer.getInstance().startup();;
			System.out.println("Kratos Server startup successfully. see logs in logs/kratos.log");
		
		}catch(Exception e){
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            LOGGER.error(sdf.format(new Date()) + " startup error", e);
            System.exit(-1);
		}
	}
	
}
   