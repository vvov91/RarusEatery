package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.List;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class DishPageView extends SherlockFragmentActivity {
	PagerAdapter mPageAdapter;
	DayMenu mDayMenu;
	int mDayId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dish_viewpager);
		Intent intent = getIntent();
		int dishId = intent.getIntExtra("dishId", -1);
		mDayId=intent.getIntExtra("dayId", -1);
		if (savedInstanceState != null)
			mDayMenu = (DayMenu) getLastCustomNonConfigurationInstance();
		else
			mDayMenu = (DayMenu) getIntent().getParcelableExtra(
					DayMenu.class.getCanonicalName());
		List<Fragment> fragments = new ArrayList<Fragment>();
		List<RarusMenu> menu = mDayMenu.mRarusMenu;
		for (RarusMenu m : menu) {
			DishPageViewFragment dpvp = new DishPageViewFragment();
			dpvp.p = m;
			fragments.add(dpvp);
		}

		mPageAdapter = new rarus.eatery.activity.PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(mPageAdapter);
		pager.setCurrentItem(dishId);

		setTitle(mDayMenu.mStringDate);
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
		return mDayMenu;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Intent intent = new Intent();
		intent.putExtra(DayMenu.class.getCanonicalName(), mDayMenu);
		intent.putExtra("dayId", mDayId);
		setResult(RESULT_OK, intent);
		finish();
	}
}
