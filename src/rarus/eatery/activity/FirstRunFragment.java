package rarus.eatery.activity;

import rarus.eatery.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class FirstRunFragment extends Fragment implements OnClickListener {
	ProgressBar pb;
	Button btnDownload;
	EditText etCardNumber, etServer1, etServer2;
	SharedPreferences sp;
	Editor ed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.first_run_frame, null);
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getBaseContext());
		ed = sp.edit();
		etCardNumber = (EditText) v.findViewById(R.id.etCardNumber);
		etServer1 = (EditText) v.findViewById(R.id.etServer1);
		etServer2 = (EditText) v.findViewById(R.id.etServer2);
		pb = (ProgressBar) v.findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);
		btnDownload = (Button) v.findViewById(R.id.btnDownload);
		btnDownload.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		savePreference();
		pb.setVisibility(View.VISIBLE);
		btnDownload.setEnabled(false);
		btnDownload.setText("downloading...");
		SlidingMenuActivity ra = (SlidingMenuActivity) getActivity();
		ra.onRefreshClick(v);
	}

	void savePreference() {
		if (!etCardNumber.getText().toString().isEmpty())
			ed.putString("cardNumber", etCardNumber.getText().toString());
		if (!etServer1.getText().toString().isEmpty())
			ed.putString("server1", etServer1.getText().toString());
		if (!etServer2.getText().toString().isEmpty())
			ed.putString("server2", etServer2.getText().toString());
		ed.commit();
	}

	void loadPreference() {
		etCardNumber.setText(sp.getString("cardNumber", ""));
		etServer1.setText(sp.getString("server1", ""));
		etServer2.setText(sp.getString("server2", ""));
	}

	public void onResume() {
		loadPreference();
		super.onResume();
	}
}
