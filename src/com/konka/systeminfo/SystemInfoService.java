package com.konka.systeminfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.konka.android.tv.KKCommonManager;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("NewApi")
public class SystemInfoService extends Service {

	DataBaseOpenHelper dboh;
	DataBaseOpenHelper[] db;
	private Map<String, ActivityRecord> map;
	private static String SName = "androidsystem";
	public static final String ACTIVITYINFO_ACTION ="com.konka.systeminfo.activityinfo";
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.e("ycj", "activityinfo service is start"+intent);
		Log.e("ycj", "now date is  " + CommonUtil.dateLegal(getCheckDate()));
		
//		Log.e("ycj", " aintent "+intent.getAction());
		if (CommonUtil.dateLegal(getCheckDate()) && intent != null) {
			if(intent.getAction() != null && ACTIVITYINFO_ACTION.equalsIgnoreCase(intent.getAction())){
					// 请求上传数据
					readDatabase(intent);
					stopSelf();

			}else {
				// 开机自启动
				createDatabase();
				/*dboh = new DataBaseOpenHelper(getApplicationContext(), SName
						+ getTodayDate() + ".db", null, 1);
			    SQLiteDatabase data = dboh.getReadableDatabase();*/
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
		String data = databaseFilename + "/" + SName + getTodayDate() + ".db";
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
				data, null); 
		database.execSQL("CREATE TABLE IF NOT EXISTS activityinfo(_id integer primary key autoincrement, " +
				"activityName varchar(100), packageName varchar(100), versionCode varchar(100),versionName varchar(100)," +
				"startDate varchar(100),endDate varchar(100))");
		CommonUtil.setFileOrDirRW("777",CommonUtil.TdefualtPath + "/" + SName + getTodayDate() + ".db");
		CommonUtil.setFileOrDirRW("777",CommonUtil.TdefualtPath + "/" + SName + getTodayDate() + ".db-journal");
		database.close();
	}

	/**
	 * 遍历数据库文件夹，读取今天之前的数据库
	 */
	@SuppressLint("NewApi")
	private void readDatabase(Intent intent) {
		Log.e("ycj", " switch = "+intent.getIntExtra("switch", 1)+"activity_list = "+intent.getIntExtra("activity_list", 1));
		if (intent.getIntExtra("switch", 1) == 1&& intent.getIntExtra("activity_list", 1) == 1) {
			if (dbFileArray().length > 0) {
				File file = null;
//				db = new DataBaseOpenHelper[dbFileArray().length];
				while (dbFileArray().length > 0) {
					Log.e("ycj", " filename is " + dbFileArray()[0]);
//					db[0] = new DataBaseOpenHelper(getApplicationContext(),dbFileArray()[0], null, 1);
//					SQLiteDatabase data = db[0].getReadableDatabase();
//					file = new File(CommonUtil.TdefualtPath+dbFileArray()[0]);
					String str = CommonUtil.TdefualtPath + "/" + dbFileArray()[0];
					/*SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
						     str, null);*/ 
					SQLiteDatabase database = null;
					try {
						database = SQLiteDatabase.openDatabase(str, null, SQLiteDatabase.OPEN_READONLY);
					} catch (SQLiteException e) {
						
					}
					if(database != null){
						queryActivityInfoData(database);// 查询数据库中的全部数据并处理存储到map中
						
						intent.setAction("com.konka.tvsideservice.ACCEPT_DATA_SERVICE");
						if (map != null && map.size() > 0) {
							intent.putExtra("data", mapToJoson().toString());
						} else {
							intent.putExtra("is_empty", true);
						}
						startService(intent);
					}
//					Log.e("ycj", " remove filename then "
//							+ dbFileArray()[0]);
					file = new File(CommonUtil.TdefualtPath + "/" + dbFileArray()[0]);
					database.deleteDatabase(file);

//					Log.e("ycj", " remove filename then "
//							+ dbFileArray().length);
					// }
				}
			}
		}
	}

	private JSONObject mapToJoson() {
		JSONObject json = new JSONObject();
		try {
			json.put("domain", "activity_start");
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
		JSONArray jsonlist = new JSONArray();
		try {
			jsondate.put("activity_list", jsonlist);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (!map.isEmpty()) {
			for (Map.Entry<String, ActivityRecord> entry : map.entrySet()) {
				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("activity_name", entry.getValue().activityName);
					jsonobj.put("package_name", entry.getValue().packageName);
					jsonobj.put("date", entry.getValue().date);
					jsonobj.put("app_version_name",entry.getValue().versionName);
					jsonobj.put("app_version_code",entry.getValue().versionCode);
					jsonobj.put("use_recorder", entry.getValue().timeRecordToJasonString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				jsonlist.put(jsonobj);
			}
		}
//		Log.e("ycj", "  " + json);
		return json;

	}

	@SuppressLint({ "UseSparseArrays", "NewApi" })
	private Map<String, ActivityRecord> queryActivityInfoData(
			SQLiteDatabase data) {

		Cursor c = data.rawQuery("select * from activityinfo", null);
//		Log.e("ycj", "c  " + c.moveToNext());
		map = new HashMap<String, ActivityRecord>();
		while (c.moveToNext()) {

			String activityName = c.getString(c.getColumnIndex("activityName"));
			String packageName = c.getString(c.getColumnIndex("packageName"));
			String versionName = c.getString(c.getColumnIndex("versionName"));
			int versionCode = c.getInt(c.getColumnIndex("versionCode"));
			String startDate = c.getString(c.getColumnIndex("startDate"));
			String endDate = c.getString(c.getColumnIndex("endDate"));

//			Log.e("ycj", "activityname  " + activityName);
			
				if(CommonUtil.dateLegal(startDate)&&CommonUtil.dateLegal(endDate)){
					ActivityRecord newRecord = new ActivityRecord(activityName,
							packageName, versionCode, versionName, startDate, endDate);
					if (newRecord.valid()) {
						ActivityRecord mapRecord = map.get(newRecord.activityName);
						if (mapRecord == null) {
							map.put(newRecord.activityName, newRecord);
						} else if (mapRecord.addRecord(newRecord)) {
							map.put(mapRecord.activityName, mapRecord);
						}
					}
			}
//			Log.e("ycj", "map is  " + map);
		}
		data.close();
		return map;
	}
	/*
	 * 返回文件夹非当天之前的数据库并放入数组
	 */
	private String[] dbFileArray() {
		File f = new File(CommonUtil.TdefualtPath);// "./"表示要遍历的文件夹为当前文件夹
		String sf[] = f.list();// 将文件夹中的文件名放入数组中
		ArrayList<String> list = new ArrayList<String>();
		if (sf != null && sf.length > 0) {
			for (String s : sf) {// 开始遍历文件夹
				String[] p = s.split("\\.");
				if (s.equals("androidsystem" + getTodayDate() + ".db")
						|| p[p.length - 1].equals("db-journal")
						|| s.equals("anranderror.db")) {
					list.remove(s);
				} else {
					list.add(s);
				}
			}
		}
		// Log.e("ycj", "list size is  "+list.size());
		return list.toArray(new String[list.size()]);
	}
	@SuppressLint("SimpleDateFormat")
	private static String getTodayDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(date);
		return str;
	}
	private static String getCheckDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String str = sdf.format(date);
		return str;
	}
}
