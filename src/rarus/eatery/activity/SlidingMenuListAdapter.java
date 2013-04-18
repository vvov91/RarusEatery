package rarus.eatery.activity;

import java.util.ArrayList;

import rarus.eatery.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
			// v = li.inflate(android.R.layout.simple_list_item_1, parent,
			// false);
			v = li.inflate(R.layout.sliding_menu_list_item, parent, false);
			viewHolder = new MenuListViewHolder();
			viewHolder.tvDate = (TextView) v.findViewById(R.id.tvDate);
			viewHolder.ivLock = (ImageView) v.findViewById(R.id.ivLock);
			v.setTag(viewHolder);
		} else {
			viewHolder = (MenuListViewHolder) v.getTag();
		}
		String str = mDatesString.get(position);
		viewHolder.tvDate.setText(str);
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime = mDates.get(position) - DishAdapter.HOURS_7;
		if (currentUnixTime > menuUnixTime) {
			viewHolder.tvDate.setTextColor(Color.LTGRAY);
			viewHolder.ivLock
					.setImageResource(R.drawable.ic_device_access_secure);
		} else
			viewHolder.tvDate.setTextColor(Color.BLACK);
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
		TextView tvDate;
		ImageView ivLock;
	}
}