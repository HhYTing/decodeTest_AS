package com.example.util;

import android.util.Log;



public class LogUtils{
	private final static String DEBUG_TAG = "LogUtils";
	private static boolean logEnable = false;
	private static int LOG_MAXLENGTH = 1024;  
	
	public static boolean getLogUtilsStatus(){
		return logEnable;
	}
	public static void setLogUtils(boolean Enable){
		Log.d(DEBUG_TAG,"log enable="+Enable);
		logEnable = Enable;
	}
	
	public static int v (String tag, String msg){
		if(logEnable){
			return Log.v(tag,msg);
		}else{
			return 1;
		}
	}
	public static int d (String tag, String msg){
		if(logEnable){
			return Log.d(tag,msg);
		}else{
			return 1;
		}
	}
	public static int i (String tag, String msg){
		if(logEnable){
			return Log.i(tag,msg);
		}else{
			return 1;
		}
	}
	public static int w (String tag, String msg){
		if(logEnable){
			return Log.w(tag,msg);
		}else{
			return 1;
		}
	}
	public static int e (String tag, String msg){
		if(logEnable){
			return Log.e(tag,msg);
		}else{
			return 1;
		}
	}
	public static int e (String tag, String msg, Throwable tr){
		if(logEnable){
			return Log.e(tag,msg,tr);
		}else{
			return 1;
		}
	}
	
	//logcat单次输出数据量限制为4k左右，超出部分会被丢弃，所以自己封装一个打印大数据量的函数
	//该函数最大输出为100k(100*1k)
	public static void printLargeData (String tag, String msg){
		int strLength = msg.length();  
        int start = 0;  
        int end = LOG_MAXLENGTH;  
        for (int i = 0; i < 100; i++) {  
            if (strLength > end) {  
                Log.i(tag, msg.substring(start, end));  
                start = end;  
                end = end + LOG_MAXLENGTH;  
            } else {  
                Log.i(tag, msg.substring(start, strLength));  
                break;  
            }  
        }
	}
	
}
