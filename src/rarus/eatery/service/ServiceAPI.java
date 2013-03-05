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
	private boolean successfull;

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
			result = new APIMessage(EateryConstants.GET_MENU_CODE, getMenu());
		}
			break;
		case EateryConstants.SET_ORDER_CODE: {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - setOrderCode");
			result = new APIMessage(EateryConstants.SET_ORDER_CODE, setOrder());
		}
			break;

		}
		return result;
	}

	private ServiceList getMenu() {
		String login = sp.getString(EateryConstants.PREF_LOGIN, "error");
		String password = sp.getString(EateryConstants.PREF_PASSWORD, "error");
		String xml = XMLParser.getMenuXMLRequest();
		ServiceList list = null;
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Getting xml");
		HTTPPostRequest request = new HTTPPostRequest(EateryConstants.URL,
				login, password, xml);
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Getted xml");
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - getMenuRequestResult:\n" + request.getResult());
			list = XMLParser.parseXMLMenu(request.getResult());
			successfull = true;
		} else {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Error:\n" + request.getError());
			successfull = false;
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Process error");
			//processError(request.getResult(), request.getError());
		}
		return list;
	}	
	
	private Object setOrder(){
		return null;
	}
}
