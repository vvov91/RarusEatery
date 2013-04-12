package rarus.eatery.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter class to show the dates
 */
public class SlidingMenuListAdapter extends ArrayAdapter {

	private ArrayList<String> mDatesString;
	private ArrayList<Integer> mDates;
	private int mSelectedPosition = -1;
	private Context mContext;

	public SlidingMenuListAdapter(Context context, int textViewResourceId,
			ArrayList<String> datesString, ArrayList<Integer> dates) {
		super(context, textViewResourceId, datesString);
		this.mContext = context;
		this.mDatesString = datesString;
		this.mDates = dates;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		MenuListViewHolder viewHolder;

		if (v == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(android.R.layout.simple_list_item_1, parent, false);

			viewHolder = new MenuListViewHolder();
			viewHolder.text1 = (TextView) v.findViewById(android.R.id.text1);
			v.setTag(viewHolder);
		} else {
			viewHolder = (MenuListViewHolder) v.getTag();
		}
		String str = mDatesString.get(position);
		viewHolder.text1.setText(str);
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime = mDates.get(position) - DishAdapter.HOURS_7;
		if (currentUnixTime > menuUnixTime)
			viewHolder.text1.setTextColor(Color.LTGRAY);
		else
			viewHolder.text1.setTextColor(Color.BLACK);
		if ((mSelectedPosition != -1) && (mSelectedPosition == position))
			v.setBackgroundColor(Color.GRAY);
		else
			v.setBackgroundColor(Color.WHITE);
		return v;
	}

	public void setSelected(int selectedPosition) {
		this.mSelectedPosition = selectedPosition;
	}

	static class MenuListViewHolder {
		TextView text1;
	}
}