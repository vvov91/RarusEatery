package rarus.eatery.activity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import rarus.eatery.R;
import rarus.eatery.database.DBManager;
import rarus.eatery.model.DayMenu;
import rarus.eatery.model.Dish;
import rarus.eatery.ui.DayMenuFragment;
import rarus.eatery.ui.MenuList;
import rarus.eatery.webservice.WebServiceClient;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	final String TAG = "Main";
	final String LOG_TAG = "Main";

	private PagerAdapter mPageAdapter;
	DBManager db = new DBManager(this);
	SharedPreferences sp;
	int modeTemp, currentFragmentId = 0, previousFragmentId = 0;
	final int DIALOG_ORDER = 1;
	List<Fragment> fragments = new ArrayList<Fragment>();
	public static Boolean changedOrderedAmount = false;
	MenuList menu;
	boolean firstRun = true;

	Intent serviceIntent;
	BroadcastReceiver receiver;
	ServiceConnection connection;
	WebServiceClient client;
	public final static String BROADCAST_ACTION = "rarus.eatery.broadcast";
	public final static String SERVER_RESULT = "rarus.eatery.serviceResult";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		firstRun = savedInstanceState == null;
		setContentView(R.layout.activity_main);
		Log.d(TAG, "MainActivity: onCreate()");
		startService();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		String modeValue = sp.getString("mode", "1");
		if (firstRun) {
			/*if (modeValue.equals("0"))
				modeValue = getString(R.string.modeDB);
			else
				modeValue = getString(R.string.modeOnline);*/
			modeValue = getString(R.string.modeDB);
			Toast.makeText(getBaseContext(), modeValue + ". Загрузка...",
					Toast.LENGTH_LONG).show();
		}

		final String SERV_LOGIN = sp.getString("servLogin", "mobile");
		final String SERV_PASS = sp.getString("serPass", "mobile");

		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(LOG_TAG, "MainActivity onServiceConnected");
				client = ((WebServiceClient.MyBinder) binder).getService();
				client.getMenu("mobile", "mobile");
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
			}
		};
		if (!firstRun) {
			menu = (MenuList) getLastCustomNonConfigurationInstance();
			makeViewPager(menu);
		}

	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		// final MenuList data = collectMyLoadedData();
		return menu;
	}

	private void startService() {
		serviceIntent = new Intent(this, WebServiceClient.class);
		startService(serviceIntent);
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(LOG_TAG, "MainActivity onServiceConnected");
				client = ((WebServiceClient.MyBinder) binder).getService();
				// client.getMenu("mobile", "mobile");
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
			}
		};

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = intent.getBooleanExtra(SERVER_RESULT, false);
				if (result) {
					if (firstRun) {
						menu = client.getMenuResult();
						Log.d(LOG_TAG, "Menu gotten");
						for (Integer dat : menu.getAvalibleData()) {
							Log.i("menuRes", menu.getMenuByDate(dat).toString());
						}
						makeViewPager(menu);
					}
				} else {
					Toast.makeText(getBaseContext(), "Произошла ошибка.",
							Toast.LENGTH_LONG).show();
				}
			}
		};
		IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(receiver, intFilt);
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(serviceIntent, connection, 0);
		Log.d(TAG, "MainActivity: onStart()");
		Log.d("WebServ", "MainActivity: onStart()");
		// client.getMenu("mobile", "mobile");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "MainActivity: onRestart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "MainActivity: onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "MainActivity: onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(connection);
		Log.d(TAG, "MainActivity: onStop()");
	}

	@Override
	protected void onDestroy() {
		stopService(serviceIntent);
		unregisterReceiver(receiver);
		super.onDestroy();
		Log.d(TAG, "MainActivity: onDestroy()");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, "Настройки");
		mi.setIntent(new Intent(this, SettingsActivity.class));
		return super.onCreateOptionsMenu(menu);
	}

	public void makeViewPager(MenuList menu) {
		DBManager db = new DBManager(this);
		db.open();
		List<Integer> dates = db.getMenuDates();
		for (Integer date : dates) {
			DayMenuFragment dm = new DayMenuFragment();
			dm.dishes = (ArrayList<DayMenu>) db.getMenuAtDate(date);
			java.util.Date d = new Date(((long) date.intValue()) * 1000);
			dm.date = d.toString();
			fragments.add(dm);
		}
		db.close();

		this.mPageAdapter = new rarus.eatery.adapters.PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) super.findViewById(R.id.viewPager);
		pager.setAdapter(this.mPageAdapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				if ((currentFragmentId != position) && changedOrderedAmount) {
					showDialog(DIALOG_ORDER);
					previousFragmentId = currentFragmentId;
					currentFragmentId = position;
				}
			}

			@Override
			public void onPageSelected(int position) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		// pager.setCurrentItem(2);
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ORDER) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			// заголовок
			adb.setTitle(R.string.exit);
			// сообщение
			adb.setMessage(R.string.save_data);
			// иконка
			adb.setIcon(android.R.drawable.ic_dialog_info);
			// кнопка положительного ответа
			adb.setPositiveButton(R.string.yes, myClickListener);
			// кнопка отрицательного ответа
			adb.setNegativeButton(R.string.no, myClickListener);
			// создаем диалог
			return adb.create();
		}
		return super.onCreateDialog(id);
	}

	OnClickListener myClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE: {
				Toast.makeText(getBaseContext(), R.string.saved, 3).show();
				DayMenuFragment previousDayMenu = new DayMenuFragment();
				previousDayMenu = (DayMenuFragment) fragments.get(previousFragmentId);
				db.open();
				int dateInt = db.getMenuDates().get(previousFragmentId);
				db.deleteMenuAtDate(dateInt);
				//db.addMenu(dateInt, previousDayMenu.dishes);
				//db.addOrder(dateInt, previousDayMenu.dishes);
				db.close();
				changedOrderedAmount = false;
			}
				break;
			// негативная кнопка
			case Dialog.BUTTON_NEGATIVE:
				Toast.makeText(getBaseContext(), "Не сохранено", 3).show();
				db.open();
				int previousDateInt = db.getMenuDates().get(previousFragmentId);
				DayMenuFragment previousDayMenu = (DayMenuFragment) fragments
						.get(previousFragmentId);
				previousDayMenu.dishes = (ArrayList<DayMenu>) db
						.getMenuAtDate(previousDateInt);
				db.close();
				mPageAdapter.notifyDataSetChanged();
				changedOrderedAmount = false;
				break;
			}
		};
	};
}