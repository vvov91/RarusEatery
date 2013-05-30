package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.Date;

import rarus.eatery.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationListAdapter extends BaseAdapter {
	private ArrayList<String> mDatesString;
	private ArrayList<Integer> mDates;
	private Context mContext;
	private LayoutInflater mInflator;

	public NavigationListAdapter(Context context,
			ArrayList<String> mDatesString, ArrayList<Integer> mDates) {
		mContext = context;
		mInflator = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mDatesString = mDatesString;
		this.mDates = mDates;
	}

	@Override
	public int getCount() {
		return mDatesString.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatesString.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.navigation_list_item,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ivLock = (ImageView) convertView
					.findViewById(R.id.ivLock);
			viewHolder.tvDate = (TextView) convertView
					.findViewById(R.id.tvDate);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String str = mDatesString.get(position);
		viewHolder.tvDate.setText(str);
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime = mDates.get(position) - DishAdapter.HOURS_7;
		if (currentUnixTime > menuUnixTime) {
			viewHolder.tvDate.setTextColor(Color.LTGRAY);
			viewHolder.ivLock
					.setImageResource(R.drawable.ic_device_access_secure);
		} else {
			viewHolder.tvDate.setTextColor(mContext.getResources().getColor(
					R.color.pressed_rarus_2));
			viewHolder.ivLock.setImageDrawable(null);
		}
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflator.inflate(
					R.layout.navigation_list_dropdown_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ivLock = (ImageView) convertView
					.findViewById(R.id.ivLock);
			viewHolder.tvDate = (TextView) convertView
					.findViewById(R.id.tvDate);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
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
			viewHolder.tvDate.setTextColor(mContext.getResources().getColor(
					R.color.pressed_rarus_2));
			viewHolder.ivLock.setImageDrawable(null);
		}
		return convertView;
	}

	private class ViewHolder {
		public ImageView ivLock;
		public TextView tvDate;
	}
}