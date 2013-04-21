package rarus.eatery.activity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DishPageViewFragment extends Fragment {
	public RarusMenu p;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dish_viewpager_frame, null);
		TextView tvName = (TextView) v.findViewById(R.id.tvName);
		tvName.setText(p.getName());
		// tvName.setTypeface(null, Typeface.BOLD);
		((TextView) v.findViewById(R.id.tvPrice)).setText(getResources()
				.getString(R.string.price)
				+ " "
				+ p.getPrice()
				+ getResources().getString(R.string.hrn));
		TextView tvTotal = (TextView) v.findViewById(R.id.tvTotal);
		TextView tvAmount = (TextView) v.findViewById(R.id.tvAmount);
		String ratingNum = p.getRating().substring(0,
				p.getRating().indexOf(" "));
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
		Number number;
		try {
			number = format.parse(ratingNum);
		} catch (ParseException e) {
			e.printStackTrace();
			number = 0.0;
		}
		float ratingFloatNum = number.floatValue();
		RatingBar rbRating = (RatingBar) v.findViewById(R.id.rbRating);
		rbRating.setEnabled(false);
		rbRating.setRating(ratingFloatNum);
		if (p.getAmmount() % 1 != 0)
			tvAmount.setText(Float.toString(p.getAmmount()));
		else
			tvAmount.setText(Integer.toString((int) p.getAmmount()));
		float total = p.getAmmount() * p.getPrice();
		DecimalFormat decimalFormat = new DecimalFormat("###.##");
		tvTotal.setText(getResources().getString(R.string.total) + " "
				+ decimalFormat.format(total)
				+ getResources().getString(R.string.hrn));
		if (p.getAmmount() != 0) {
			tvAmount.setTypeface(null, Typeface.BOLD);
		} else {
			tvAmount.setTypeface(null, Typeface.NORMAL);
		}
		((TextView) v.findViewById(R.id.tvDescription)).setText(getResources()
				.getString(R.string.description) + " " + p.getDescription());
		Button btnMinus = (Button) v.findViewById(R.id.btnMinus);
		Button btnPlus = (Button) v.findViewById(R.id.btnPlus);
		btnPlus.getBackground().setColorFilter(
				new LightingColorFilter(R.color.orange, 0xFFffae1a));
		btnMinus.getBackground().setColorFilter(
				new LightingColorFilter(R.color.orange, 0xFFffae1a));

		btnMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float orderedAmmount = p.getAmmount();
				boolean portioned = p.isPortioned();
				float step = (float) (portioned ? 0.5 : 1);
				if (orderedAmmount > step)
					orderedAmmount -= step;
				else
					orderedAmmount = 0;
				p.setAmmount(orderedAmmount);
				p.setModified(true);
				// reload fragment
				DishPageViewActivity activity = (DishPageViewActivity) getActivity();
				activity.reloadFragmentData();
				SlidingMenuActivity.mChangedOrderedAmount = true;
				Log.d("int", "" + SlidingMenuActivity.mChangedOrderedAmount);

			}
		});
		btnPlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float orderedAmmount = p.getAmmount();
				boolean portioned = p.isPortioned();
				float availableAmmount = p.getAvailable();
				float step = (float) (portioned ? 0.5 : 1);
				if ((orderedAmmount + step <= availableAmmount)
						|| (availableAmmount == -1))
					orderedAmmount += step;
				else
					Toast.makeText(
							v.getContext(),
							getResources()
									.getString(R.string.available_ammount)
									+ Float.toString(p.getAvailable()),
							Toast.LENGTH_SHORT).show();
				p.setAmmount(orderedAmmount);
				p.setModified(true);
				// reload fragment
				DishPageViewActivity activity = (DishPageViewActivity) getActivity();
				activity.reloadFragmentData();
				SlidingMenuActivity.mChangedOrderedAmount = true;
				Log.d("int", "" + SlidingMenuActivity.mChangedOrderedAmount);
			}
		});
		Date d = new Date(((long) p.getDate()) * 1000);
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime;
		if (d.getDay() != 1)
			menuUnixTime = p.getDate() - DishAdapter.HOURS_7;
		else
			menuUnixTime = p.getDate() - DishAdapter.HOURS_55;
		if (currentUnixTime > menuUnixTime) {
			btnPlus.setEnabled(false);
			btnMinus.setEnabled(false);
		}
		return v;
	}
}