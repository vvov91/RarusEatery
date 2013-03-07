package rarus.eatery.activity;

import rarus.eatery.model.EateryConstants;
import rarus.eatery.service.EateryWebService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	Intent serviceIntent;
	ServiceConnection connection;
	EateryWebService client;
	BroadcastReceiver receiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService();	
		final String SERV_LOGIN ="mobile";
		final String SERV_PASS ="mobile";
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity onServiceConnected");
				client = ((EateryWebService.EateryServiceBinder) binder).getService();
				Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity Login "+SERV_LOGIN);
				Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity Pass "+SERV_PASS);
				client.getMenu();
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity onServiceDisconnected");
			}
		};
	}
	@Override
	protected void onStart() {
		super.onStart();
		bindService(serviceIntent, connection, 0);
		Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity: onStart()");
		// client.getMenu("mobile", "mobile");
	}
	
	private void startService() {
		serviceIntent = new Intent(this, EateryWebService.class);
		startService(serviceIntent);
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity onServiceConnected");
				client = ((EateryWebService.EateryServiceBinder) binder).getService();
				// client.getMenu("mobile", "mobile");
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity onServiceDisconnected");
			}
		};

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = intent.getBooleanExtra(EateryConstants.SERVICE_RESULT, false);
				if (result) {					
						//menu = client.getMenuResult();
						Log.d(EateryConstants.GUI_LOG_TAG, "Menu gotten");
						//for (Integer dat : menu.getAvalibleData()) {
						//	Log.i("menuRes", menu.getMenuByDate(dat).toString());
						//}
						//makeViewPager(menu);					
				} else {
					//Toast.makeText(getBaseContext(), "Произошла ошибка. "+intent.getStringExtra(SERVER_RESULT_STATUS),
						//	Toast.LENGTH_LONG).show();
				}
			}
		};
		IntentFilter intFilt = new IntentFilter(EateryConstants.BROADCAST_ACTION);
		registerReceiver(receiver, intFilt);
	}
}