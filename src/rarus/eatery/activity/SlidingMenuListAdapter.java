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
	int selectedPositin = -1;

	SlidingMenuListAdapter(Context context, ArrayList<String> products) {
		ctx = context;
		objects = products;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// кол-во элементов
	@Override
	public int getCount() {
		return objects.size();
	}

	public void setSelected(int selectedPositin) {
		this.selectedPositin = selectedPositin;
	}

	// элемент по позиции
	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	// id по позиции
	@Override
	public long getItemId(int position) {
		return position;
	}

	// пункт списка
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// используем созданные, но не используемые view
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);
		}
		String str = getProduct(position);
		((TextView) view.findViewById(android.R.id.text1)).setText(str);
		view.setBackgroundColor(Color.WHITE);
		if ((selectedPositin!=-1)&&(selectedPositin==position)) view.setBackgroundColor(Color.DKGRAY);
		return view;
	}

	String getProduct(int position) {
		return ((String) getItem(position));
	}

}