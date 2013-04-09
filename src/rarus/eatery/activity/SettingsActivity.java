package rarus.eatery.activity;

import rarus.eatery.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.text.TextPaint;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private EditTextPreference mServer1, mServer2, mCardNumber;
	private CheckBoxPreference mWIFI;
	public static final String KEY_SERVER_1 = "server1";
	public static final String KEY_SERVER_2 = "server2";
	public static final String KEY_CARD_NUMBER = "cardNumber";
	public static final String KEY_WIFI = "onlyWI-FI";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		// Get a reference to the preferences
		mServer1 = (EditTextPreference) getPreferenceScreen().findPreference(
				KEY_SERVER_1);
		mServer2 = (EditTextPreference) getPreferenceScreen().findPreference(
				KEY_SERVER_2);
		mCardNumber = (EditTextPreference) getPreferenceScreen()
				.findPreference(KEY_CARD_NUMBER);
		mWIFI = (CheckBoxPreference) getPreferenceScreen().findPreference(
				KEY_WIFI);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Setup the initial values
		mWIFI.setSummary(getPreferenceScreen().getSharedPreferences()
				.getBoolean(KEY_WIFI, false) ? getString(R.string.anyConnection)
				: getString(R.string.onlyWIFI));
		mServer1.setSummary(getString(R.string.server1Info)
				+ " "
				+ getPreferenceScreen().getSharedPreferences().getString(
						KEY_SERVER_1, ""));
		mServer2.setSummary(getString(R.string.server2Info)
				+ " "
				+ getPreferenceScreen().getSharedPreferences().getString(
						KEY_SERVER_2, ""));
		mCardNumber.setSummary(getString(R.string.cardNumberInfo)
				+ " "
				+ getPreferenceScreen().getSharedPreferences().getString(
						KEY_CARD_NUMBER, ""));
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// Let's do something a preference value changes
		if (key.equals(KEY_WIFI)) {
			mWIFI.setSummary(getPreferenceScreen().getSharedPreferences()
					.getBoolean(KEY_WIFI, false) ? getString(R.string.anyConnection)
					: getString(R.string.onlyWIFI));
		} else if (key.equals(KEY_SERVER_1)) {
			mServer1.setSummary(getString(R.string.server1Info)
					+ " "
					+ getPreferenceScreen().getSharedPreferences().getString(
							KEY_SERVER_1, ""));
		} else if (key.equals(KEY_SERVER_2)) {
			mServer2.setSummary(getString(R.string.server2Info)
					+ " "
					+ getPreferenceScreen().getSharedPreferences().getString(
							KEY_SERVER_2, ""));
		} else if (key.equals(KEY_CARD_NUMBER)) {
			mCardNumber.setSummary(getString(R.string.cardNumberInfo)
					+ " "
					+ getPreferenceScreen().getSharedPreferences().getString(
							KEY_CARD_NUMBER, ""));
		}
	}
}