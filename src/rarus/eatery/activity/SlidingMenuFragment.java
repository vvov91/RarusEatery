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
	SlidingMenuListAdapter customMenuListAdapter;

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

		customMenuListAdapter = new SlidingMenuListAdapter(v.getContext(),
				mDatesString);

		slidingMenuLV = (ListView) v.findViewById(R.id.lv);
		slidingMenuLV.setAdapter(customMenuListAdapter);
		slidingMenuLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switchFragment(position);

			}
		});
		return v;
	}

	// the meat of switching the above fragment
	private void switchFragment(int position) {
		if (getActivity() instanceof SlidingMenuActivity) {
			SlidingMenuActivity ra = (SlidingMenuActivity) getActivity();
			ra.switchContent(position);
		}
		customMenuListAdapter.setSelected(position);
		customMenuListAdapter.notifyDataSetChanged();
	}

	// public void markListElement(int pos) {
	// // if (mPreviousPossition != -2)
	// System.out.println("markListElement" + pos);
	// System.out.println("slidingMenuLV.getChildCount()"
	// + slidingMenuLV.getChildCount());
	//
	// for (int i = 0; i < slidingMenuLV.getChildCount(); i++) {
	// System.out.println("for" + i);
	//
	// slidingMenuLV.getChildAt(i).setBackgroundColor(0xFFffffff);
	// }
	// slidingMenuLV.getChildAt(pos).setBackgroundColor(0xFF808080);
	// }

}
