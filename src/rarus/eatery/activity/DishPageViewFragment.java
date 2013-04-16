package rarus.eatery.activity;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DishPageViewFragment extends Fragment {
	public RarusMenu p;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dish_viewpager_frame, null);

		((TextView) v.findViewById(R.id.tvName)).setText(p.getName());
		((TextView) v.findViewById(R.id.tvPrice)).setText(getResources()
				.getString(R.string.price) + p.getPrice() + " грн");
		((TextView) v.findViewById(R.id.tvRating)).setText(getResources()
				.getString(R.string.rating) + p.getRating());
		TextView tvAmount = (TextView) v.findViewById(R.id.tvAmount);
		tvAmount.setText("" + p.getAmmount());
		if (p.getAmmount() != 0) {
			tvAmount.setTypeface(null, Typeface.BOLD);
		} else
			tvAmount.setTypeface(null, Typeface.NORMAL);

		((TextView) v.findViewById(R.id.tvDescription)).setText(p
				.getDescription());

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