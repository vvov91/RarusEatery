package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.List;

import rarus.eatery.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SlidingMenuFragment extends Fragment {

	ArrayList<String> mDatesString;
	ArrayList<Integer> mDates;

	ListView slidingMenuLV;
	SlidingMenuListAdapter slidingMenuListAdapter;

	public SlidingMenuFragment() {
	}

	public SlidingMenuFragment(ArrayList<String> datesString,
			ArrayList<Integer> dates) {
		mDatesString = datesString;
		mDates = dates;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sliding_menu, null);
		slidingMenuLV = (ListView) v.findViewById(R.id.lv);
		slidingMenuListAdapter = new SlidingMenuListAdapter(v.getContext(),
				R.id.lv, mDatesString, mDates);
		slidingMenuLV.setAdapter(slidingMenuListAdapter);
		slidingMenuLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switchFragment(position);

			}
		});
		return v;
	}

	private void switchFragment(int position) {
		if (getActivity() instanceof SlidingMenuActivity) {
			SlidingMenuActivity sma = (SlidingMenuActivity) getActivity();
			sma.getSupportActionBar().setSelectedNavigationItem(position);
		}
	}

	public void setSelectedItem(int position) {
		Log.d("int", "position" + position);
		try {
			slidingMenuListAdapter.setSelected(position);
			slidingMenuListAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.d("int", "Exception " + e.toString());
		}
	}
}
