package rarus.eatery.activity;

import java.util.ArrayList;

import rarus.eatery.R;
import rarus.eatery.model.Order;
import rarus.eatery.model.RarusMenu;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DishAdapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<RarusMenu> mMenu;
	Button btnMinus, btnPlus;
	TextView tvAmount;
	String date;

	DishAdapter(Context context, ArrayList<RarusMenu> menu, String date) {
		this.date = date;
		ctx = context;
		mMenu = menu;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// кол-во элементов
	@Override
	public int getCount() {
		return mMenu.size();
	}

	// элемент по позиции
	@Override
	public Object getItem(int position) {
		return mMenu.get(position);
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
			view = lInflater.inflate(R.layout.dish, parent, false);
		}

		final RarusMenu p = mMenu.get(position);
		// заполняем View в пункте списка данными из товаров: наименование, цена
		((TextView) view.findViewById(R.id.tvName)).setText(p.getName());
		((TextView) view.findViewById(R.id.tvPrice)).setText(p.getPrice()
				+ " грн");
		// ((Button) view.findViewById(R.id.btnRating)).setText(p.getRating());
		tvAmount = (TextView) view.findViewById(R.id.tvAmount);
		tvAmount.setText(p.getAmmount() + "");
		btnMinus = (Button) view.findViewById(R.id.btnMinus);
		btnPlus = (Button) view.findViewById(R.id.btnPlus);
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
				notifyDataSetChanged();
				// MainActivity.changedOrderedAmount=true;
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
							ctx,
							"AvailableAmmount "
									+ Float.toString(p.getAvailable()), 3)
							.show();
				p.setAmmount(orderedAmmount);
				p.setModified(true);
				// MainActivity.changedOrderedAmount=true;
				notifyDataSetChanged();
			}
		});
		return view;
	}

}