package rarus.eatery.activity;

import java.util.ArrayList;
import java.util.Date;

import rarus.eatery.R;
import rarus.eatery.model.RarusMenu;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class DayMenuFragment extends Fragment implements Parcelable,
		OnDishItemListener {
	ArrayList<RarusMenu> mRarusMenu = new ArrayList<RarusMenu>();
	String mStringDate;
	Date mDate;
	int mPosition = -1;
	DishAdapter mDishAdapter;
	GridView mGridView;

	public DayMenuFragment() {
	}

	public DayMenuFragment(ArrayList<RarusMenu> _mRarusMenues, String _mDate,
			int _mPos) {
		mRarusMenu = _mRarusMenues;
		mStringDate = _mDate;
		mPosition = _mPos;
	}

	private DayMenuFragment(Parcel parcel) {
		parcel.readList(mRarusMenu, RarusMenu.class.getClassLoader());
		mStringDate = parcel.readString();
		mPosition = parcel.readInt();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu, null);
		if (mPosition == -1 && savedInstanceState != null)
			mPosition = savedInstanceState.getInt("mPosition");
		mDishAdapter = new DishAdapter(v.getContext(), R.id.gvMain, mRarusMenu,
				this);
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
				activity.onDishPressed(mPosition, position);
			}
		});
		return v;
	}

	public void refreshAdapter() {
		// TODO understand why the adapter.notifyDataSetChanged() does not work
		mDishAdapter = new DishAdapter(getView().getContext(), R.id.gvMain,
				mRarusMenu, this);
		mGridView.setAdapter(mDishAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mPosition", mPosition);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeList(mRarusMenu);
		parcel.writeString(mStringDate);
		parcel.writeInt(mPosition);
	}

	public static final Parcelable.Creator<DayMenuFragment> CREATOR = new Parcelable.Creator<DayMenuFragment>() {
		// распаковываем объект из Parcel
		public DayMenuFragment createFromParcel(Parcel in) {
			return new DayMenuFragment(in);
		}

		public DayMenuFragment[] newArray(int size) {
			return new DayMenuFragment[size];
		}
	};

	public void setRarusMenu(ArrayList<RarusMenu> rarusMenu) {
		mRarusMenu = rarusMenu;
	}

	public void setStringDate(String stringDate) {
		mStringDate = stringDate;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	@Override
	public void onClickMinus(RarusMenu rarusMenu) {
		float orderedAmmount = rarusMenu.getAmmount();
		boolean portioned = rarusMenu.isPortioned();
		float step = (float) (portioned ? 0.5 : 1);
		if (orderedAmmount > step)
			orderedAmmount -= step;
		else
			orderedAmmount = 0;
		rarusMenu.setAmmount(orderedAmmount);
		rarusMenu.setModified(true);
		SlidingMenuActivity.mChangedOrderedAmount = true;
		Log.d("int", "" + SlidingMenuActivity.mChangedOrderedAmount);
	}

	@Override
	public void onClickPlus(RarusMenu rarusMenu) {
		float orderedAmmount = rarusMenu.getAmmount();
		boolean portioned = rarusMenu.isPortioned();
		float availableAmmount = rarusMenu.getAvailable();
		float step = (float) (portioned ? 0.5 : 1);
		if ((orderedAmmount + step <= availableAmmount)
				|| (availableAmmount == -1))
			orderedAmmount += step;
		else
			Toast.makeText(
					getActivity().getBaseContext(),
					"AvailableAmmount "
							+ Float.toString(rarusMenu.getAvailable()), 3)
					.show();
		rarusMenu.setAmmount(orderedAmmount);
		rarusMenu.setModified(true);
		SlidingMenuActivity.mChangedOrderedAmount = true;
		Log.d("int", "" + SlidingMenuActivity.mChangedOrderedAmount);
	}
}