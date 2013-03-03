package rarus.eatery.service;

import rarus.eatery.model.EateryConstants;
import android.os.AsyncTask;

/**
 * Класс асинхронного выполнения метов сервиса
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */
public class ServiceAPI extends AsyncTask<APIMessage, Object, APIMessage> {

	ServiceRequestResult serviceResult;

	public ServiceAPI(ServiceRequestResult serviceResult) {
		super();
		this.serviceResult = serviceResult;
	}

	@Override
	protected APIMessage doInBackground(APIMessage... params) {

		return null;
	}

}
