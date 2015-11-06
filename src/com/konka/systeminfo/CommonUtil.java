package com.konka.systeminfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

public class CommonUtil {
	public static final String defualtPath = "/data/misc/konka/com.konka.systeminfo";
	public static final String TdefualtPath = "/data/misc/konka/com.konka.systeminfo/databases";
	public static String minTvTime = "2014-01-01-00-00-00";
	public static String maxTvTime = "2050-12-31-23-59-59";
	public static boolean dateLegal(String date) {
		if(date != null ){
			if(StringToDate(date) != null){
				if (StringToDate(date).getTime() > StringToDate(minTvTime).getTime()&& StringToDate(date).getTime() < StringToDate(maxTvTime).getTime()) {
					return true;
				} else {
					return false;
				}
			}
//			Log.e("ycj", "StringToDate is null ");
			return false;
		}else {
			return false;
		}
		
	}
	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String str = sdf.format(date);
		return str;
	}
	/*
	 * 字符串转化为日期
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date StringToDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/*
	 * 获取权限
	 */
	/*@SuppressLint("NewApi")
	public static void setFileOrDirRW(String path) {
		File f = new File(path);
		f.setReadable(true, false);
		f.setWritable(true, false);
	}*/
	public static void setFileOrDirRW(String permission, String path){
		try{
			String command = "chmod" + " " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
