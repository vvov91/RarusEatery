package rarus.eatery.service;

import rarus.eatery.model.EateryConstants;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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

		return null;
	}

}
