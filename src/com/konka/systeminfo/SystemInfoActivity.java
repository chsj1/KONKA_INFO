package com.konka.systeminfo;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SystemInfoActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(SystemInfoActivity.this,SystemInfoService.class);
		startService(intent);
		Intent anrIntent = new Intent(SystemInfoActivity.this,AnrAndErrorService.class);
		startService(anrIntent);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
