package rarus.eatery.service;

import java.util.List;
import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.Preference;
import rarus.eatery.model.RarusMenu;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * ����� ������������ ���������� ����� �������
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class ServiceAPI extends AsyncTask<APIMessage, Object, APIMessage> {
	public static final String SERV_LOGIN = "mobileUser";
	public static final String SERV_PASSWORD = "mobileUser";
	private ServiceRequestResult serviceResult;
	private boolean successfull;
	private String mError;
	private String URL;

	public ServiceAPI(ServiceRequestResult serviceResult, SharedPreferences sp) {
		super();
		this.serviceResult = serviceResult;
	}

	@Override
	protected APIMessage doInBackground(APIMessage... params) {
		successfull = false;
		APIMessage result = null;
		Log.d(this.getClass().toString(), "[API] - execute start");
		if (!connectionTest()) {
			result = new APIMessage(EateryWebService.PING_CODE, null);
			return result;
		}
		Log.d(this.getClass().toString(), "[API] - good connection");
		switch (params[0].getCode()) {
		case EateryWebService.GET_MENU_CODE: {
			Log.d(this.getClass().toString(), "[API] - getMenuCode");
			result = new APIMessage(EateryWebService.GET_MENU_CODE, getMenu());
		}
			break;
		case EateryWebService.SET_ORDER_CODE: {
			Log.d(this.getClass().toString(), "[API] - setOrderCode");
			result = new APIMessage(EateryWebService.SET_ORDER_CODE,
					setOrder((List<RarusMenu>) params[0].getContent()));
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

	private boolean connectionTest() {
		URL = Preference.getFirstURL();
		Log.d(this.getClass().toString(), "[API] - connection test");
		Log.d(this.getClass().toString(), "[API] - connection test URL: \n"
				+ URL);
		if (ping())
			return true;
		//
		// Error process code.
		//
		URL = Preference.getSecondURL();
		Log.d(this.getClass().toString(), "[API] - connection test URL: \n"
				+ URL);
		return ping();
	}

	private boolean ping() {
		String xml = XMLParser.pingXml();
		HTTPPostRequest request = new HTTPPostRequest(URL, SERV_LOGIN,
				SERV_PASSWORD, xml);

		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			String res = XMLParser.parseXMLPing(request.getResult());
			Log.d(this.getClass().toString(), "[API] - Ping res \n" + res);
			if (res.equals("User authentication error")) {
				mError = "User authentication error";
				return false;
			} else
				return true;

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
		HTTPPostRequest request = new HTTPPostRequest(URL, SERV_LOGIN,
				SERV_PASSWORD, xml);
		Log.d(this.getClass().toString(), "[API] - Getted xml");
		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			Log.d(this.getClass().toString(), "[API] - getMenuRequestResult:\n"
					+ request.getResult());
			menu = XMLParser.parseXMLMenu(request.getResult());

			Log.d(this.getClass().toString(), "[API] - result successfull");
			successfull = true;
		} else {
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

	private Object setOrder(List<RarusMenu> orders) {
		List<RarusMenu> menu = null;
		String xml = XMLParser.setMenuXMLRequest(orders);
		Log.i(this.getClass().toString(), "[API] - setOrder xml:\n" + xml);
		Log.d(this.getClass().toString(), "[API] - setOrder request");
		HTTPPostRequest request = new HTTPPostRequest(URL,
				SERV_LOGIN, SERV_PASSWORD, xml);
		Log.d(this.getClass().toString(), "[API] - setOrder request sent");

		if (!request.getResult().equals("")
				&& !request.getResult().startsWith("<html>")) {
			menu = XMLParser.parseXMLSetOrder(request.getResult());
			Log.d(this.getClass().toString(), "[API] - result successfull");
			successfull = true;
		} else {
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
}
