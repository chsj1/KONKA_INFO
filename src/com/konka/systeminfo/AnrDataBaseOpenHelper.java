package com.konka.systeminfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnrDataBaseOpenHelper extends SQLiteOpenHelper{

	public final static String anrAndError="anranderror.db";
	public final static int anrVersion = 1;
	public AnrDataBaseOpenHelper(Context context) {
		super(context, anrAndError, null, anrVersion);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	     db.execSQL("CREATE TABLE IF NOT EXISTS anrAndError(_id integer primary key autoincrement, " +
					"errorType varchar(100), errorCount varchar(100), stackInfo varchar(50000),anrDate varchar(100)," +
					"appVersionCode varchar(100),appVersionName varchar(100),appPackageName varchar(100))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS anranderror");	//此处是删除数据表，在实际的业务中一般是需要数据备份的
		onCreate(db);	
		
	}


}
