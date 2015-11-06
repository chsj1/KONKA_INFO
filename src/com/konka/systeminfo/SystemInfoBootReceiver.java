package com.konka.systeminfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SystemInfoBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)||intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
			Intent activityInfoService = new Intent(context,SystemInfoService.class);
			context.startService(activityInfoService);
			Intent anrService = new Intent(context,AnrAndErrorService.class);
			context.startService(anrService);
			Log.e("ycj", "boot_start");
		}
	}

}
