package rarus.eatery.service;

import java.util.List;

import rarus.eatery.database.EateryDB;
import rarus.eatery.model.Preference;
import rarus.eatery.model.RarusMenu;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Класс асинхронного выполнения метов сервиса
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class ServiceAPI_Async extends AsyncTask<APIMessage, Object, APIMessage> {
	public static final String SERV_LOGIN = "mobileUser";
	public static final String SERV_PASSWORD = "mobileUser";
	private ServiceRequestResult serviceResult;
	private EateryDB mDBManager;
	private boolean successfull;
	private String mError;
	private String URL;

	public ServiceAPI_Async(ServiceRequestResult serviceResult, Context context) {
		super();
		this.serviceResult = serviceResult;
		mDBManager=new EateryDB(context);
	}

	@Override
	protected APIMessage doInBackground(APIMessage... params) {
		Log.d(this.getClass().toString(), "[API] - Do in background start");
		successfull = false;
		APIMessage result = null;		
		if (!connectionTest()) {
			result = new APIMessage(EateryWebService.PING_CODE, null);
			return result;
		}
		Log.d(this.getClass().toString(), "[API] - good connection");
		if(isCancelled()) return null;
		switch (params[0].getCode()) {
		case EateryWebService.GET_MENU_CODE: {
			Log.d(this.getClass().toString(), "[API] - getMenuCode");
			result = new APIMessage(EateryWebService.GET_MENU_CODE, getMenu());
		}
			break;
		case EateryWebService.SET_ORDER_CODE: {
			Log.d(this.getClass().toString(), "[API] - setOrderCode");
			result = new APIMessage(EateryWebService.SET_ORDER_CODE,
					setOrder());
		}
			break;

		}
		return result;
	}

	@Override
	protected void onPostExecute(APIMessage result) {
		super.onPostExecute(result);
		if (successfull) {
			Log.d(this.getClass().toString(),
					"[API] - successfull Request post execute");
			serviceResult.onSuccessfullRequest();
		} else {
			Log.d(this.getClass().toString(),
					"[API] - unsuccessfull Request post execute");
			result.setContent(mError);
			serviceResult.onUnSuccessfullRequest();
		}
	}
	@Override
    protected void onCancelled() {
      super.onCancelled();
      Log.d(this.getClass().toString(), "Cancel");

    }
	private boolean connectionTest() {
		if(isCancelled()) return false;
		if(Preference.getWi_FiFlag()){
			Log.d(this.getClass().toString(), "[API] - only Wi-Fi");
			if(!Utility.hasWiFiConnection()){
				mError="No avaliable Wi-Fi connections";
				return false;
			}
		}
		else{
			Log.d(this.getClass().toString(), "[API] - any Connection");
			if(!Utility.hasInternetConnection()){
				mError="No avaliable connections";
				return false;
			}
		}
		Utility.hasInternetConnection();
		URL = Preference.getSecondURL();
		Log.d(this.getClass().toString(), "[API] - connection test");
		Log.d(this.getClass().toString(), "[API] - connection test URL: \n"
				+ URL);
		if(isCancelled()) return false;
		if (ping())
			return true;
		if(isCancelled()) return false;
		Log.d(this.getClass().toString(), "[API] - mError :"+ mError);
		
		if(!mError.equals("Timeout Exception")){
			return false;
		}
		//
		// Error process code.
		//
		URL = Preference.getFirstURL();
		Log.d(this.getClass().toString(), "[API] - connection test URL: \n"
				+ URL);
		if(isCancelled()) return false;
		return ping();
	}

	private boolean ping() {
		String xml = XMLParser.pingXml();
		long start=System.currentTimeMillis();
		if(isCancelled()) return false;
		HTTPPostRequest request = new HTTPPostRequest(URL, SERV_LOGIN,
				SERV_PASSWORD, xml);
		if(isCancelled()) return false;
		long stop=System.currentTimeMillis();
		Log.d(this.getClass().toString(), "[API] - Request time" + (start-stop));
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			if(isCancelled()) return false;
			start=System.currentTimeMillis();
			String res = XMLParser.parseXMLPing(request.getResult());
			stop=System.currentTimeMillis();
			if(isCancelled()) return false;
			Log.d(this.getClass().toString(), "[API] - Parse getMenu time" + (start-stop));
			Log.d(this.getClass().toString(), "[API] - Ping res \n" + res);
			if (res.equals("OK")) {
				mError = "";
				return true;
			} else{
				mError=res;
				return false;
			}
		} else {
			Log.d(this.getClass().toString(),
					"[API] - Error:\n"
							+ (request.getResult().startsWith("<html>") ? request
									.getResult() : request.getError()));
			successfull = false;
			Log.d(this.getClass().toString(), "[API] - Process error");
			processError(request.getResult(), request.getError());
		}
		return false;
	}

	private List<RarusMenu> getMenu() {
		String xml = XMLParser.getMenuXMLRequest();
		List<RarusMenu> menu = null;
		if(isCancelled()) return null;
		long start=System.currentTimeMillis();
		HTTPPostRequest request = new HTTPPostRequest(URL, SERV_LOGIN,
				SERV_PASSWORD, xml);
		long stop=System.currentTimeMillis();
		if(isCancelled()) return null;
		Log.d(this.getClass().toString(), "[API] - Request getMenu time" + (start-stop));
		Log.d(this.getClass().toString(), "[API] - Getted xml");
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			Log.d(this.getClass().toString(), "[API] - getMenuRequestResult:\n"
					+ request.getResult());
			if(isCancelled()) return null;
			start=System.currentTimeMillis();
			menu = XMLParser.parseXMLMenu(request.getResult());
			stop=System.currentTimeMillis();
			if(isCancelled()) return null;
			Log.d(this.getClass().toString(), "[API] - Parse getMenu time" + (start-stop));
			Log.d(this.getClass().toString(), "[API] - result successfull");
			successfull = true;
			Log.d(this.getClass().toString(),
					"[API] - writing to DB");
			writeToDB(menu);
		} else {
			if(isCancelled()) return null;
			Log.d(this.getClass().toString(),
					"[API] - Error:\n"
							+ (request.getResult().startsWith("<html>") ? request
									.getResult() : request.getError()));
			successfull = false;
			Log.d(this.getClass().toString(), "[API] - Process error");
			processError(request.getResult(), request.getError());
		}
		return menu;
	}

	private Object setOrder() {
		List<RarusMenu> orders=mDBManager.getOrdersNotSent();
		List<RarusMenu> menu = null;
		String xml = XMLParser.setMenuXMLRequest(orders);
		Log.i(this.getClass().toString(), "[API] - setOrder xml:\n" + xml);
		Log.d(this.getClass().toString(), "[API] - setOrder request");
		if(isCancelled()) return null;
		HTTPPostRequest request = new HTTPPostRequest(URL,
				SERV_LOGIN, SERV_PASSWORD, xml);
		if(isCancelled()) return null;
		Log.d(this.getClass().toString(), "[API] - setOrder request sent");

		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			if(isCancelled()) return null;
			menu = XMLParser.parseXMLSetOrder(request.getResult());
			if(isCancelled()) return null;
			Log.d(this.getClass().toString(), "[API] - result successfull");
			successfull = true;
		} else {
			if(isCancelled()) return null;
			Log.d(this.getClass().toString(),
					"[API] - Error:\n"
							+ (request.getResult().startsWith("<html>") ? request
									.getResult() : request.getError()));
			successfull = false;
			Log.d(this.getClass().toString(), "[API] - Process error");
			processError(request.getResult(), request.getError());
		}
		return menu;
	}

	private void processError(String result, String error) {
		if (result.startsWith("<html>")) {
			error = XMLParser.processHtmlError(result);
		}
		Log.e(this.getClass().toString(), "[API] - error:\n" + error);
		mError = error;
	}
	
	private void writeToDB(List<RarusMenu> menu) {
		if(isCancelled()) return;
		Log.d(this.getClass().toString(), "[SERVICE]: Запись меню в БД ");
		mDBManager.deleteMenu();
		mDBManager.saveMenu(menu);	
	}
}
