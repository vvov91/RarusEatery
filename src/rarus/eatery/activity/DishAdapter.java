package rarus.eatery.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Adapter class to show the dishes
 */
public class DishAdapter extends ArrayAdapter {

	private Context context;
	private ArrayList<RarusMenu> mMenu;
	private OnDishItemListener changeAmount;
	public static final int HOURS_7 = 25200;

	public DishAdapter(Context context, int textViewResourceId,
			ArrayList<RarusMenu> menu, OnDishItemListener changeAmount) {
		super(context, textViewResourceId, menu);
		this.context = context;
		this.mMenu = menu;
		this.changeAmount = changeAmount;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		DishViewHolder viewHolder;
		if (v == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.dish, parent, false);
			viewHolder = new DishViewHolder();
			viewHolder.tvName = (TextView) v.findViewById(R.id.tvName);
			viewHolder.tvPrice = (TextView) v.findViewById(R.id.tvPrice);
			viewHolder.tvTotal = (TextView) v.findViewById(R.id.tvTotal);
			viewHolder.tvAmount = (TextView) v.findViewById(R.id.tvAmount);
			viewHolder.btnMinus = (Button) v.findViewById(R.id.btnMinus);
			viewHolder.btnPlus = (Button) v.findViewById(R.id.btnPlus);
			v.setTag(viewHolder);
		} else {
			viewHolder = (DishViewHolder) v.getTag();
		}

		final RarusMenu rarusMenu = mMenu.get(position);
		if (rarusMenu != null) {
			viewHolder.tvName.setText(rarusMenu.getName());
			viewHolder.tvName.setTextColor(Color.rgb(255, 99, 33));
			viewHolder.tvPrice.setText(getContext().getResources().getString(
					R.string.price)
					+ rarusMenu.getPrice()
					+ getContext().getResources().getString(R.string.hrn));
			viewHolder.tvAmount.setText(rarusMenu.getAmmount() + "");

			if (rarusMenu.getAmmount() != 0) {
				float total = rarusMenu.getAmmount() * rarusMenu.getPrice();
				DecimalFormat decimalFormat = new DecimalFormat("###.#");
				viewHolder.tvTotal.setText(getContext().getResources()
						.getString(R.string.total)
						+ decimalFormat.format(total)
						+ getContext().getResources().getString(R.string.hrn));
				viewHolder.tvAmount.setTypeface(null, Typeface.BOLD);
			} else {
				viewHolder.tvAmount.setTypeface(null, Typeface.NORMAL);
				viewHolder.tvTotal.setText(null);
			}
			viewHolder.btnMinus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					changeAmount.onClickMinus(rarusMenu);
					notifyDataSetChanged();
				}
			});
			viewHolder.btnPlus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					changeAmount.onClickPlus(rarusMenu);
					notifyDataSetChanged();
				}
			});
			long currentUnixTime = System.currentTimeMillis() / 1000L;
			int menuUnixTime = rarusMenu.getDate() - HOURS_7;
			if (currentUnixTime > menuUnixTime) {
				viewHolder.btnPlus.setEnabled(false);
				viewHolder.btnMinus.setEnabled(false);
			}
		}
		v.setBackgroundColor(Color.WHITE);

		return v;
	}

	static class DishViewHolder {
		TextView tvAmount;
		TextView tvName;
		TextView tvPrice;
		TextView tvTotal;
		Button btnMinus;
		Button btnPlus;
	}
}