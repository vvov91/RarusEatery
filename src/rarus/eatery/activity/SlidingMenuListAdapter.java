package rarus.eatery.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SlidingMenuListAdapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<String> objects;

	SlidingMenuListAdapter(Context context, ArrayList<String> products) {
		ctx = context;
		objects = products;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// ���-�� ���������
	@Override
	public int getCount() {
		return objects.size();
	}

	// ������� �� �������
	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	// id �� �������
	@Override
	public long getItemId(int position) {
		return position;
	}

	// ����� ������
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ���������� ���������, �� �� ������������ view
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);
		}
		String str = getProduct(position);
		((TextView) view.findViewById(android.R.id.text1)).setText(str);
		return view;
	}

	String getProduct(int position) {
		return ((String) getItem(position));
	}

}