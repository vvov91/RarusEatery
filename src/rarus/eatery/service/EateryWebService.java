package rarus.eatery.service;


import java.util.List;
import java.util.concurrent.ExecutionException;
import rarus.eatery.database.EateryDB;
import rarus.eatery.model.RarusMenu;
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
	
	public static final String SERVICE_RESULT = "rarus.eatery.service.result";
	public static final String SERVICE_RESULT_CODE = "rarus.eatery.service.resultcode";
	public static final String SERVICE_ERROR = "rarus.eatery.service.error";
	public static final String BROADCAST_ACTION = "rarus.eatery.service.broadcast";
	public static final int PING_CODE = 0;
	public static final int GET_MENU_CODE = 1;
	public static final int SET_ORDER_CODE = 2;	
	public static final String SERVICE_LOG_TAG = "rarus.eatery.service";
	
	private EateryServiceBinder binder = new EateryServiceBinder();
	private ServiceAPI_Async api;
	private SharedPreferences sp;

	@Override
	public void onSuccessfullRequest() {
		Log.d(this.getClass().toString(),
				"[SERVICE] - SuccessfullReturned");
		try {
			Log.d(this.getClass().toString(), "[SERVICE] - api.get");
			APIMessage message = api.get();
			Log.d(this.getClass().toString(), "[SERVICE] - code");
			switch (message.getCode()) {
			case GET_MENU_CODE: {
				Log.d(this.getClass().toString(),
						"[SERVICE] - get menu code");
				List<RarusMenu> list = (List<RarusMenu>) message.getContent();
				Log.d(this.getClass().toString(),
						"[SERVICE] - Servicel list getted");
				Intent intent = new Intent( BROADCAST_ACTION);
				intent.putExtra( SERVICE_RESULT, true);
				intent.putExtra( SERVICE_RESULT_CODE,
						 GET_MENU_CODE);
				sendBroadcast(intent);
			}
				break;
			case  SET_ORDER_CODE: {
				Log.d(this.getClass().toString(),
						"[SERVICE] - Order succssessfuly send");
				getMenu();
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
		Log.d(this.getClass().toString(),
				"[SERVICE] - UnSuccessfullReturned");
		try {
			APIMessage message = api.get();
			switch (message.getCode()) {
			case  GET_MENU_CODE: {
				String error = (String) message.getContent();
				Intent intent = new Intent( BROADCAST_ACTION);
				intent.putExtra( SERVICE_RESULT, false);
				intent.putExtra( SERVICE_RESULT_CODE,
						 GET_MENU_CODE);
				Log.d(this.getClass().toString(),
						"[SERVICE] - UnSuccessfullReturned error:\n" + error);
				intent.putExtra( SERVICE_ERROR, error);
				sendBroadcast(intent);
			}
				break;
			case  SET_ORDER_CODE: {
			}
			case  PING_CODE: {
				String error = (String) message.getContent();
				Intent intent = new Intent( BROADCAST_ACTION);
				intent.putExtra( SERVICE_RESULT, false);
				intent.putExtra( SERVICE_RESULT_CODE,
						 PING_CODE);
				Log.d(this.getClass().toString(),
						"[SERVICE] - UnSuccessfullReturned error:\n" + error);
				intent.putExtra( SERVICE_ERROR, error);
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
		Log.d(this.getClass().toString(), "[SERVICE] - On bind");
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		return binder;
	}

	public void onCreate() {
		super.onCreate();
		Log.d(this.getClass().toString(), "[SERVICE] - onCreate");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(this.getClass().toString(), "[SERVICE] - onDestroy");
	}
	
	public void update(){
		Log.d(this.getClass().toString(), "[SERVICE] - Starting update");	
		setMenu();		
	}
	
	public void getMenu() {
		Log.d(this.getClass().toString(), "[SERVICE] - Get menu");
		api = new ServiceAPI_Async(this,getApplicationContext());
		api.execute(new APIMessage( GET_MENU_CODE, null));
	}

	public void setMenu() {
		Log.d(this.getClass().toString(), "[SERVICE] - Set menu");
		api = new ServiceAPI_Async(this,getApplicationContext());
		api.execute(new APIMessage( SET_ORDER_CODE, null));
	}



	public class EateryServiceBinder extends Binder {
		public EateryWebService getService() {
			return EateryWebService.this;
		}
	}
}
