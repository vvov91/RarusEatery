package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.Date;

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
		Date d = new Date(((long) mDates.get(position)) * 1000);
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime;
		if (d.getDay() != 1)
			menuUnixTime = mDates.get(position) - DishAdapter.HOURS_7;
		else
			menuUnixTime = mDates.get(position) - DishAdapter.HOURS_55;
		if (currentUnixTime > menuUnixTime) {
			viewHolder.tvDate.setTextColor(Color.LTGRAY);
			viewHolder.ivLock
					.setImageResource(R.drawable.ic_device_access_secure);
		} else {
			viewHolder.tvDate.setTextColor(getContext().getResources()
					.getColor(R.color.pressed_rarus_2));
			viewHolder.ivLock.setImageDrawable(null);
		}
		if ((mSelectedPosition != -1) && (mSelectedPosition == position))
			v.setBackgroundColor(getContext().getResources().getColor(
					R.color.orange));
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