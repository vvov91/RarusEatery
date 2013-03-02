package rarus.eatery.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import rarus.eatery.R;
import rarus.eatery.activity.MainActivity;
import rarus.eatery.adapters.DishAdapter;
import rarus.eatery.database.DBManager;
import rarus.eatery.model.DayMenu;
import rarus.eatery.model.Dish;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class DayMenuFragment extends Fragment {
	MenuItem fav;
	final String TAG = "States";
	public ArrayList<DayMenu> dishes = new ArrayList<DayMenu>();
	DishAdapter dishAdapter;
	GridView gvMain;
	public String date;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu, null);
		Button btnOrder = (Button) v.findViewById(R.id.btnOrder);
		TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
		tvDate.setText(date);
		dishAdapter = new DishAdapter(v.getContext(), dishes, date);
		gvMain = (GridView) v.findViewById(R.id.gvMain);
		gvMain.setAdapter(dishAdapter);

		adjustGridView();
		btnOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date dateDate = null;
				try {
					dateDate = df.parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DBManager db = new DBManager(v.getContext());
				db.open();
				int dateInt = (int) (dateDate.getTime() / 1000)+7200;
				db.deleteMenuAtDate(dateInt);
				//db.addMenu(dateInt, dishes);
				//db.addOrder(dateInt, dishes);
				db.close();
				MainActivity.changedOrderedAmount = false;
				Toast.makeText(v.getContext(), "заказ сохранен", 3).show();
			}
		});
		Log.d(TAG, "menu" + date + ": onCreate()");

		
		
		return v;
	}

	private void adjustGridView() {

		Configuration config = getResources().getConfiguration();
		gvMain.setNumColumns(config.orientation+1);
		gvMain.setVerticalSpacing(5);
		gvMain.setHorizontalSpacing(5);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "menu: onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "menu: onResume()");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "menu: onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "menu: onStop()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "menu: onDestroy()");
	}

}