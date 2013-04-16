package rarus.eatery.activity;

import java.util.ArrayList;

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
import android.widget.TextView;

public class SlidingMenuFragment extends Fragment {

	ArrayList<String> mDatesString;
	ArrayList<Integer> mDates;
	String mTimeStamp;
	ListView mSlidingMenuLV;
	SlidingMenuListAdapter mSlidingMenuListAdapter;

	public SlidingMenuFragment() {
	}

	public SlidingMenuFragment(ArrayList<String> mDatesString,
			ArrayList<Integer> mDates, String mTimeStamp) {
		this.mDatesString = mDatesString;
		this.mDates = mDates;
		this.mTimeStamp = mTimeStamp;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sliding_menu, null);
		TextView tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
		tvTimeStamp.setText(getResources().getString(R.string.updated)+" "+mTimeStamp);
		mSlidingMenuLV = (ListView) v.findViewById(R.id.lv);
		mSlidingMenuListAdapter = new SlidingMenuListAdapter(v.getContext(),
				R.id.lv, mDatesString, mDates);
		mSlidingMenuLV.setAdapter(mSlidingMenuListAdapter);
		mSlidingMenuLV.setOnItemClickListener(new OnItemClickListener() {
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
			mSlidingMenuListAdapter.setSelected(position);
			mSlidingMenuListAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.d("int", "Exception " + e.toString());
		}
	}
}
