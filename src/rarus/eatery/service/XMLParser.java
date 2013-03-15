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

import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.Menu;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;

/**
 * Класс с набюором статических методов для формирования XML запросов и разбора
 * XML ответов вебсервера 1-С
 * 
 * @author Dmitriy Bazunov <binnarywolf@gmail.com>
 */

public class XMLParser {

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
		String cardCode = "000013BDBD";
		try {
			sz.setOutput(writer);
			sz.setPrefix("soapenv", EateryConstants.SOAP_PREFIX);
			sz.setPrefix("mob", EateryConstants.MOB_PREFIX);
			sz.startTag(EateryConstants.SOAP_PREFIX, "Envelope");
			sz.startTag(EateryConstants.SOAP_PREFIX, "Header");
			sz.endTag(EateryConstants.SOAP_PREFIX, "Header");
			sz.startTag(EateryConstants.SOAP_PREFIX, "Body");
			sz.startTag(EateryConstants.MOB_PREFIX, "ping");
			sz.startTag(EateryConstants.MOB_PREFIX, "loginStructure");
			sz.startTag(EateryConstants.MOB_PREFIX, "cardNumber");
			sz.text(cardCode);
			sz.endTag(EateryConstants.MOB_PREFIX, "cardNumber");
			sz.endTag(EateryConstants.MOB_PREFIX, "loginStructure");
			sz.endTag(EateryConstants.MOB_PREFIX, "ping");
			sz.endTag(EateryConstants.SOAP_PREFIX, "Body");
			sz.endTag(EateryConstants.SOAP_PREFIX, "Envelope");
			sz.endDocument();
			return writer.toString();
		} catch (IOException exception) {
			Log.e(EateryConstants.SERVICE_LOG_TAG, "[XMLParser] IOException "+ exception.getLocalizedMessage());
			return null;
		}
	}

	public static String parseXMLPing(String xml) {
		try {
			Log.i(EateryConstants.SERVICE_LOG_TAG, "[XMLParser] getted ping xml:\n"+ xml);
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
					Log.i(EateryConstants.SERVICE_LOG_TAG, "[XMLParser] getted tag xml:  "+ name);
					break;
				case XmlPullParser.END_TAG:
				case XmlPullParser.TEXT:
					if (name.equals("enName")) {
						Log.i(EateryConstants.SERVICE_LOG_TAG, "[XMLParser] getted tag text:  "+xpp.getText());
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
		String cardCode = "000013BDBD";
		try {
			sz.setOutput(writer);
			sz.setPrefix("soapenv", EateryConstants.SOAP_PREFIX);
			sz.setPrefix("mob", EateryConstants.MOB_PREFIX);
			sz.startTag(EateryConstants.SOAP_PREFIX, "Envelope");
			sz.startTag(EateryConstants.SOAP_PREFIX, "Header");
			sz.endTag(EateryConstants.SOAP_PREFIX, "Header");
			sz.startTag(EateryConstants.SOAP_PREFIX, "Body");
			sz.startTag(EateryConstants.MOB_PREFIX, "getMenu");
			sz.startTag(EateryConstants.MOB_PREFIX, "loginStructure");
			sz.startTag(EateryConstants.MOB_PREFIX, "cardNumber");
			sz.text(cardCode);
			sz.endTag(EateryConstants.MOB_PREFIX, "cardNumber");
			sz.endTag(EateryConstants.MOB_PREFIX, "loginStructure");
			sz.endTag(EateryConstants.MOB_PREFIX, "getMenu");
			sz.endTag(EateryConstants.SOAP_PREFIX, "Body");
			sz.endTag(EateryConstants.SOAP_PREFIX, "Envelope");
			sz.endDocument();
			return writer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static List<Menu> parseXMLMenu(String xml) {
		try {
			List<Menu> menu = new ArrayList<Menu>();
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
						Menu m = new Menu(idMenu, date, dishId, dishName,
								dishDescription, portioned, price, rating,
								preorder, availableAmmount, orderedAmmount,
								modified, timestamp);
						menu.add(m);
						idMenu++;
						Log.i("Dish_log", m.toString());

					}
					if (xpp.getName().equals("menuItems")) {
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
					if (name.equals("availableAmmount")) {
						availableAmmount = (float) Double.parseDouble(xpp
								.getText());
					}
					if (name.equals("orderedAmmount")) {
						orderedAmmount = (float) Double.parseDouble(xpp
								.getText());
					}
					if (name.equals("preordered")) {
						portioned=Boolean.parseBoolean(xpp.getText());
					}
					if (name.equals("portioned")) {
						portioned=Boolean.parseBoolean(xpp.getText());
					}
					if (name.equals("timestamp")) {
						// to do timestamp
					}
					break;

				default:
					break;
				}
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

	private static Integer dateToUnix(String date) {
		Date d = new Date(Integer.parseInt(date.substring(0, 4)) - 1900,
				Integer.parseInt(date.substring(5, 7)) - 1,
				Integer.parseInt(date.substring(8, 10)));
		return (int) (d.getTime() / 1000);
	}

}
