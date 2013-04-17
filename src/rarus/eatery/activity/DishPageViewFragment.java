package rarus.eatery.activity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.graphics.Color;
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
		tvName.setTypeface(null, Typeface.BOLD);
		((TextView) v.findViewById(R.id.tvPrice)).setText(getResources()
				.getString(R.string.price)
				+ p.getPrice()
				+ getResources().getString(R.string.hrn));
		((TextView) v.findViewById(R.id.tvRating)).setText(getResources()
				.getString(R.string.rating) + p.getRating());
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
		tvAmount.setText("" + p.getAmmount());
		if (p.getAmmount() != 0) {
			tvAmount.setTypeface(null, Typeface.BOLD);
			float total = p.getAmmount() * p.getPrice();
			DecimalFormat decimalFormat = new DecimalFormat("###.##");
			tvTotal.setText(getResources().getString(R.string.total) + "\n"
					+ decimalFormat.format(total)
					+ getResources().getString(R.string.hrn));
		} else {
			tvAmount.setTypeface(null, Typeface.NORMAL);
			tvTotal.setText(null);
		}

		((TextView) v.findViewById(R.id.tvDescription)).setText(getResources()
				.getString(R.string.description) + "\n" + p.getDescription());

		Button btnMinus = (Button) v.findViewById(R.id.btnMinus);
		Button btnPlus = (Button) v.findViewById(R.id.btnPlus);
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
							"AvailableAmmount "
									+ Float.toString(p.getAvailable()), 3)
							.show();
				p.setAmmount(orderedAmmount);
				p.setModified(true);
				// reload fragment
				DishPageViewActivity activity = (DishPageViewActivity) getActivity();
				activity.reloadFragmentData();
				SlidingMenuActivity.mChangedOrderedAmount = true;
				Log.d("int", "" + SlidingMenuActivity.mChangedOrderedAmount);
			}
		});
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		int menuUnixTime = p.getDate() - DishAdapter.HOURS_7;
		if (currentUnixTime > menuUnixTime) {
			btnPlus.setEnabled(false);
			btnMinus.setEnabled(false);
		}
		return v;
	}
}