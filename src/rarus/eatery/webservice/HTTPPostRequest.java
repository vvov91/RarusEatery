package rarus.eatery.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

class HTTPPostRequest {

	private final String LOG_TAG = "HTTP_Request";

	private String _result = "";
	private String _error = "";

	/**
	 * Send request to the server. This class must be run in the own thread
	 * 
	 * @param url
	 *            URL
	 * @param user
	 *            authentication user
	 * @param password
	 *            authentication password
	 * @param xml
	 *            request package
	 */
	public HTTPPostRequest(String url, String user, String password, String xml) {
		sendRequest(url, user, password, xml);
	}

	public String getResult() {
		return _result;
	}

	public String getError() {
		return _error;
	}

	private String sendRequest(String url, String user, String password,
			String xml) {
		try {

			final DefaultHttpClient httpClient = new DefaultHttpClient();

			// аутентификационные данные в пакете
			Credentials creds = null;
			creds = new UsernamePasswordCredentials(user, password);
			CredentialsProvider credsProv = new BasicCredentialsProvider();
			credsProv.setCredentials(AuthScope.ANY, creds);
			httpClient.setCredentialsProvider(credsProv);

			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 15000);
			// устанавливаем параметры
			HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);
			// С помощью метода POST отправляем конверт
			HttpPost httppost = new HttpPost(url);
			// и заголовки
			// httppost.setHeader("soapaction", _soap_action);
			httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
			HttpEntity entity = new StringEntity(xml, "UTF-8");
			httppost.setEntity(entity);

			HttpResponse httpRresp = httpClient.execute(httppost);
			getResponse(httpRresp);

			httpClient.getConnectionManager().shutdown();

		} catch (ConnectTimeoutException cte) {
			String msg = "Время подключения к серверу истекло";
			Log.e(getClass().getName(), msg);
			_error = msg;
		} catch (Exception e) {
			setError(e);
		}
		return xml;
	}

	private boolean getResponse(HttpResponse httpResponse) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			

			String sResponse;
			StringBuilder s = new StringBuilder();
			while ((sResponse = reader.readLine()) != null) {				
				s = s.append(sResponse);
			}
			_result = s.toString().trim();

			return true;
		} catch (UnsupportedEncodingException e) {
			setError(e);
			return false;
		} catch (IllegalStateException e) {
			setError(e);
			return false;
		} catch (IOException e) {
			setError(e);
			return false;
		} catch (Exception e) {
			setError(e);
			return false;
		}
	}

	private void setError(Exception e) {
		Log.e(LOG_TAG, e.getLocalizedMessage());
		_error = e.getLocalizedMessage();
	}
}