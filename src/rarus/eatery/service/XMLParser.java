package rarus.eatery.service;

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

import rarus.eatery.model.Preference;
import rarus.eatery.model.RarusMenu;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;

/**
 *  ласс с набором статических методов дл€ формировани€ XML запросов и разбора
 * XML ответов вебсервера 1-—
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */

public class XMLParser {
	public static final String SOAP_PREFIX = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String MOB_PREFIX = "http://mobileEda";	
	public static String processHtmlError(String html) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(html));
			xpp.next();
			String name = "";
			String error = "";
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = xpp.getName();
					break;
				case XmlPullParser.END_TAG:
					break;

				case XmlPullParser.TEXT:
					if (name.equals("h1")) {
						error = xpp.getText();
					}
					break;

				default:
					break;
				}
				xpp.next();
			}

			return error;
		} catch (XmlPullParserException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String pingXml() {
		XmlSerializer sz = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String cardCode =Preference.getCardNumber();
		Log.i(XMLParser.class.toString(),"XML_Parser  card number  "+ cardCode );
		try {
			sz.setOutput(writer);
			sz.setPrefix("soapenv", SOAP_PREFIX);
			sz.setPrefix("mob", MOB_PREFIX);
			sz.startTag(SOAP_PREFIX, "Envelope");
			sz.startTag(SOAP_PREFIX, "Header");
			sz.endTag(SOAP_PREFIX, "Header");
			sz.startTag(SOAP_PREFIX, "Body");
			sz.startTag(MOB_PREFIX, "ping");
			sz.startTag(MOB_PREFIX, "loginStructure");
			sz.startTag(MOB_PREFIX, "cardNumber");
			sz.text(cardCode);
			sz.endTag(MOB_PREFIX, "cardNumber");
			sz.endTag(MOB_PREFIX, "loginStructure");
			sz.endTag(MOB_PREFIX, "ping");
			sz.endTag(SOAP_PREFIX, "Body");
			sz.endTag(SOAP_PREFIX, "Envelope");
			sz.endDocument();
			return writer.toString();
		} catch (IOException exception) {
			Log.e(XMLParser.class.toString(), "[XMLParser] IOException "+ exception.getLocalizedMessage());
			return null;
		}
	}

	public static String setMenuXMLRequest(List<RarusMenu> orders){
		XmlSerializer sz = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String cardCode=Preference.getCardNumber();
		try {
			sz.setOutput(writer);
			sz.setPrefix("soapenv", SOAP_PREFIX);
			sz.setPrefix("mob", MOB_PREFIX);
			sz.startTag(SOAP_PREFIX, "Envelope");
			sz.startTag(SOAP_PREFIX, "Header");
			sz.endTag(SOAP_PREFIX, "Header");
			sz.startTag(SOAP_PREFIX, "Body");
			sz.startTag(MOB_PREFIX, "setMenu");
			sz.startTag(MOB_PREFIX, "loginStructure");
			sz.startTag(MOB_PREFIX, "cardNumber");
			sz.text(cardCode);
			sz.endTag(MOB_PREFIX, "cardNumber");
			sz.endTag(MOB_PREFIX, "loginStructure");
			Integer date=orders.get(0).getDate();
			boolean first=true;
			sz.startTag(MOB_PREFIX, "setMenuData");
			sz.startTag(MOB_PREFIX, "menuItems");
			sz.startTag(MOB_PREFIX, "dateTime");
			sz.text(dateFromUnix(date));
			sz.endTag(MOB_PREFIX, "dateTime");
			sz.startTag(MOB_PREFIX,"timeStamp");
			sz.text(dateFromUnix(orders.get(0).getTimestamp()));
			sz.endTag(MOB_PREFIX,"timeStamp");			
			for(RarusMenu dish:orders){
				if(date!=dish.getDate()){
					sz.endTag(MOB_PREFIX, "menuItems");
					date=dish.getDate();
					sz.startTag(MOB_PREFIX, "menuItems");
					sz.startTag(MOB_PREFIX, "dateTime");
					sz.text(dateFromUnix(date));
					sz.endTag(MOB_PREFIX, "dateTime");
					sz.startTag(MOB_PREFIX,"timeStamp");
					sz.text(dateFromUnix(dish.getTimestamp()));
					sz.endTag(MOB_PREFIX,"timeStamp");					
				}
				sz.startTag(MOB_PREFIX, "OrderItems");				
				sz.startTag(MOB_PREFIX, "dishId");
				sz.text(dish.getDishId());
				sz.endTag(MOB_PREFIX, "dishId");
				sz.startTag(MOB_PREFIX, "ammount");
				sz.text(Float.toString(dish.getAmmount()));
				sz.endTag(MOB_PREFIX, "ammount");
				sz.endTag(MOB_PREFIX,"OrderItems");
			}
			sz.endTag(MOB_PREFIX, "menuItems");
			sz.endTag(MOB_PREFIX, "setMenuData");
			sz.endTag(MOB_PREFIX, "setMenu");
			sz.endTag(SOAP_PREFIX, "Body");
			sz.endTag(SOAP_PREFIX, "Envelope");
			sz.endDocument();
			return writer.toString();
		} catch (Exception e) {
			Log.e(XMLParser.class.toString(), e.getMessage());
			return null;
		}
	}
	public static List<RarusMenu> parseXMLSetOrder(String xml){
		Log.i(XMLParser.class.toString(), "[XMLParser] getted setMenu response xml:\n"+ xml);
		return null;
	}
	public static String parseXMLPing(String xml) {
		try {
			Log.i(XMLParser.class.toString(), "[XMLParser] getted ping xml:\n"+ xml);
			String userName = "";
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));
			xpp.next();
			String name = "";
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = xpp.getName();
					break;
				case XmlPullParser.END_TAG:
				case XmlPullParser.TEXT:
					if (name.equals("enName")) {
						Log.i(XMLParser.class.toString(), "[XMLParser] getted tag text:  "+xpp.getText());
						userName = xpp.getText();
						name="";
					}
					break;

				default:
					break;
				}
				xpp.next();
			}
			return userName;
		} catch (XmlPullParserException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getMenuXMLRequest() {
		XmlSerializer sz = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String cardCode=Preference.getCardNumber();
		try {
			sz.setOutput(writer);
			sz.setPrefix("soapenv", SOAP_PREFIX);
			sz.setPrefix("mob", MOB_PREFIX);
			sz.startTag(SOAP_PREFIX, "Envelope");
			sz.startTag(SOAP_PREFIX, "Header");
			sz.endTag(SOAP_PREFIX, "Header");
			sz.startTag(SOAP_PREFIX, "Body");
			sz.startTag(MOB_PREFIX, "getMenu");
			sz.startTag(MOB_PREFIX, "loginStructure");
			sz.startTag(MOB_PREFIX, "cardNumber");
			sz.text(cardCode);
			sz.endTag(MOB_PREFIX, "cardNumber");
			sz.endTag(MOB_PREFIX, "loginStructure");
			sz.endTag(MOB_PREFIX, "getMenu");
			sz.endTag(SOAP_PREFIX, "Body");
			sz.endTag(SOAP_PREFIX, "Envelope");
			sz.endDocument();
			return writer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static List<RarusMenu> parseXMLMenu(String xml) {
		try {
			List<RarusMenu> menu = new ArrayList<RarusMenu>();
			List<RarusMenu> temp = new ArrayList<RarusMenu>();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));
			xpp.next();
			String name = "";
			while (!name.equals("menuItems")) {
				if (xpp.getEventType() == XmlPullParser.START_TAG) {
					name = xpp.getName();
				}
				xpp.next();
			}
			int idMenu = 0;
			Integer date = null;
			String dishId = "";
			String dishName = "";
			String dishDescription = "";
			boolean portioned = false;
			float price = 0;
			String rating = "";
			boolean preorder = false;
			float availableAmmount = 0;
			float orderedAmmount = 0;
			boolean modified = false;
			int timestamp = 0;
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = xpp.getName();
					break;
				case XmlPullParser.END_TAG:
					if (xpp.getName().equals("menu")) {						
						RarusMenu m = new RarusMenu(idMenu, date, dishId, dishName,
								dishDescription, portioned, price, rating,
								preorder, availableAmmount, orderedAmmount,
								modified, timestamp);
						temp.add(m);
						//menu.add(m);
						idMenu++;
						Log.i("Dish_log", m.toString());

					}
					if (xpp.getName().equals("menuItems")) {
						for(RarusMenu dish:temp){
							dish.setTimestamp(timestamp);
						}
						menu.addAll(temp);
						temp=new ArrayList<RarusMenu>();
					}
					name = "";
					break;

				case XmlPullParser.TEXT:
					if (name.equals("dateTime")) {
						date = dateToUnix(xpp.getText());
						
					}
					if (name.equals("dishId")) {
						dishId = xpp.getText(); //Integer.parseInt(xpp.getText());
					}
					if (name.equals("name")) {
						dishName = xpp.getText();
					}
					if (name.equals("description")) {
						dishDescription = xpp.getText();
					}
					if (name.equals("price")) {
						price = (float) Double.parseDouble(xpp.getText());
					}
					if (name.equals("rating")) {
						rating = xpp.getText();
					}
					if (name.equals("available")) {
						availableAmmount = (float) Double.parseDouble(xpp
								.getText());
					}
					if (name.equals("ammount")) {
						orderedAmmount = (float) Double.parseDouble(xpp
								.getText());
						Log.d("ammount","ammount:"+orderedAmmount);
					}
					if (name.equals("preordered")) {
						preorder=Boolean.parseBoolean(xpp.getText());
					}
					if (name.equals("portioned")) {
						portioned=Boolean.parseBoolean(xpp.getText());
					}
					if (name.equals("timeStamp")) {
						timestamp=dateToUnix(xpp.getText());
					}
					break;

				default:
					break;
				}
				xpp.next();
			}
			for(RarusMenu dish:menu){
				Log.i("timestamp", "timestamp="+dateFromUnix(dish.getTimestamp()));
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
	
	private static String dateFromUnix(Integer date) {
		Date d=new Date((long)date*1000);
		Log.i("Date_log", d.toString());
		String year=Integer.toString(d.getYear()+1900);
		String month=Integer.toString(d.getMonth()+1);
		String day=Integer.toString(d.getDate());
		String hours=Integer.toString(d.getHours());
		String minutes=Integer.toString(d.getMinutes());
		String seconds=Integer.toString(d.getSeconds());
		if(month.length()<2){
			month="0"+month;
		}
		if(day.length()<2){
			day="0"+day;
		}
		if(hours.length()<2){
			hours="0"+hours;
		}
		if(minutes.length()<2){
			minutes="0"+minutes;
		}
		if(seconds.length()<2){
			seconds="0"+seconds;
		}
		String stringDate=year+"-"+month+"-"+day+"T"+hours+":"+minutes+":"+seconds;
		Log.i("Date_log", stringDate);
		return stringDate;
	}
	private static Integer dateToUnix(String date) {
		Log.i("Date_log", date);
		Date d = new Date(Integer.parseInt(date.substring(0, 4)) - 1900,
				Integer.parseInt(date.substring(5, 7)) - 1,
				Integer.parseInt(date.substring(8, 10)));
		Log.i("Date_log", "Day:"+d.toString());
		d.setHours(Integer.parseInt(date.substring(11, 13)));
		Log.i("Date_log", "Hour:"+d.toString());
		d.setMinutes(Integer.parseInt(date.substring(14, 16)));
		Log.i("Date_log", "min:"+d.toString());
		d.setSeconds(Integer.parseInt(date.substring(17, 19)));
		Log.i("Date_log", "Sec:"+d.toString());
		return (int) (d.getTime() / 1000);
	}

}
