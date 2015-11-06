package com.konka.systeminfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.konka.android.tv.KKCommonManager;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class AnrAndErrorService extends Service {
	private Map<String, AnrRecord> anrMap;
//	AnrDataBaseOpenHelper anrdb;
	KKCommonManager kkcm;
	public static final String ANRERROR_ACTION = "com.konka.systeminfo.anranderror";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		kkcm = KKCommonManager.getInstance(getApplicationContext());
		Log.e("ycj", "anranderrorservice service is start");
		if (CommonUtil.dateLegal(getCheckDate()) && intent != null) {
			if(intent.getAction() != null && ANRERROR_ACTION.equalsIgnoreCase(intent.getAction())){
					anrAndError(intent);
					stopSelf();
//					Log.e("ycj", "anranderror hand each");
			}else {
				// 开机自启动
//				Log.e("ycj", "createDatabase()");
				createDatabase();
				/*anrdb = new AnrDataBaseOpenHelper(getApplicationContext());
				SQLiteDatabase data = anrdb.getReadableDatabase();*/
				/*
				 * 修改数据库读写权限
				 */
//				data.close();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	private void createDatabase() {
		File misc = new File("/data/misc");
		if(!misc.exists()){
			misc.mkdirs();
		}
		CommonUtil.setFileOrDirRW("777","/data/misc");
		File miscKonka = new File("/data/misc/konka");
		if(!miscKonka.exists()){
			miscKonka.mkdirs();
		}
		CommonUtil.setFileOrDirRW("777","/data/misc/konka");
		File fileName = new File(CommonUtil.defualtPath);
		if(!fileName.exists()){
			fileName.mkdirs();
			CommonUtil.setFileOrDirRW("777",CommonUtil.defualtPath);
		}
		File databaseFilename = new File(CommonUtil.TdefualtPath);
		if(!databaseFilename.exists()){
			databaseFilename.mkdirs();
			CommonUtil.setFileOrDirRW("777",CommonUtil.TdefualtPath);
		}
		String str = databaseFilename + "/" + "anranderror.db";
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
			     str, null); 
		database.execSQL("CREATE TABLE IF NOT EXISTS anrAndError(_id integer primary key autoincrement, " +
				"errorType varchar(100), errorCount varchar(100), stackInfo varchar(50000),anrDate varchar(100)," +
				"appVersionCode varchar(100),appVersionName varchar(100),appPackageName varchar(100))");
		CommonUtil.setFileOrDirRW("777",CommonUtil.TdefualtPath + "/" + "anranderror.db");
		CommonUtil.setFileOrDirRW("777",CommonUtil.TdefualtPath + "/" + "anranderror.db-journal");
		database.close();
	}
	private void anrAndError(Intent intent) {

		if (intent.getIntExtra("switch", 1) == 1&& intent.getIntExtra("errorList", 1) == 1) {
			
			/*anrdb = new AnrDataBaseOpenHelper(getApplicationContext());
			SQLiteDatabase data = anrdb.getReadableDatabase();*/
			
			
//			File file = new File(CommonUtil.TdefualtPath+"anranderror.db");
			String str = CommonUtil.TdefualtPath+ "/" +"anranderror.db";
			/*SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
				     str, null); */
			SQLiteDatabase database = null;
			try {
				database = SQLiteDatabase.openDatabase(str, null, SQLiteDatabase.OPEN_READWRITE);
			} catch (Exception e) {
			}
			
			if(database != null){
				queryAnrData(database);
				
				intent.setAction("com.konka.tvsideservice.ACCEPT_DATA_SERVICE");
				if (anrMap != null && anrMap.size() > 0) {
					intent.putExtra("data", anrMapToJosn().toString());
				} else {
					intent.putExtra("is_empty", true);
				}
				startService(intent);
				
//				database = anrdb.getWritableDatabase();
				database.delete("anrAndError", null, null);
				database.close();
			}
		}
		// data.update("anrAndError", null, null, null);
	}

	private Map<String, AnrRecord> queryAnrData(SQLiteDatabase data) {
		Cursor c = data.rawQuery("select * from anrAndError", null);
//		Log.e("ycj", "anr  " + "c.moveToNext");
		anrMap = new HashMap<String, AnrRecord>();
		while (c.moveToNext()) {

			String errorType = c.getString(c.getColumnIndex("errorType"));
			int errorCount = c.getInt(c.getColumnIndex("errorCount"));
			String stackInfo = c.getString(c.getColumnIndex("stackInfo"));
			String anrDate = c.getString(c.getColumnIndex("anrDate"));
			int anrAppVersionCode = c.getInt(c.getColumnIndex("appVersionCode"));
			String anrAppVersionName = c.getString(c.getColumnIndex("appVersionName"));
			String anrAppPackageName = c.getString(c.getColumnIndex("appPackageName"));

//			Log.e("ycj", "c anrDate = " + anrDate);
//				if(CommonUtil.dateLegal(anrDate)){
					AnrRecord anrRecord = new AnrRecord(errorType, errorCount,
							stackInfo, anrDate, anrAppVersionCode, anrAppVersionName,
							anrAppPackageName);
					if (anrRecord.valid()) {
						AnrRecord anrMapRecord = anrMap.get(anrRecord.stackInfo);
						if (anrMapRecord == null) {
							anrMap.put(anrRecord.stackInfo, anrRecord);
						} else if (anrMapRecord.addRecord(anrRecord)) {
							anrMap.put(anrMapRecord.stackInfo, anrMapRecord);
						}
//					}
			   }
//				Log.e("ycj", "anrMap key "+anrMap.get(anrAppPackageName));
		}
		return anrMap;

	}

	private JSONObject anrMapToJosn() {
		JSONObject json = new JSONObject();
		try {
			json.put("domain", "error");
			json.put("version", "1.0");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		JSONObject jsondate = new JSONObject();
		try {
			json.put("data", jsondate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			jsondate.put("paltform", kkcm.getPlatform());
			jsondate.put("tv_software_version", kkcm.getVersion());
			jsondate.put("softid", kkcm.getSoftwareID());
			jsondate.put("tv_model", kkcm.getType());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		JSONArray jsonlist = new JSONArray();
		try {
			jsondate.put("errorlist", jsonlist);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (!anrMap.isEmpty()) {
			for (Entry<String, AnrRecord> entry : anrMap.entrySet()) {
				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("error_type",entry.getValue().errorType);
					jsonobj.put("error_count",entry.getValue().errorCount); 
					jsonobj.put("stack_info",entry.getValue().stackInfo); 
					jsonobj.put("date",entry.getValue().anrDate);
					jsonobj.put("app_version_code",entry.getValue().anrAppVersionCode);
					jsonobj.put("app_version_name",entry.getValue().anrAppVersionName);
					jsonobj.put("app_package_name", entry.getValue().anrAppPackageName);
					 
				} catch (JSONException e) {
					e.printStackTrace();
				}
				jsonlist.put(jsonobj);
			}
		}
//		Log.e("ycj", " anr " + json);
		return json;
	}
	private static String getCheckDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String str = sdf.format(date);
		return str;
	}
}
