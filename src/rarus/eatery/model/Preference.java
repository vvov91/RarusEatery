package rarus.eatery.model;

import android.content.SharedPreferences;

public class Preference {
	private static SharedPreferences preference;
	
	public static void prefInit(SharedPreferences sp){
		preference=sp;
		
	}
	public static String getFirstURL(){
		return preference.getString("server1", "-----------");
	}
	public static String getSecondURL(){
		return preference.getString("server2", "-----------");
	}
	public static String getCardNumber(){
		return preference.getString("cardNumber", "-----------");
	}
}
