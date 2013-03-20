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

	Intent serviceIntent;
	ServiceConnection connection;
	EateryWebService client;
	BroadcastReceiver receiver;

	EateryDB mEDB;
	SharedPreferences sp;
	int mCurrentFragmentId = 0;
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
		if (savedInstanceState != null)
			mEDB = (EateryDB) getLastCustomNonConfigurationInstance();
		else
			this.mEDB = new EateryDB(getApplicationContext());
		setBehindContentView(R.layout.sliding_menu);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		if (mEDB.getMenuDates().size() == 0) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new FirstRunFragment())
					.commit();
			getSlidingMenu().setSlidingEnabled(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			makeFragments();
			switchContent(mCurrentFragmentId);
			getSlidingMenu().setSlidingEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public void switchContent(final int possition) {
		// смена основного фрагмента
		mCurrentFragmentId = possition;
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
		intent.putExtra(DishPageView.DISH_ID, dishId);
		intent.putExtra(DishPageView.DAY_ID, dayId);
		DayMenu dm = (DayMenu) fragments.get(dayId);
		intent.putExtra(DishPageView.LIST_DAY_MENU, dm.mRarusMenu);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		int dayId = data.getIntExtra(DishPageView.DAY_ID, -1);
		ArrayList<RarusMenu> tempRM = data
				.getParcelableArrayListExtra(DishPageView.LIST_DAY_MENU);
		DayMenu tempDM = fragments.get(mCurrentFragmentId);
		tempDM.mRarusMenu = tempRM;
		Log.d("int", "curday" + mCurrentFragmentId);
		Log.d("int", "curday" + fragments.get(mCurrentFragmentId).mStringDate);
		tempDM.refreshAdapter();
	}

	public void makeFragments() {
		// создание основного фрагмента
		List<Integer> dates = mEDB.getMenuDates();
		datesString = new ArrayList<String>();
		fragments = new ArrayList<DayMenu>();
		for (Integer date : dates) {
			DayMenu dm = new DayMenu();
			dm.mRarusMenu = (ArrayList<RarusMenu>) mEDB.getMenu(date);
			// System.out.println(dm.mRarusMenu.get(0).getAvailable());
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
		// mi.setIntent(new Intent(this, SettingsActivity.class));
		// add save/clean on taskbar
		menu.add(0, 2, 0, "Save").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, 3, 0, "Clean").setShowAsAction(
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

		case 2:
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
		Log.d(getClass().getName(), "MainActivity: onStart()");
	}

	@Override
	protected void onDestroy() {
		unbindService(connection);
		unregisterReceiver(receiver);
		Log.d(getClass().getName(), "MainActivity: onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(getClass().getName(), "MainActivity: onStop()");
	}

	public void onRefreshClick(View v) {
		client.getMenu();
		Toast.makeText(getBaseContext(), "Обновление меню...", 3).show();

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
				// client.getMenu();
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
				// Обновление фрагмента
				this.mEDB = new EateryDB(getApplicationContext());
				if (mEDB.getMenuDates().size() != 0) {
					makeFragments();
					switchContent(mCurrentFragmentId);
					Toast.makeText(getBaseContext(), "Меню обновлено.", 3)
							.show();
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
			Toast.makeText(getBaseContext(), "Запрос сервиса неудачен.", 3)
					.show();

			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			String error = intent.getStringExtra(EateryConstants.SERVICE_ERROR);
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при получении меню:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				Toast.makeText(getBaseContext(), "Ошибка при получении меню.",
						3).show();
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при отправке заказа:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				Toast.makeText(getBaseContext(), "Ошибка при отправке заказа.",
						3).show();
			}
				break;
			case EateryConstants.PING_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при соединеннии с сервером:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				Toast.makeText(getBaseContext(),
						"Ошибка при соединеннии с сервером.", 3).show();
			}
				break;
			}
		}
	}
}