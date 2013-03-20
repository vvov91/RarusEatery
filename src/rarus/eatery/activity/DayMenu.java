package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.Date;

import rarus.eatery.R;
import rarus.eatery.database.EateryDB;
import rarus.eatery.model.RarusMenu;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class DayMenu extends Fragment implements Parcelable {
	final String TAG = "States";
	ArrayList<RarusMenu> mRarusMenu = new ArrayList<RarusMenu>();
	String mStringDate;
	Date mDate;
	int mPos = -1;
	DishAdapter mDishAdapter;
	GridView mGridView;

	public DayMenu() {
	}

	public DayMenu(ArrayList<RarusMenu> _mRarusMenues, String _mDate, int _mPos) {
		mRarusMenu = _mRarusMenues;
		mStringDate = _mDate;
		mPos = _mPos;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu, null);
		if (mPos == -1 && savedInstanceState != null)
			mPos = savedInstanceState.getInt("mPos");
		Button btnOrder = (Button) v.findViewById(R.id.btnOrder);
		mDishAdapter = new DishAdapter(v.getContext(), mRarusMenu, mStringDate);
		mGridView = (GridView) v.findViewById(R.id.gvMain);
		mGridView.setAdapter(mDishAdapter);
		Configuration config = getResources().getConfiguration();
		mGridView.setNumColumns(config.orientation);
		mGridView.setVerticalSpacing(5);
		mGridView.setHorizontalSpacing(5);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (getActivity() == null)
					return;
				SlidingMenuActivity activity = (SlidingMenuActivity) getActivity();
				activity.onDishPressed(mPos, position);
			}
		});
		btnOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveOrder();
			}
		});
		Log.d(TAG, "menu" + mStringDate + ": onCreate()");
		return v;
	}

	public void refreshAdapter() {
		// TODO understand why the adapter.notifyDataSetChanged() does not work
		mDishAdapter = new DishAdapter(getView().getContext(), mRarusMenu,
				mStringDate);
		mGridView.setAdapter(mDishAdapter);
	}

	public void saveOrder() {
		EateryDB db = new EateryDB(getView().getContext());
		db.saveMenu(mRarusMenu);
		Toast.makeText(getView().getContext(), "заказ сохранен", 3).show();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mPos", mPos);
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeList(mRarusMenu);
		parcel.writeString(mStringDate);
		parcel.writeInt(mPos);
	}

	public static final Parcelable.Creator<DayMenu> CREATOR = new Parcelable.Creator<DayMenu>() {
		// распаковываем объект из Parcel
		public DayMenu createFromParcel(Parcel in) {
			return new DayMenu(in);
		}

		public DayMenu[] newArray(int size) {
			return new DayMenu[size];
		}
	};

	private DayMenu(Parcel parcel) {
		parcel.readList(mRarusMenu, RarusMenu.class.getClassLoader());
		mStringDate = parcel.readString();
		mPos = parcel.readInt();
	}

}