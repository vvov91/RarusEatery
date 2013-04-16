package rarus.eatery.activity;

/*
 * ќсновное окно программы
 * ¬ нем содержитс¤ фрагмент меню на день 
 */
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rarus.eatery.R;
import rarus.eatery.database.EateryDB;
import rarus.eatery.model.Preference;
import rarus.eatery.model.RarusMenu;
import rarus.eatery.service.EateryWebService;
import rarus.eatery.service.Utility;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
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
	SlidingMenuFragment mSlidingMenuFragment;
	ProgressDialog pd;
	SlidingMenu mSlidingMenu;

	EateryDB mEateryDB;
	int mCurrentFragmentId = -1, mNextFragmentId = -1;
	List<DayMenuFragment> mDayMenuFragmentFragments = new ArrayList<DayMenuFragment>();
	List<String> mDatesString = new ArrayList<String>();
	List<Integer> mDates = new ArrayList<Integer>();
	static Boolean mChangedOrderedAmount = false;
	FirstRunFragment mFirstRunFragment;
	Boolean mWaiting = false;
	final int DIALOG_EXIT = 3, DIALOG_CLEAR = 2, DIALOG_SAVE = 1;
	final int MENU_SETTINGS = 1, MENU_SAVE = 2, MENU_CLEAR = 3;
	MenuItem mMenuItemSave, mMenuItemClear;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService();
		setTitle(R.string.app_name);
		setContentView(R.layout.main_content_frame);
		setBehindContentView(R.layout.sliding_menu);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		Log.d("int", "" + mChangedOrderedAmount);

		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindScrollScale(0.25f);
		mSlidingMenu.setFadeDegree(0.25f);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().hide();

		mEateryDB = new EateryDB(getApplicationContext());
		if (mEateryDB.getMenuDates().isEmpty()) {
			mFirstRunFragment = new FirstRunFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, mFirstRunFragment).commit();
			getSlidingMenu().setSlidingEnabled(false);
		} else {
			if ((savedInstanceState != null)
					|| (!mDayMenuFragmentFragments.isEmpty())) {
				mDayMenuFragmentFragments = (List<DayMenuFragment>) getLastCustomNonConfigurationInstance();
				mCurrentFragmentId = savedInstanceState
						.getInt("mCurrentFragmentId");
				mDatesString = savedInstanceState
						.getStringArrayList("mDatesString");
				mDates = savedInstanceState.getIntegerArrayList("mDates");
				makeSlidingMenu();
				mNextFragmentId = mCurrentFragmentId;
				getSupportActionBar().setSelectedNavigationItem(
						mCurrentFragmentId);
				Log.d("int", "mCurrentFragmentId=" + mCurrentFragmentId
						+ "mNextFragmentId=" + mNextFragmentId);
			} else {
				makeFragments();
				getSupportActionBar().setSelectedNavigationItem(
						mCurrentFragmentId);
			}
		}
		Preference.prefInit(PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext()));
		Utility.initUtility(this);
	}

	public void switchContent() {
		if (mDayMenuFragmentFragments.isEmpty())
			return;
		// смена основного фрагмента
		mCurrentFragmentId = mNextFragmentId;
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame,
						mDayMenuFragmentFragments.get(mNextFragmentId))
				.commit();
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
		Log.d("int", "mNextFragmentId=" + mNextFragmentId);
		mSlidingMenuFragment.setSelectedItem(mNextFragmentId);
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime = mDayMenuFragmentFragments.get(mCurrentFragmentId)
				.getDate(0) - DishAdapter.HOURS_7;
		mMenuItemSave.setVisible(currentUnixTime < menuUnixTime);
		mMenuItemClear.setVisible(currentUnixTime < menuUnixTime);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// list navigation(actionbar)
		Log.d("int", "itemPosition" + itemPosition);

		mNextFragmentId = itemPosition;
		if ((mChangedOrderedAmount) && (mNextFragmentId != mCurrentFragmentId))
			showDialog(1);
		else
			switchContent();
		return true;
	}

	public void showDownloadDialog(boolean waiting) {
		if (waiting) {
			pd = new ProgressDialog(this);
			pd.setTitle(R.string.synchronization);
			pd.setMessage(getString(R.string.download));
			pd.show();
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					client.cancel();
					Toast.makeText(getApplicationContext(), R.string.cancel,
							Toast.LENGTH_SHORT).show();
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

				}
			});
		}
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_SAVE) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.date_change);
			adb.setMessage(R.string.save_menu);
			adb.setIcon(android.R.drawable.ic_dialog_info);
			adb.setPositiveButton(R.string.yes, saveDialog);
			adb.setNegativeButton(R.string.no, saveDialog);
			adb.setCancelable(false);
			return adb.create();
		} else if (id == DIALOG_CLEAR) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.clear_menu);
			adb.setMessage(R.string.clear_menu_info);
			adb.setIcon(android.R.drawable.ic_dialog_info);
			adb.setPositiveButton(R.string.yes, cleanDialog);
			adb.setNegativeButton(R.string.no, cleanDialog);
			adb.setCancelable(false);
			return adb.create();
		} else if (id == DIALOG_EXIT) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.exit);
			adb.setMessage(R.string.exit_info);
			adb.setIcon(android.R.drawable.ic_dialog_info);
			adb.setPositiveButton(R.string.yes, exitDialog);
			adb.setNegativeButton(R.string.no, exitDialog);
			adb.setCancelable(false);
			return adb.create();
		}
		return super.onCreateDialog(id);
	}

	OnClickListener saveDialog = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				onSaveClick();
				break;
			case Dialog.BUTTON_NEGATIVE:
				removeChanges();
				break;
			}
			if (mWaiting)
				downloadMenu();
			else
				switchContent();
		}
	};
	OnClickListener cleanDialog = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				onCleanClick();
				Log.d("int", "clean");
				break;
			case Dialog.BUTTON_NEGATIVE:
				Log.d("int", "no clean");
				break;
			}
		}
	};
	OnClickListener exitDialog = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				finish();
				break;
			case Dialog.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}
		}
	};

	public void onDishPressed(int dayId, int dishId) {
		Intent intent = new Intent(this, DishPageViewActivity.class);
		intent.putExtra(DishPageViewActivity.DISH_ID, dishId);
		DayMenuFragment dm = (DayMenuFragment) mDayMenuFragmentFragments
				.get(dayId);
		intent.putExtra(DishPageViewActivity.LIST_DAY_MENU, dm.mRarusMenu);
		intent.putExtra(DishPageViewActivity.DATE, dm.mStringDate);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		ArrayList<RarusMenu> tempRM = data
				.getParcelableArrayListExtra(DishPageViewActivity.LIST_DAY_MENU);
		DayMenuFragment tempDM = mDayMenuFragmentFragments
				.get(mCurrentFragmentId);
		tempDM.setRarusMenu(tempRM);
		tempDM.refreshAdapter();
	}

	public void makeFragments() {
		// создание основного фрагмента
		mCurrentFragmentId = -1;
		mNextFragmentId = -1;
		mDates = mEateryDB.getMenuDates();
		mDatesString = new ArrayList<String>();
		mDayMenuFragmentFragments = new ArrayList<DayMenuFragment>();
		for (Integer date : mDates) {
			DayMenuFragment tempDayMenuFragment = new DayMenuFragment();
			tempDayMenuFragment.setRarusMenu((ArrayList<RarusMenu>) mEateryDB
					.getMenu(date));
			java.util.Date d = new Date(((long) date.intValue()) * 1000);
			Locale locale = new Locale("ru", "RU");
			DateFormat df = new SimpleDateFormat("EEEEEE, d MMM", locale);
			String reportDate = df.format(d);
			tempDayMenuFragment.setStringDate(reportDate);
			mDatesString.add(tempDayMenuFragment.mStringDate);
			tempDayMenuFragment.setPosition(mDayMenuFragmentFragments.size());
			mDayMenuFragmentFragments.add(tempDayMenuFragment);

			long currentUnixTime = System.currentTimeMillis() / 1000L;
			if ((mCurrentFragmentId == -1)
					&& (currentUnixTime < (date - DishAdapter.HOURS_7))) {
				mCurrentFragmentId = mDates.indexOf(date);
				mNextFragmentId = mDates.indexOf(date);
			}
		}
		if ((mCurrentFragmentId == -1) || (mNextFragmentId == -1)) {
			mCurrentFragmentId = 0;
			mNextFragmentId = 0;
		}
		makeSlidingMenu();

	}

	public void makeSlidingMenu() {
		mSlidingMenuFragment = new SlidingMenuFragment(
				(ArrayList<String>) mDatesString, (ArrayList<Integer>) mDates);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.rootlayout, mSlidingMenuFragment).commit();
		// создание выпадающей навигации
		ArrayAdapter<String> list = new ArrayAdapter<String>(
				getSupportActionBar().getThemedContext(),
				R.layout.sherlock_spinner_item, mDatesString);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
		getSlidingMenu().setSlidingEnabled(true);
		getSupportActionBar().show();
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// системное меню
		MenuItem menuItem = menu.add(0, MENU_SETTINGS, 0,
				R.string.menu_settings);
		menuItem.setIntent(new Intent(this, SettingsActivity.class));
		// add save/clean on taskbar
		menu.add(0, MENU_SAVE, 0, R.string.save)
				.setIcon(R.drawable.save)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0, MENU_CLEAR, 0, R.string.clean)
				.setIcon(R.drawable.clean)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		mMenuItemSave = menu.findItem(MENU_SAVE);
		mMenuItemClear = menu.findItem(MENU_CLEAR);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;
		case 2:
			onSaveClick();
			break;
		case 3:
			showDialog(2);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCleanClick() {

		DayMenuFragment tempDM = mDayMenuFragmentFragments
				.get(mCurrentFragmentId);
		for (RarusMenu dmiterator : tempDM.mRarusMenu) {
			if (dmiterator.getAmmount() != 0) {
				dmiterator.setAmmount(0);
				mChangedOrderedAmount = true;
				Log.d("int", "" + mChangedOrderedAmount);
			}
		}
		tempDM.refreshAdapter();
		Toast.makeText(this, R.string.cleaned, Toast.LENGTH_SHORT).show();
	}

	public void removeChanges() {
		int date = mEateryDB.getMenuDates().get(mCurrentFragmentId);
		DayMenuFragment tempDM = mDayMenuFragmentFragments
				.get(mCurrentFragmentId);
		tempDM.mRarusMenu = (ArrayList<RarusMenu>) mEateryDB.getMenu(date);
		tempDM.refreshAdapter();
		mChangedOrderedAmount = false;
	}

	public void onSaveClick() {
		mEateryDB
				.saveMenu(mDayMenuFragmentFragments.get(mCurrentFragmentId).mRarusMenu);
		Toast.makeText(getBaseContext(), R.string.saved, Toast.LENGTH_SHORT)
				.show();
		ArrayList<RarusMenu> rm = (ArrayList<RarusMenu>) mEateryDB
				.getOrdersNotSent();
		for (RarusMenu rmiterator : rm) {
			Log.d("int", "" + rmiterator.getAmmount());
		}
		mChangedOrderedAmount = false;
		Log.d("int", "" + mChangedOrderedAmount);
	}

	// method for synchronizing the menu (link in the layout)
	public void onRefreshClick(View v) {
		mWaiting = true;
		if (mChangedOrderedAmount)
			showDialog(1);
		// downloadMenu();
	}

	public void downloadMenu() {
		client.update();
		Toast.makeText(getBaseContext(), R.string.synchronization,
				Toast.LENGTH_SHORT).show();
		showDownloadDialog(true);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}

	// method to display the menu (link in the layout)
	public void onMenuClick(View v) {
		openOptionsMenu();
	}

	private void startService() {
		serviceIntent = new Intent(this, EateryWebService.class);
		startService(serviceIntent);
		connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.d(this.getClass().toString(),
						"MainActivity onServiceConnected");
				client = ((EateryWebService.EateryServiceBinder) binder)
						.getService();
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.d(this.getClass().toString(),
						"MainActivity onServiceDisconnected");
			}
		};

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = intent.getBooleanExtra(
						EateryWebService.SERVICE_RESULT, false);
				recive(result, intent);
			}
		};
		IntentFilter intFilt = new IntentFilter(
				EateryWebService.BROADCAST_ACTION);
		registerReceiver(receiver, intFilt);
	}

	private void recive(boolean result, Intent intent) {

		Log.d(this.getClass().toString(),
				"MainActivity: ѕолученно сообщение от сервиса");
		pd.dismiss();
		mWaiting = false;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		if (result) {
			int operationCode = intent.getIntExtra(
					EateryWebService.SERVICE_RESULT_CODE, 0);
			Log.d(this.getClass().toString(),
					"MainActivity: «апрос сервиса успешен");
			switch (operationCode) {
			case EateryWebService.GET_MENU_CODE: {
				Log.d(this.getClass().toString(),
						"MainActivity: ѕолученно меню");
				// update fragment
				this.mEateryDB = new EateryDB(getApplicationContext());
				if (mEateryDB.getMenuDates().size() != 0) {
					makeFragments();
					mNextFragmentId = mCurrentFragmentId;
					getSupportActionBar().setSelectedNavigationItem(
							mNextFragmentId);
					Toast.makeText(getBaseContext(), R.string.updated_menu,
							Toast.LENGTH_SHORT).show();
					mChangedOrderedAmount = false;
					Log.d("int", "" + mChangedOrderedAmount);
				}
			}
				break;
			case EateryWebService.SET_ORDER_CODE: {
			}
				break;
			case EateryWebService.PING_CODE: {
			}
				break;
			}

		} else {

			Log.d(this.getClass().toString(),
					"MainActivity: «апрос сервиса неудачен");
			Toast.makeText(getBaseContext(), R.string.error_request,
					Toast.LENGTH_SHORT).show();

			int operationCode = intent.getIntExtra(
					EateryWebService.SERVICE_RESULT_CODE, 0);
			String error = intent
					.getStringExtra(EateryWebService.SERVICE_ERROR);
			switch (operationCode) {
			case EateryWebService.GET_MENU_CODE: {
				Log.d(this.getClass().toString(),
						"MainActivity: ошибка при получении меню:");
				Log.e(this.getClass().toString(), "MainActivity: " + error);
				Toast.makeText(getBaseContext(),
						R.string.error_getting + error, Toast.LENGTH_SHORT)
						.show();
			}
				break;
			case EateryWebService.SET_ORDER_CODE: {
				Log.d(this.getClass().toString(),
						"MainActivity: ошибка при отправке заказа:");
				Log.e(this.getClass().toString(), "MainActivity: " + error);
				Toast.makeText(getBaseContext(),
						R.string.error_sending + error, Toast.LENGTH_SHORT)
						.show();
			}
				break;
			case EateryWebService.PING_CODE: {
				Log.d(this.getClass().toString(),
						"MainActivity: ошибка при соединеннии с сервером:");
				Log.e(this.getClass().toString(), "MainActivity: " + error);
				Toast.makeText(getBaseContext(),
						R.string.error_connecting + error, Toast.LENGTH_SHORT)
						.show();
			}
				break;
			}
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return mDayMenuFragmentFragments;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// saving id of the current day
		outState.putInt("mCurrentFragmentId", mCurrentFragmentId);
		outState.putStringArrayList("mDatesString",
				(ArrayList<String>) mDatesString);
		outState.putIntegerArrayList("mDates", (ArrayList<Integer>) mDates);
	}

	public void onBackPressed() {
		if (mSlidingMenu.isShown())
			showDialog(DIALOG_EXIT);
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
		stopService(serviceIntent);
		Log.d(getClass().getName(), "MainActivity: onDestroy()");
		super.onDestroy();
	}

}