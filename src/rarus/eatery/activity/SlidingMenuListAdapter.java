package rarus.eatery.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SlidingMenuListAdapter extends ArrayAdapter {

	private ArrayList<String> dates;
	private int selectedPosition = -1;
	private Context context;

	public SlidingMenuListAdapter(Context context, int textViewResourceId,
			ArrayList<String> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.dates = objects;
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
		String str = dates.get(position);
		viewHolder.text1.setText(str);
		if ((selectedPosition != -1) && (selectedPosition == position))
			v.setBackgroundColor(Color.DKGRAY);
		else
			v.setBackgroundColor(Color.WHITE);

		return v;
	}

	public void setSelected(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	static class MenuListViewHolder {
		TextView text1;
	}
}