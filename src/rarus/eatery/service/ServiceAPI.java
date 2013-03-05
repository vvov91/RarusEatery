package rarus.eatery.service;

import rarus.eatery.model.EateryConstants;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Класс асинхронного выполнения метов сервиса
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class ServiceAPI extends AsyncTask<APIMessage, Object, APIMessage> {

	private ServiceRequestResult serviceResult;
	private SharedPreferences sp;

	public ServiceAPI(ServiceRequestResult serviceResult, SharedPreferences sp) {
		super();
		this.serviceResult = serviceResult;
		this.sp = sp;
	}

	@Override
	protected APIMessage doInBackground(APIMessage... params) {
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Do in background start");
		APIMessage result = null;
		switch (params[0].getCode()) {
		case EateryConstants.GET_MENU_CODE: {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - getMenuCode");
			result=new APIMessage(EateryConstants.GET_MENU_CODE, getMenu());
		}
			break;
		case EateryConstants.SET_ORDER_CODE: {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - setOrderCode");
			result=new APIMessage(EateryConstants.SET_ORDER_CODE, setOrder());
		}
			break;

		}
		return result;
	}

}
