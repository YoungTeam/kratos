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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName: DateUtil
 * @Description: 时间日期通用库
 * @author YoungTeam
 * @date 2019年1月8日 下午7:03:47
 *
 */
public class DateUtil {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

	public static String now(){
		return format(new Date());
	}
	
	public static Date parse(String dateTime){
		return parse(dateTime,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static Date parse(String dateTime,String dateFormat){
		SimpleDateFormat thisFormat = new SimpleDateFormat(dateFormat); 
		try {
			return (Date)thisFormat.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String format(Date dateTime){
		
		return dateFormat.format(dateTime);
	}
	
	public static String format(Date dateTime,String dateFormat){
		
		SimpleDateFormat thisFormat = new SimpleDateFormat(dateFormat); 
		return thisFormat.format(dateTime);
	}
	
	public static String format(Date dateTime,String dateFormat,Locale  local){
		
		SimpleDateFormat thisFormat = new SimpleDateFormat(dateFormat,local); 
		return thisFormat.format(dateTime);
	}
}
