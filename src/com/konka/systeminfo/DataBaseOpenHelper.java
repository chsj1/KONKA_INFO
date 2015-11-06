package com.konka.systeminfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHelper extends SQLiteOpenHelper{

	private final static int VERSION_NUM = 1;
	
	public DataBaseOpenHelper(Context context,String str,CursorFactory cf,int i) {
		super(context, str, null, i);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE IF NOT EXISTS activityinfo(_id integer primary key autoincrement, " +
				"activityName varchar(100), packageName varchar(100), versionCode varchar(100),versionName varchar(100)," +
				"startDate varchar(100),endDate varchar(100))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS activityinfo");	//此处是删除数据表，在实际的业务中一般是需要数据备份的
		onCreate(db);	
	}
}
