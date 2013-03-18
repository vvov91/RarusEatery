package rarus.eatery.activity;

import java.util.Date;
import java.util.List;

import rarus.eatery.database.EateryDB;
import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.RarusMenu;
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

public class MainActivity extends Activity {
	Intent serviceIntent;
	ServiceConnection connection;
	EateryWebService client;
	BroadcastReceiver receiver;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService();
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity onServiceConnected");
				client = ((EateryWebService.EateryServiceBinder) binder)
						.getService();				
				client.getMenu();
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity onServiceDisconnected");
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(serviceIntent, connection, 0);
		Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity: onStart()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
		unregisterReceiver(receiver);
		Log.d(EateryConstants.GUI_LOG_TAG, "MainActivity: onDestroy()");
	}

	private void startService() {
		serviceIntent = new Intent(this, EateryWebService.class);
		startService(serviceIntent);
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity onServiceConnected");
				client = ((EateryWebService.EateryServiceBinder) binder)
						.getService();
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity onServiceDisconnected");
			}
		};

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = intent.getBooleanExtra(
						EateryConstants.SERVICE_RESULT, false);
				recive(result, intent);
			}
		};
		IntentFilter intFilt = new IntentFilter(
				EateryConstants.BROADCAST_ACTION);
		registerReceiver(receiver, intFilt);
	}

	private void recive(boolean result, Intent intent) {
		Log.d(EateryConstants.GUI_LOG_TAG,
				"MainActivity: Полученно сообщение от сервиса");
		if (result) {			
			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			Log.d(EateryConstants.GUI_LOG_TAG,
					"MainActivity: Запрос сервиса успешен");
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: Полученно меню");
				EateryDB db=new EateryDB(getApplicationContext());
				List<Integer> dates= db.getMenuDates();
				for(Integer d:dates){
					java.util.Date date=new Date((long)d*1000);
					Log.d(EateryConstants.GUI_LOG_TAG,
							"MainActivity: Дата"+ date.toString()) ;
					List<RarusMenu> menu=db.getMenu(d);
					for(RarusMenu m:menu ){
						Log.i(EateryConstants.GUI_LOG_TAG,
								"MainActivity:  меню "+ m.toString()) ;
					}
				}
				
			
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
			}
				break;
			case EateryConstants.PING_CODE: {
			}
				break;
			}

		} else {
			Log.d(EateryConstants.GUI_LOG_TAG,
					"MainActivity: Запрос сервиса неудачен");
			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			String error = intent.getStringExtra(EateryConstants.SERVICE_ERROR);
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при получении меню:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при отправке заказа:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
			}
				break;
			case EateryConstants.PING_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при соединеннии с сервером:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				
			}
				break;
			}
		}
	}
}