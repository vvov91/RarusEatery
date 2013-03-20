package rarus.eatery.service;

import java.util.Date;
import java.util.List;

import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.RarusMenu;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Класс асинхронного выполнения метов сервиса 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class ServiceAPI extends AsyncTask<APIMessage, Object, APIMessage> {

	private ServiceRequestResult serviceResult;
	private SharedPreferences sp;
	private boolean successfull;
	private String mError;
	private String URL = EateryConstants.URL_1;
	public ServiceAPI(ServiceRequestResult serviceResult, SharedPreferences sp) {
		super();
		this.serviceResult = serviceResult;
		this.sp = sp;
	}

	@Override
	protected APIMessage doInBackground(APIMessage... params) {
		successfull = false;		
		APIMessage result = null;
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - execute start");
		if (!connectionTest()) {
			result = new APIMessage(EateryConstants.PING_CODE, null);
			return result;
		}
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - good connection");
		switch (params[0].getCode()) {
		case EateryConstants.GET_MENU_CODE: {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - getMenuCode");
			result = new APIMessage(EateryConstants.GET_MENU_CODE, getMenu());
		}
			break;
		case EateryConstants.SET_ORDER_CODE: {
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - setOrderCode");
			result = new APIMessage(EateryConstants.SET_ORDER_CODE, setOrder((List<RarusMenu>)params[0].getContent()));
		}
			break;

		}
		return result;
	}

	@Override
	protected void onPostExecute(APIMessage result) {
		super.onPostExecute(result);
		if (successfull) {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - successfull Request post execute");
			serviceResult.onSuccessfullRequest();
		} else {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - unsuccessfull Request post execute");
			result.setContent(mError);
			serviceResult.onUnSuccessfullRequest();
		}
	}

	private boolean connectionTest(){
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - connection test");
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - connection test URL: \n"+URL);
		if(ping()) return true;
		//
		//Error process code.
		//
		URL=EateryConstants.URL_2;
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - connection test URL: \n"+URL);
		return ping();		
	}
	private boolean ping() {		
		String xml = XMLParser.pingXml(sp);		
		HTTPPostRequest request = new HTTPPostRequest(URL,
				EateryConstants.SERV_LOGIN, EateryConstants.SERV_PASSWORD, xml);
		
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			String res = XMLParser.parseXMLPing(request.getResult());
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Ping res \n"+ res);
			if (res.equals("User authentication error")) {
				mError = "User authentication error";
				return false;
			}
			else
				return true;

		} else {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - Error:\n"
							+ (request.getResult().startsWith("<html>") ? request
									.getResult() : request.getError()));
			successfull = false;
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Process error");
			processError(request.getResult(), request.getError());
		}
		return false;
	}

	private List<RarusMenu> getMenu() {
		String xml = XMLParser.getMenuXMLRequest(sp);
		List<RarusMenu> menu = null;		
		HTTPPostRequest request = new HTTPPostRequest(URL,
				EateryConstants.SERV_LOGIN, EateryConstants.SERV_PASSWORD, xml);
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Getted xml");
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - getMenuRequestResult:\n" + request.getResult());
			menu = XMLParser.parseXMLMenu(request.getResult());
			
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - result successfull");
			successfull = true;
		} else {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - Error:\n"
							+ (request.getResult().startsWith("<html>") ? request
									.getResult() : request.getError()));
			successfull = false;
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Process error");
			processError(request.getResult(), request.getError());
		}
		return menu;
	}

	private Object setOrder(List<RarusMenu> orders) {
		List<RarusMenu> menu = null;
		String xml = XMLParser.setMenuXMLRequest(sp,orders);
		Log.i(EateryConstants.SERVICE_LOG_TAG, "[API] - setOrder xml:\n"+xml);
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - setOrder request");
		HTTPPostRequest request = new HTTPPostRequest(URL,
				EateryConstants.SERV_LOGIN, EateryConstants.SERV_PASSWORD, xml);
		Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - setOrder request sent");
		
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			menu = XMLParser.parseXMLSetOrder(request.getResult());			
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - result successfull");
			successfull = true;
		} else {
			Log.d(EateryConstants.SERVICE_LOG_TAG,
					"[API] - Error:\n"
							+ (request.getResult().startsWith("<html>") ? request
									.getResult() : request.getError()));
			successfull = false;
			Log.d(EateryConstants.SERVICE_LOG_TAG, "[API] - Process error");
			processError(request.getResult(), request.getError());
		}
		return menu;
	}

	private void processError(String result, String error) {
		if (result.startsWith("<html>")) {
			error = XMLParser.processHtmlError(result);
		}
		Log.e(EateryConstants.SERVICE_LOG_TAG, "[API] - error:\n" + error);
		mError = error;
	}
}
