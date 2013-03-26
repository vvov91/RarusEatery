package rarus.eatery.model;

import android.content.SharedPreferences;

public class Preference {
	private static SharedPreferences preference;
	
	public static void prefInit(SharedPreferences sp){
		preference=sp;
		
	}
	public static String getFirstURL(){
		return preference.getString("server1", "http://192.168.38.252:8095/DiningRoomTest/ws/mobileEda");
	}
	public static String getSecondURL(){
		return preference.getString("server2", "http://178.219.241.102:8095/DiningRoomTest/ws/mobileEda");
	}
	public static String getCardNumber(){
		return preference.getString("cardNumber", "000013BDBD");
	}
}
