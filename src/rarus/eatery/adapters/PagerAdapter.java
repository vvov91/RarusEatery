package rarus.eatery.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

	List<Fragment> fragments;

	public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		return	this.fragments.get(arg0);
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	
}