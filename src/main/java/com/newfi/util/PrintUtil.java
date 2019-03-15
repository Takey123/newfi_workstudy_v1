package com.newfi.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;



public class PrintUtil {
	
	
	/**
	 * 返回JSON数据
	 * @param request
	 * @param response
	 * @throws IOException
	 */

	public static void print(HttpServletRequest request,HttpServletResponse response,JSONObject json,String actionName){
		
		response.setCharacterEncoding("utf-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			Log4jUtil.info(actionName+"方法返回的结果为"+json);
			out.print(json);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
