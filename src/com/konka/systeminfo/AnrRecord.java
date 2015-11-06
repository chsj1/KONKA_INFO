package com.konka.systeminfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AnrRecord {

	public String errorType;
	public int errorCount;
	public String stackInfo;
	public String anrDate;
	public int anrAppVersionCode;
	public String anrAppVersionName;
	public String anrAppPackageName;
	private boolean valid = false;
	public AnrRecord(String errorType, int errorCount, String stackInfo,
			String anrDate, int anrAppVersionCode, String anrAppVersionName,
			String anrAppPackageName) {
		super();
		this.errorType = errorType;
		this.errorCount = errorCount;
		this.stackInfo = stackInfo;
		this.anrDate = anrDate;
		this.anrAppVersionCode = anrAppVersionCode;
		this.anrAppVersionName = anrAppVersionName;
		this.anrAppPackageName = anrAppPackageName;
		setTimeRecord(anrDate);
	}
	private void setTimeRecord(String anrdate) {
		errorCount = 1;
		if(anrdate.length()>10){
			anrDate = anrdate.substring(0, 10);
		}else{
			anrDate = anrdate;
		}
		valid = true;
		
	}
	public boolean addRecord(AnrRecord anrRecord) {
		errorCount += anrRecord.errorCount;
		if(errorCount < 0){
			return false;
		}
		return true;
	}
	public boolean valid() {
		// TODO Auto-generated method stub
		return valid;
	}
	
	
	
}
