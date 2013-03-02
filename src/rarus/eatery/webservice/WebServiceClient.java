package rarus.eatery.webservice;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import rarus.eatery.activity.MainActivity;
import rarus.eatery.database.DBManager;
import rarus.eatery.model.DayMenu;
import rarus.eatery.model.Dish;
import rarus.eatery.model.Menu;
import rarus.eatery.ui.MenuList;
import rarus.eatery.ui.MenuOnDate;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class WebServiceClient extends Service implements WebServiceInterface {
	
	DBManager db;
	final String LOG_TAG = "Web_Client";
	MyBinder binder = new MyBinder();
	int mode;
	WebServiceAPI api;
	MenuList menu;
	SharedPreferences sp;
	
	@Override
	public void onServiceSuccessfullRequest() {
		Log.d(LOG_TAG, "ServerSuccessfull");
		try {
			menu=api.get();
			writeToBD(menu);
			Intent intent=new Intent(MainActivity.BROADCAST_ACTION);
			intent.putExtra(MainActivity.SERVER_RESULT, true);
			sendBroadcast(intent);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {			
			e.printStackTrace();
		}

	}

	@Override
	public void onServiceErrorRequest() {
		Intent intent=new Intent(MainActivity.BROADCAST_ACTION);
		intent.putExtra("result", false);
		sendBroadcast(intent);
	}

	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "MyService onBind");
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		return binder;
	}

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
	}
	
	public void getMenu(String login,String password){		
		mode=Integer.parseInt(sp.getString("mode", "1"));
		Log.d(LOG_TAG,"getMenu");
		switch(mode){
		case 0:{
			MenuList m=new MenuList();
			Log.d(LOG_TAG,"Connection to DB");			
			db = new DBManager(this);
			db.open();
			List<Integer> dates = db.getMenuDates();
			for (Integer date : dates) {
				MenuOnDate  mD=new MenuOnDate(date);
				mD.addDishList(db.getMenuAtDate(date));
				m.addMenuOnDate(mD);
			}
			db.close();
			menu=m;			
			Intent intent=new Intent(MainActivity.BROADCAST_ACTION);
			intent.putExtra(MainActivity.SERVER_RESULT, true);
			sendBroadcast(intent);			
		}
		case 1:
			Log.d(LOG_TAG,"Connection to server");
			api=new WebServiceAPI(WebServiceClient.this);
			api.execute(login,password);			
		}
		
		
	}
	private void  writeToBD(MenuList m){
		Log.d(LOG_TAG, "DB write");
		db=new DBManager(this);
		db.open();
		db.deleteMenuAll();
		db.deleteDishAll();
		List <Integer> dates=m.getAvalibleData();
		/*for(Integer d:m.getAvalibleData()){
			List<DayMenu> dm = m.getMenuByDate(d).getDishes();
			List<Menu> menu = new ArrayList<Menu>();
						
			db.addMenu(m.getMenuByDate(d).getDishes());
		}*/
		db.close();	
		Log.d(LOG_TAG, "DB end write");
	}
	public MenuList getMenuResult(){
		return menu;
	}
	public class MyBinder extends Binder {
		public WebServiceClient getService() {
			return WebServiceClient.this;
		}
	}
}
