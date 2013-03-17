package rarus.eatery.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rarus.eatery.database.EateryDB;
import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.Menu;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Сервис взаимодейстфия приложения с веб-сервером 1-С
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class EateryWebService extends Service implements ServiceRequestResult {

	private EateryDB mDBManager;
	private EateryServiceBinder binder = new EateryServiceBinder();
	private ServiceAPI api;
	private SharedPreferences sp;

	@Override
	public void onSuccessfullRequest() {
		Log.d(EateryConstants.SERVICE_LOG_TAG,
				"[SERVICE] - SuccessfullReturned");
		try {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[SERVICE] - api.get");
			APIMessage message = api.get();
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[SERVICE] - code");
			switch (message.getCode()) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.SERVICE_LOG_TAG,
						"[SERVICE] - get menu code");
				List<Menu> list=(List<Menu>)message.getContent();
				Log.d(EateryConstants.SERVICE_LOG_TAG,
						"[SERVICE] - Servicel list getted");
				writeToDB(list);
				Intent intent = new Intent(EateryConstants.BROADCAST_ACTION);
				intent.putExtra(EateryConstants.SERVICE_RESULT, true);
				intent.putExtra(EateryConstants.SERVICE_RESULT_CODE, EateryConstants.GET_MENU_CODE);
				sendBroadcast(intent);
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
			}
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUnSuccessfullRequest() {
		Log.d(EateryConstants.SERVICE_LOG_TAG,
				"[SERVICE] - UnSuccessfullReturned");
		try {
			APIMessage message = api.get();
			switch (message.getCode()) {
			case EateryConstants.GET_MENU_CODE: {
				String error =(String) message.getContent();
				Intent intent = new Intent(EateryConstants.BROADCAST_ACTION);
				intent.putExtra(EateryConstants.SERVICE_RESULT, false);
				intent.putExtra(EateryConstants.SERVICE_RESULT_CODE, EateryConstants.GET_MENU_CODE);
				Log.d(EateryConstants.SERVICE_LOG_TAG,
						"[SERVICE] - UnSuccessfullReturned error:\n"+ error);
				intent.putExtra(EateryConstants.SERVICE_ERROR, error);
				sendBroadcast(intent);
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
			}
			case EateryConstants.PING_CODE: {
				String error =(String) message.getContent();
				Intent intent = new Intent(EateryConstants.BROADCAST_ACTION);
				intent.putExtra(EateryConstants.SERVICE_RESULT, false);
				intent.putExtra(EateryConstants.SERVICE_RESULT_CODE, EateryConstants.PING_CODE);
				Log.d(EateryConstants.SERVICE_LOG_TAG,
						"[SERVICE] - UnSuccessfullReturned error:\n"+ error);
				intent.putExtra(EateryConstants.SERVICE_ERROR, error);
				sendBroadcast(intent);
			}
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public IBinder onBind(Intent arg0) {
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - On bind");
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		return binder;
	}
	
	public void onCreate() {
		super.onCreate();
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - onCreate");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - onDestroy");
	}
	public void getMenu() {
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - Get menu");
		api = new ServiceAPI(this,PreferenceManager.getDefaultSharedPreferences(this));
		api.execute(new APIMessage(EateryConstants.GET_MENU_CODE, null));
	}

	public void setOrder() {
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - Set order");
		api = new ServiceAPI(this, PreferenceManager.getDefaultSharedPreferences(this));
		api.execute(new APIMessage(EateryConstants.SET_ORDER_CODE,null));
	}
	
	private void writeToDB(List<Menu> menu){
		for(Menu m:menu){
			java.util.Date date=new Date((long)m.getDate()*1000);
			Log.d(EateryConstants.GUI_LOG_TAG,
					"EateryWebService: Дата "+ date.toString()) ;
		}
		mDBManager=new EateryDB(getApplicationContext());
		mDBManager.saveMenu(menu);	
		List<Integer> dates= mDBManager.getMenuDates();
		for(Integer d:dates){
			java.util.Date date=new Date((long)d*1000);
			Log.d(EateryConstants.GUI_LOG_TAG,
					"MainActivity: Дата"+ date.toString()) ;
		}
		
	}
	public class EateryServiceBinder extends Binder {
		public EateryWebService getService() {
			return EateryWebService.this;
		}
	}
}
