package rarus.eatery.activity;

import java.util.ArrayList;

import rarus.eatery.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SlidingMenuFragment extends Fragment {

	ArrayList<String> mDatesString;
	ListView slidingMenuLV;
	SlidingMenuListAdapter slidingMenuListAdapter;

	public SlidingMenuFragment() {
	}

	public SlidingMenuFragment(ArrayList<String> datesString) {
		mDatesString = datesString;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sliding_menu, null);
		// customMenuListAdapter = new SlidingMenuListAdapter(v.getContext(),
		// mDatesString);
		slidingMenuLV = (ListView) v.findViewById(R.id.lv);
		sLidingMenuListAdapter = new SlidingMenuListAdapter(v.getContext(), R.id.lv, mDatesString);
		slidingMenuLV.setAdapter(sLidingMenuListAdapter);
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
			sma.changeContentRequest(position);
		}
	}

	public void setSelectedItem(int position) {
		sLidingMenuListAdapter.setSelected(position);
		sLidingMenuListAdapter.notifyDataSetChanged();
	}
}
