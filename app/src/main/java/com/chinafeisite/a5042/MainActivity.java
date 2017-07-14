package com.chinafeisite.a5042;



import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("dd","MainActivity启动了");
//		Log.d("dd",Boolean.toString(isServiceWork(getApplicationContext(),"com.chinafeisite.a5042.LogService")));
		if (!isServiceWork(MainActivity.this,"com.chinafeisite.a5042.LogService")){
			Intent startIntent = new Intent(this, LogService.class);
			startService(startIntent);
		}
		Log.d("dd","999999999");
		Log.d("dd","888888888");
		Log.d("dd","777777777");
		Log.d("dd","6666666666");
		Log.d("dd","55555555555");
		Log.d("dd",Boolean.toString(isServiceWork(getApplicationContext(),"com.chinafeisite.a5042.LogService")));



	}
			
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}



	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}



//	class InnerRecevier extends BroadcastReceiver {
//
//		final String SYSTEM_DIALOG_REASON_KEY = "reason";
//
//		final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
//
//		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//
//		public String chage = "开启";
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
//				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//				if (reason != null) {
//					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//						if (chage.equals("开启")){
////							LogService.lp.type = WindowManager.LayoutParams.TYPE_PHONE;
////							LogService.lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
////							chage ="关闭";
////							LogService.wm.updateViewLayout(LogService.mContainer, LogService.lp);
////							LogService.this.destoryView();
//							Log.d("dd","关闭");
//
//						}else{
//							chage ="开启";
//							Log.d("dd","开启");
//						}
//						Toast.makeText(MainActivity.this, "Home键被监听", Toast.LENGTH_SHORT).show();
//					} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
//						Toast.makeText(MainActivity.this, "多任务键被监听", Toast.LENGTH_SHORT).show();
//					}
//				}
//			}
//		}





//	}


}
