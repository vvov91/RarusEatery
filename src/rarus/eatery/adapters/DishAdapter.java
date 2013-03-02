package rarus.eatery.adapters;

import java.util.ArrayList;

import rarus.eatery.R;
import rarus.eatery.activity.MainActivity;
import rarus.eatery.model.DayMenu;
import rarus.eatery.model.Dish;

import android.content.Context;
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
	ArrayList<DayMenu> objects;
	Button btnMinus, btnPlus;
	TextView tvAmount;
	String date;
	public DishAdapter(Context context, ArrayList<DayMenu> products, String date) {
		this.date=date;
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
			view = lInflater.inflate(R.layout.dish, parent, false);
		}

		final Dish p = getProduct(position);
		// заполняем View в пункте списка данными из товаров: наименование, цена
		((TextView) view.findViewById(R.id.tvName)).setText(p.getName());
		((TextView) view.findViewById(R.id.tvPrice)).setText(p.getPrice() + " грн");
		((Button) view.findViewById(R.id.btnRating)).setText(p.getRating());
		tvAmount = (TextView) view.findViewById(R.id.tvAmount);
		//tvAmount.setText(p.getOrderedAmmount() + "");
		tvAmount.setText("");

		btnMinus = (Button) view.findViewById(R.id.btnMinus);
		btnPlus = (Button) view.findViewById(R.id.btnPlus);
		btnMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//float orderedAmmount = p.getOrderedAmmount();
				boolean portioned = p.isPortioned();
				float step = (float) (portioned ? 0.5 : 1);
				/*if (orderedAmmount > step)
					orderedAmmount -= step;
				else
					orderedAmmount = 0;
				p.setOrderedAmmount(orderedAmmount);*/
				notifyDataSetChanged();
				MainActivity.changedOrderedAmount=true;
			}
		});
		btnPlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//float orderedAmmount = p.getOrderedAmmount();
				boolean portioned = p.isPortioned();
				//float availableAmmount = p.getAvailableAmmount();
				float step = (float) (portioned ? 0.5 : 1);
				/*if ((orderedAmmount + step <= availableAmmount)||(availableAmmount==-1))
					orderedAmmount += step;
				else
					Toast.makeText(
							ctx,
							"AvailableAmmount "
									+ Float.toString(p.getAvailableAmmount()),
							3).show();
				p.setOrderedAmmount(orderedAmmount);*/
				MainActivity.changedOrderedAmount=true;
				notifyDataSetChanged();
			}
		});
		return view;
	}

	Dish getProduct(int position) {
		return ((Dish) getItem(position));
	}

}