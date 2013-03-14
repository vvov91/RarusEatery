package rarus.eatery.service;

import java.util.List;

import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.Menu;
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
	
	@Override
	protected void onPostExecute(APIMessage result) {
		super.onPostExecute(result);
		if (successfull) {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - successfull Request post execute");
			serviceResult.onSuccessfullRequest();
		} else{
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - unsuccessfull Request post execute");
			serviceResult.onUnSuccessfullRequest();
			}
	}
	private List<Menu> getMenu() {
		String login = sp.getString(EateryConstants.PREF_LOGIN, "mobile");
		String password = sp.getString(EateryConstants.PREF_PASSWORD, "mobile");
		String xml = XMLParser.getMenuXMLRequest();
		List<Menu> menu = null;
		Log.i(EateryConstants.SERVICE_LOG_TAG, "[API] - Login " + login);
		Log.i(EateryConstants.SERVICE_LOG_TAG, "[API] - Pass " + password);
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Getting xml");
		HTTPPostRequest request = new HTTPPostRequest(EateryConstants.URL,
				login, password, xml);
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Getted xml");
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - getMenuRequestResult:\n" + request.getResult());
			menu = XMLParser.parseXMLMenu(request.getResult());
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - result successfull");
			successfull = true;
		} else {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Error:\n" + 
		(request.getResult().startsWith("<html>")?request.getResult():request.getError()));
			successfull = false;
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Process error");
			//processError(request.getResult(), request.getError());
		}
		return menu;
	}	
	
	private Object setOrder(){
		return null;
	}
}
