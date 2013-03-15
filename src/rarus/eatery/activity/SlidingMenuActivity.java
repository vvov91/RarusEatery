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
import rarus.eatery.model.Order;
import rarus.eatery.model.RarusMenu;

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
			System.out.println(dm.mRarusMenu.get(0).getAvailable());
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
		Log.d(TAG, "MainActivity: onStart()");
		Log.d("WebServ", "MainActivity: onStart()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "MainActivity: onStop()");
	}

}