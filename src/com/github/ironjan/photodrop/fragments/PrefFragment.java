package com.github.ironjan.photodrop.fragments;

import android.widget.EditText;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.StartActivity_;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.github.ironjan.photodrop.helper.Prefs_;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EFragment(R.layout.frgmt_pref)
public class PrefFragment extends SherlockFragment {

	private static final int MINUTE = 60 * 1000;

	@ViewById
	ToggleButton tglAutomaticSync;

	@ViewById
	EditText txtSyncInterval;

	@Pref
	Prefs_ prefs;

	@Bean
	SessionKeeper sessionKeeper;

	@AfterViews
	void bindPreferences() {
		txtSyncInterval.setText(prefs.syncIntervall().get() / MINUTE + ""); //$NON-NLS-1$
		tglAutomaticSync.setChecked(prefs.automaticSync().get());
	}

	@Override
	public void onPause() {
		boolean automaticSync = tglAutomaticSync.isChecked();
		int syncInterval = MINUTE* Integer.parseInt(txtSyncInterval
				.getText().toString());

		prefs.edit().automaticSync().put(automaticSync).syncIntervall()
				.put(syncInterval).apply();

		super.onPause();
	}

	@Click(R.id.btnUnlink)
	void unlinkDropbox() {
		sessionKeeper.unlink();
		StartActivity_.intent(getActivity()).start();
	}
}
