package rarus.eatery.activity;

/*
 * Основное окно программы
 * В нем содержится фрагмент меню на день 
 */
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import rarus.eatery.R;
import rarus.eatery.database.EateryDB;
import rarus.eatery.model.EateryConstants;
import rarus.eatery.model.RarusMenu;
import rarus.eatery.service.EateryWebService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class SlidingMenuActivity extends SlidingFragmentActivity implements
		OnNavigationListener {

	final String TAG = "Main";
	final String LOG_TAG = "Main";
	
	Intent serviceIntent;
	ServiceConnection connection;
	EateryWebService client;
	BroadcastReceiver receiver;
	
	EateryDB mEDB;
	SharedPreferences sp;
	int modeTemp, currentFragmentId = 0, previousFragmentId = 0;
	final int DIALOG_ORDER = 1;
	List<DayMenu> fragments = new ArrayList<DayMenu>();
	List<String> datesString = new ArrayList<String>();
	static Boolean changedOrderedAmount = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startService();
		
		setTitle(R.string.app_name);

		setContentView(R.layout.main_content_frame);

		// show home as up so we can toggle
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setBehindContentView(R.layout.sliding_menu);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		if (savedInstanceState != null)
			mEDB = (EateryDB) getLastCustomNonConfigurationInstance();
		else
			mEDB = new EateryDB(getBaseContext());
		makeFragments(mEDB);
		switchContent(0);

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

	}

	public void switchContent(final int possition) {
		// смена основного фрагмента
		fragments.get(possition);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragments.get(possition)).commit();
		// getSupportActionBar().setTitle(datesString.get(possition));
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
		getSupportActionBar().setSelectedNavigationItem(possition);

	}

	public void onDishPressed(int dayId, int dishId) {
		Intent intent = new Intent(this, DishPageView.class);
		intent.putExtra("dishId", dishId);
		DayMenu dm = (DayMenu) fragments.get(dayId);
		intent.putExtra(DayMenu.class.getCanonicalName(), dm);
		startActivity(intent);
	}

	public void makeFragments(EateryDB edb) {
		// создание основного фрагмента
		List<Integer> dates = edb.getMenuDates();
		datesString = new ArrayList<String>();
		fragments = new ArrayList<DayMenu>();
		for (Integer date : dates) {
			DayMenu dm = new DayMenu();
			dm.mRarusMenu = (ArrayList<RarusMenu>) edb.getMenu(date);
			//System.out.println(dm.mRarusMenu.get(0).getAvailable());
			java.util.Date d = new Date(((long) date.intValue()) * 1000);
			dm.mStringDate = d.toString();
			datesString.add(dm.mStringDate);
			dm.mPos = fragments.size();
			fragments.add(dm);
		}
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						R.id.rootlayout,
						new SlidingMenuFragment((ArrayList<String>) datesString))
				.commit();

		// создание выпадающей навигации
		ArrayAdapter<String> list = new ArrayAdapter<String>(
				getSupportActionBar().getThemedContext(),
				R.layout.sherlock_spinner_item, datesString);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return mEDB;
	}

	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// выпадающая навигация
		switchContent(itemPosition);
		return true;
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// системное меню
		MenuItem mi = menu.add(0, 1, 0, "Настройки");
		mi.setIntent(new Intent(this, SettingsActivity.class));
		// add save/clean on taskbar
		menu.add("Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Clean").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "Got click: " + item.toString(),
				Toast.LENGTH_SHORT).show();

		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(serviceIntent, connection, 0);
		Log.d(TAG, "MainActivity: onStart()");
	}
	@Override
	protected void onDestroy() {
		unbindService(connection);
		unregisterReceiver(receiver);
		Log.d(TAG, "MainActivity: onDestroy()");
		super.onDestroy();		
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "MainActivity: onStop()");
	}
	
	public void onRefreshClick(View v){
		client.getMenu();
	} 
	private void startService() {
		serviceIntent = new Intent(this, EateryWebService.class);
		startService(serviceIntent);
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity onServiceConnected");
				client = ((EateryWebService.EateryServiceBinder) binder)
						.getService();
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity onServiceDisconnected");
			}
		};

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = intent.getBooleanExtra(
						EateryConstants.SERVICE_RESULT, false);
				recive(result, intent);
			}
		};
		IntentFilter intFilt = new IntentFilter(
				EateryConstants.BROADCAST_ACTION);
		registerReceiver(receiver, intFilt);
	}
	private void recive(boolean result, Intent intent) {
		Log.d(EateryConstants.GUI_LOG_TAG,
				"MainActivity: Полученно сообщение от сервиса");
		if (result) {			
			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			Log.d(EateryConstants.GUI_LOG_TAG,
					"MainActivity: Запрос сервиса успешен");
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: Полученно меню");
				EateryDB db=new EateryDB(getApplicationContext());
				List<Integer> dates= db.getMenuDates();
				for(Integer d:dates){
					java.util.Date date=new Date((long)d*1000);
					Log.d(EateryConstants.GUI_LOG_TAG,
							"MainActivity: Дата"+ date.toString()) ;
					List<RarusMenu> menu=db.getMenu(d);
					for(RarusMenu m:menu ){
						Log.i(EateryConstants.GUI_LOG_TAG,
								"MainActivity:  меню "+ m.toString()) ;
					}
				}
				
			
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
			}
				break;
			case EateryConstants.PING_CODE: {
			}
				break;
			}

		} else {
			Log.d(EateryConstants.GUI_LOG_TAG,
					"MainActivity: Запрос сервиса неудачен");
			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			String error = intent.getStringExtra(EateryConstants.SERVICE_ERROR);
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при получении меню:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при отправке заказа:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
			}
				break;
			case EateryConstants.PING_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при соединеннии с сервером:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				
			}
				break;
			}
		}
	}
}