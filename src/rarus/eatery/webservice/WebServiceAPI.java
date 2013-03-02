package rarus.eatery.webservice;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import rarus.eatery.model.DayMenu;
import rarus.eatery.model.Dish;
import rarus.eatery.ui.MenuList;
import rarus.eatery.ui.MenuOnDate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

public class WebServiceAPI extends AsyncTask<String, Boolean, MenuList> {

	private static final String SOAP_PREFIX = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String MOB_PREFIX = "http://mobileEda";
	final String LOG_TAG = "SERV_API";
	final static String URL = "http://178.219.241.102:8090/DiningRoomTest_kuev/ws/mobileEda";
	PreferenceManager sp;
	
	public WebServiceInterface service;
	boolean successfull;

	public WebServiceAPI(WebServiceInterface i) {
		super();
		service = i;
	}

	@Override
	protected MenuList doInBackground(String... args) {
		Log.d(LOG_TAG, "Do in background start");
		String login = args[0];
		String password = args[1];
		String xml = getMenuXMLRequest();
		MenuList menu = null;
		Log.d(LOG_TAG, "Geting xml");
		

		HTTPPostRequest request = new HTTPPostRequest(URL, login, password, xml);
		Log.d(LOG_TAG, "Getted xml");
		Log.d(LOG_TAG, "error:"+request.getError());
		if (!request.getResult().equals("")) {
			Log.d(LOG_TAG, request.getResult());
			menu = parseXMLMenu(request.getResult());
			successfull = true;
		} else {
			Log.d(LOG_TAG, request.getError());
			successfull = false;
		}
		return menu;
	}

	@Override
	protected void onPostExecute(MenuList result) {
		super.onPostExecute(result);
		if (successfull) {
			Log.i(LOG_TAG, "Menu get" + result);
			service.onServiceSuccessfullRequest();
		} else
			service.onServiceErrorRequest();
	}

	private String getMenuXMLRequest() {
		XmlSerializer sz = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			sz.setOutput(writer);
			sz.setPrefix("soapenv", SOAP_PREFIX);
			sz.setPrefix("mob", MOB_PREFIX);
			sz.startTag(SOAP_PREFIX, "Envelope");
			sz.startTag(SOAP_PREFIX, "Header");
			sz.endTag(SOAP_PREFIX, "Header");
			sz.startTag(SOAP_PREFIX, "Body");
			sz.startTag(MOB_PREFIX, "getMenu");
			sz.startTag(MOB_PREFIX, "getMenuData");
			sz.endTag(MOB_PREFIX, "getMenuData");
			sz.endTag(MOB_PREFIX, "getMenu");
			sz.endTag(SOAP_PREFIX, "Body");
			sz.endTag(SOAP_PREFIX, "Envelope");
			sz.endDocument();
			return writer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private MenuList parseXMLMenu(String xml) {
		try {
			MenuList menu = new MenuList();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));
			int eventType = xpp.getEventType();
			String tmp = "";
			xpp.next();
			String name="";
			while(!name.equals("menuItems")){
				if (xpp.getEventType() == XmlPullParser.START_TAG){
					Log.d(LOG_TAG, "START_TAG: name = " + xpp.getName());
					name=xpp.getName();
				}			
				xpp.next();
			}
			MenuOnDate menuOnDate=null;
			Integer date=null;
			List <Dish> dishList=new ArrayList<Dish>();
			int dishId=0;
			String dishName="";
			String description="";
			float price=0;
			boolean portioned = false;
			String rating="";
			boolean preorder = false;
			while(xpp.getEventType() != XmlPullParser.END_DOCUMENT){
				
				
				switch (xpp.getEventType()) {
				// начало документа
				case XmlPullParser.START_DOCUMENT:					
					break;
				// начало тэга
				case XmlPullParser.START_TAG:
					name=xpp.getName();
					//Log.d(LOG_TAG,"Start:"+xpp.getName());
					break;
				// конец тэга
				case XmlPullParser.END_TAG:
					if(xpp.getName().equals("menu")){
						DayMenu dm = new DayMenu(0, 0, dishId, dishName, description, portioned, 
								price, rating, preorder, 0f, 0);
						//Log.i("Dish_log",d.toString());
						menuOnDate.addDish(dm);
					}
					if(xpp.getName().equals("menuItems")){
						Log.d(LOG_TAG,"End:"+xpp.getName());
						menu.addMenuOnDate(menuOnDate);
						menuOnDate=null;
					}
					
					name="";
					break;
				// содержимое тэга
				case XmlPullParser.TEXT:
					if(name.equals("dateTime")){
						Log.d(LOG_TAG,"Date:"+xpp.getText());
						date=dateToUnix(xpp.getText());
						menuOnDate=new MenuOnDate(date);
					}
					if(name.equals("dishId")){
						Log.d(LOG_TAG,"DishId:"+xpp.getText());
						dishId=Integer.parseInt(xpp.getText());
					}
					if(name.equals("name")){						
						dishName=xpp.getText();
					}
					if(name.equals("description")){
						description=xpp.getText();
					}
					if(name.equals("price")){
						price=(float)Double.parseDouble(xpp.getText());
					}
					if(name.equals("portioned")){
						portioned=(xpp.getText() == "true" ? true : false);
					}
					if(name.equals("rating")){
						rating=xpp.getText();
					}
					if(name.equals("preorder")){
						preorder=(xpp.getText() == "true" ? true : false);
					}
					break;
					
				default:
					break;
				}
				// следующий элемент
				xpp.next();
			}			
						
			return menu;
		} catch (XmlPullParserException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private Integer dateToUnix(String date){
		Date d=new Date(Integer.parseInt(date.substring(0,4))-1900, Integer.parseInt(date.substring(5,7))-1, Integer.parseInt(date.substring(8,10)));
		
		return (int)(d.getTime()/1000);
	}
	
	
}
