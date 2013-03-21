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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

	SlidingMenuFragment mSlidingMenuFragment;
	EateryDB mEDB;
	SharedPreferences sp;
	int mCurrentFragmentId = 0, mNextFragmentId = 0;
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
		Log.d("int", "" + changedOrderedAmount);

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
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().hide();

		if (mEDB.getMenuDates().size() == 0) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new FirstRunFragment())
					.commit();
			getSlidingMenu().setSlidingEnabled(false);
		} else {
			makeFragments();
			changeContentRequest(mCurrentFragmentId);

		}
	}

	public void switchContent() {
		// смена основного фрагмента
		mCurrentFragmentId = mNextFragmentId;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragments.get(mNextFragmentId))
				.commit();
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
		getSupportActionBar().setSelectedNavigationItem(mNextFragmentId);
	}

	// list navigation(actionbar)
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// выпадающа¤ навигаци¤
		changeContentRequest(itemPosition);
		mSlidingMenuFragment.setSelectedItem(itemPosition);
		return true;
	}

	public void changeContentRequest(int newId) {
		mNextFragmentId = newId;
		if (changedOrderedAmount)
			showDialog(1);
		else
			switchContent();
	}

	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
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
			case Dialog.BUTTON_POSITIVE:
				onSaveClick();
				switchContent();
				Log.d("int", "save");
				break;
			// негаитвная кнопка
			case Dialog.BUTTON_NEGATIVE:
				removeChanges();
				switchContent();
				Log.d("int", "-");
				break;
			}
		}
	};

	public void onDishPressed(int dayId, int dishId) {
		Intent intent = new Intent(this, DishPageView.class);
		intent.putExtra(DishPageView.DISH_ID, dishId);
		DayMenu dm = (DayMenu) fragments.get(dayId);
		intent.putExtra(DishPageView.LIST_DAY_MENU, dm.mRarusMenu);
		intent.putExtra(DishPageView.DATE, dm.mStringDate);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		ArrayList<RarusMenu> tempRM = data
				.getParcelableArrayListExtra(DishPageView.LIST_DAY_MENU);
		DayMenu tempDM = fragments.get(mCurrentFragmentId);
		tempDM.mRarusMenu = tempRM;
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
			java.util.Date d = new Date(((long) date.intValue()) * 1000);

			Locale locale = new Locale("ru", "RU");

			DateFormat df = new SimpleDateFormat("EEEEEE, d MMM", locale);
			String reportDate = df.format(d);
			dm.mStringDate = reportDate;
			datesString.add(dm.mStringDate);
			dm.mPos = fragments.size();
			fragments.add(dm);
		}
		mSlidingMenuFragment = new SlidingMenuFragment(
				(ArrayList<String>) datesString);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.rootlayout, mSlidingMenuFragment).commit();
		// создание выпадающей навигации
		ArrayAdapter<String> list = new ArrayAdapter<String>(
				getSupportActionBar().getThemedContext(),
				R.layout.sherlock_spinner_item, datesString);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
		getSlidingMenu().setSlidingEnabled(true);
		getSupportActionBar().show();
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return mEDB;
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// системное меню
		MenuItem mi = menu.add(0, 1, 0, "Ќастройки");
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
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;
		case 2:
			onSaveClick();
			break;
		case 3:
			onCleanClick();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCleanClick() {
		DayMenu tempDM = fragments.get(mCurrentFragmentId);
		for (RarusMenu dmiterator : tempDM.mRarusMenu) {
			if (dmiterator.getAmmount() != 0) {
				dmiterator.setAmmount(0);
				changedOrderedAmount = true;
				Log.d("int", "" + changedOrderedAmount);

			}
		}
		tempDM.refreshAdapter();
		Toast.makeText(this, "Заказ очищен, необходимо сохранение",
				Toast.LENGTH_SHORT).show();
	}

	public void removeChanges() {
		int date = mEDB.getMenuDates().get(mCurrentFragmentId);
		DayMenu tempDM=fragments.get(mCurrentFragmentId);
		tempDM.mRarusMenu=(ArrayList<RarusMenu>) mEDB.getMenu(date);
		tempDM.refreshAdapter();
		changedOrderedAmount=false;
	}

	public void onSaveClick() {
		mEDB.saveMenu(fragments.get(mCurrentFragmentId).mRarusMenu);
		Toast.makeText(getBaseContext(), "Заказ сохранен", 3).show();
		ArrayList<RarusMenu> rm = (ArrayList<RarusMenu>) mEDB
				.getOrdersNotSent();
		for (RarusMenu rmiterator : rm) {
			Log.d("int", "" + rmiterator.getAmmount());
		}
		changedOrderedAmount = false;
		Log.d("int", "" + changedOrderedAmount);

	}

	// method for synchronizing the menu (link in the layout)
	public void onRefreshClick(View v) {
		client.update();
		Toast.makeText(getBaseContext(), "������������� ������ � ��������...", 3)
				.show();
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
				"MainActivity: ѕолученно сообщение от сервиса");
		if (result) {
			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			Log.d(EateryConstants.GUI_LOG_TAG,
					"MainActivity: «апрос сервиса успешен");
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ѕолученно меню");
				// ќбновление фрагмента
				this.mEDB = new EateryDB(getApplicationContext());
				if (mEDB.getMenuDates().size() != 0) {
					makeFragments();
					changeContentRequest(mCurrentFragmentId);
					Toast.makeText(getBaseContext(), "ћеню обновлено.", 3)
							.show();
					changedOrderedAmount = false;
					Log.d("int", "" + changedOrderedAmount);
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
					"MainActivity: «апрос сервиса неудачен");
			Toast.makeText(getBaseContext(), "«апрос сервиса неудачен.", 3)
					.show();

			int operationCode = intent.getIntExtra(
					EateryConstants.SERVICE_RESULT_CODE, 0);
			String error = intent.getStringExtra(EateryConstants.SERVICE_ERROR);
			switch (operationCode) {
			case EateryConstants.GET_MENU_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при получении меню:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				Toast.makeText(getBaseContext(), "ќшибка при получении меню.",
						3).show();
			}
				break;
			case EateryConstants.SET_ORDER_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при отправке заказа:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				Toast.makeText(getBaseContext(), "ќшибка при отправке заказа.",
						3).show();
			}
				break;
			case EateryConstants.PING_CODE: {
				Log.d(EateryConstants.GUI_LOG_TAG,
						"MainActivity: ошибка при соединеннии с сервером:");
				Log.e(EateryConstants.GUI_LOG_TAG, "MainActivity: " + error);
				Toast.makeText(getBaseContext(),
						"ќшибка при соединеннии с сервером.", 3).show();
			}
				break;
			}
		}
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
}