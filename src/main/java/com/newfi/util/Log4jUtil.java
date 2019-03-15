package com.newfi.util;

import org.apache.log4j.Logger;

public class Log4jUtil {
	
	private static Logger log=Logger.getLogger(Log4jUtil.class);
	
	private Log4jUtil() {}  
	
	
	public static void info(Object object){
		log.info(object);
	}
	
	public static void debug(Object object){
		log.debug(object);
	}
	
	public static void error(Object object){
		log.error(object);
	}
	
}
