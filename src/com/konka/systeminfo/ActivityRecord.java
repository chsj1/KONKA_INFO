package com.konka.systeminfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.util.Log;

public class ActivityRecord {
   
	private boolean valid = false;

	public String activityName;
	public String packageName;
	public int versionCode;
	public String versionName;
	public String date;
	public int [][]timeRecord = new int[24][2];
	public static int HOUR_SECOND = 60*60;
	public static final String MINDATE ="2014-01-01-0-0-0";
	public static final String MAXDATE ="2050-12-31-23-59-59";
	private Date minDate,maxDate;
	
	public ActivityRecord(String activityName, String packageName,
			int versionCode, String versionName,String startDate,String endDate) {
		super();
		this.activityName = activityName;
		this.packageName = packageName;
		this.versionCode = versionCode;
		this.versionName = versionName;
		setTimeRecord(startDate, endDate);
		
	}
	
	private boolean checkTime(int hour, int second) {
		if (hour >= 0  && hour <= 23 && second >= 0 && second < HOUR_SECOND)
			return true;
		else
			return false;
	}
	
	private void setTimeRecord(String startDate, String endDate) {
		Date startTime = null,endTime = null;
		int startHour,startSecond,endHour,endSecond;
		if(startDate.length()>10){
			date = startDate.substring(0, 10);
		}else{
			date = startDate;
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		
		try {
			startTime = sdf.parse(startDate);
			endTime = sdf.parse(endDate);
			minDate = sdf.parse(MINDATE);
			maxDate = sdf.parse(MAXDATE);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		if(minDate.getTime()>startTime.getTime()&&startTime.getTime()>maxDate.getTime()){
			valid = false;
			return;
		}
		
		startHour = startTime.getHours();
		startSecond = startTime.getMinutes() * 60 + startTime.getSeconds();
		
		endHour = endTime.getHours();
		endSecond = endTime.getMinutes() * 60 + endTime.getSeconds();
		
		if (checkTime(startHour, startSecond) == false ||
			checkTime(endHour, endSecond) == false) {
			return;
		}
				
		for (int hour = startHour; hour <= endHour; hour++) {
			timeRecord[hour][0] = 1;
			if (hour == startHour && hour == endHour) {
				timeRecord[hour][1] = endSecond - startSecond;	
			} else if (hour == startHour && hour != endHour){
				timeRecord[hour][1] = HOUR_SECOND - startSecond;
			} else if (hour != startHour && hour != endHour) {
				timeRecord[hour][1] = HOUR_SECOND;
			} else if (hour != startHour && hour == endHour) {
				timeRecord[hour][1] = endSecond;
			} 
		}
		valid = true;
	}

	public boolean addRecord(ActivityRecord activityRecord) {
		for (int hour = 0; hour <= 23; hour++) {
			timeRecord[hour][0] += activityRecord.timeRecord[hour][0];
			timeRecord[hour][1] += activityRecord.timeRecord[hour][1];
			
			if (checkTime(hour, timeRecord[hour][1]) == false) {
				return false;
			}
		}
		return true;
	}
	
	public String timeRecordToJasonString() {
		String jasonTime = "";
		for (int hour = 0; hour <= 23; hour++) {
			if (timeRecord[hour][0] > 0) {
				if (jasonTime.length() != 0) {
					jasonTime += ",";
				}
				jasonTime += hour + "," + timeRecord[hour][0] + "," + timeRecord[hour][1];			
			}
		}
		return jasonTime;
	}
	
	public boolean valid() {
		return valid;
	}
	
	/*class ActivityRecord{
		private String activity; 
		private String package; 
		private int timeRecord[24][2];
		public ActivitRecord(String activity, String package, String startTime, String endTime) {
			activity = activity;
			package = package;
			parseTimeRecord(startTime, endTime)
		}
		private void parseTimeRecord(String startTime, String endTime) {
			private int startHour, startSecond; 
			private int endtHour,  endSecond;
			
			
			
			for (int hour = startHour; hour <= endtHour; hour++) {
				timeRecord[hour][0] += 1;
				if (hour == startHour && hour == endHour) {
					timeRecord[hour][1] += endSecond - startSecond;						
				} else if (hour == startHour && hour != endHour){
					timeRecord[hour][1] += HOUR_SECOND - startSecond;
				} else if (hour != startHour && hour != endHour) {
					timeRecord[hour][1] += HOUR_SECOND;
				} else if (hour != startHour && hour == endHour) {
					timeRecord[hour][1] += endSecond;
				} 
			}
		}
		public void AddRecord(ActivityRecord record) {

		}
	}
	ActivityRecord newRecord = new ActivityRecord(activity, package, startTime,endTime);
	ActivityRecord mapItem = map.get(newRecord.activity);
	if (mapItem != null) {
		mapItem.AddRecord(newRecord);
		map.put(newRecord.activity, mapItem);
	} else {
		map.put(newRecord.activity, newRecord);
	}*/
}
