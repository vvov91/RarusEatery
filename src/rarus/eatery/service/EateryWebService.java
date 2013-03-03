package rarus.eatery.service;

import java.util.concurrent.ExecutionException;

import rarus.eatery.database.*;
import rarus.eatery.model.*;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * ������ �������������� ���������� � ���-�������� 1-�
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class EateryWebService extends Service implements ServiceRequestResult {

	private DBManager db;
	private EateryServiceBinder binder = new EateryServiceBinder();
	private ServiceAPI api;
	SharedPreferences sp;

	@Override
	public void onSuccessfullRequest() {
		Log.d(EateryConstants.SERVICE_LOG_TAG,
				"[SERVICE] - SuccessfullReturned");
		try {
			APIMessage message = api.get();
			switch (message.getCode()) {
			case EateryConstants.GET_MENU_CODE: {
				writeToDB((Menu)message.getContent());
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

	public IBinder onBind(Intent arg0) {
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - On bind");
		sp = PreferenceManager.getDefaultSharedPreferences(this);
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
		api = new ServiceAPI(this);
		api.execute(new APIMessage(EateryConstants.GET_MENU_CODE, null));
	}

	public void setOrder() {
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[SERVICE] - Set order");
		api = new ServiceAPI(this);
		api.execute(new APIMessage(EateryConstants.SET_ORDER_CODE,null));
	}
	
	private void writeToDB(Menu menu){
		
	}
	class EateryServiceBinder extends Binder {
		EateryWebService getService() {
			return EateryWebService.this;
		}
	}

}
