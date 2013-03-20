package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.List;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class DishPageView extends SherlockFragmentActivity {
	PagerAdapter mPageAdapter;
	int mDayId;
	List<RarusMenu> mMenu;

	public static final String DISH_ID = "dishId";
	public static final String DATE = "date";
	public static final String LIST_DAY_MENU = "listDayMenu";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dish_viewpager);
		Intent intent = getIntent();
		int dishId = intent.getIntExtra(DISH_ID, -1);
		if (savedInstanceState != null)
			mMenu = (List<RarusMenu>) getLastCustomNonConfigurationInstance();
		else
			mMenu = getIntent().getParcelableArrayListExtra(LIST_DAY_MENU);
		List<Fragment> fragments = new ArrayList<Fragment>();
		for (RarusMenu m : mMenu) {
			DishPageViewFragment dpvp = new DishPageViewFragment();
			dpvp.p = m;
			fragments.add(dpvp);
		}
		mPageAdapter = new rarus.eatery.activity.PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(mPageAdapter);
		pager.setCurrentItem(dishId);
		setTitle(intent.getStringExtra(DATE));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().show();
	}

	public void reloadFragmentData() {
		mPageAdapter.notifyDataSetChanged();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		MenuItem mi = menu.add(0, 1, 0, "Настройки");
		mi.setIntent(new Intent(this, SettingsActivity.class));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return mMenu;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(LIST_DAY_MENU,
				(ArrayList<? extends Parcelable>) mMenu);
		setResult(RESULT_OK, intent);
		finish();
	}
}
