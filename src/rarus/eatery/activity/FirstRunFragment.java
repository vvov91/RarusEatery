package rarus.eatery.activity;

import rarus.eatery.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FirstRunFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.loading, null);

		final ProgressBar pb = (ProgressBar) v.findViewById(R.id.progressBar1);
		TextView tv = (TextView) v.findViewById(R.id.tv);
		tv.setText("Загружается меню...");
		// pb.setVisibility(View.INVISIBLE);

		// final Button btnDownload = (Button) v.findViewById(R.id.btnDownload);
		// btnDownload.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// pb.setVisibility(View.VISIBLE);
		// btnDownload.setEnabled(false);
		// btnDownload.setText("downloading...");
		// SlidingMenuActivity ra = (SlidingMenuActivity) getActivity();
		// ra.onRefreshClick(v);
		// }
		// });

		return v;
	}
}