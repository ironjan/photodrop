package com.github.ironjan.photodrop;

import android.app.Activity;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;

import de.keyboardsurfer.android.widget.crouton.Crouton;

@EActivity(R.layout.act_authentification)
@OptionsMenu(R.menu.main)
public class StartActivity extends SherlockActivity {

	private static final String TAG = StartActivity.class.getSimpleName();
	public static final int REQUEST_LINK_TO_DBX = 0;

	@OptionsItem(R.id.mnuAbout)
	void mnuAboutClicked() {
		OSLibsActivity_.intent(this).start();
	}

	@Bean
	SessionKeeper sessionKeeper;

	@Click(R.id.btnLink)
	void startAuthentication() {
		try {
			sessionKeeper.startAuthentication(StartActivity.this);
		} catch (IllegalStateException e) {
			CroutonW.showAlert(this, e);
		}
	}

	@OnActivityResult(REQUEST_LINK_TO_DBX)
	void resultLink(int resultCode) {
		if (resultCode != Activity.RESULT_OK) {
			CroutonW.showAlert(this,
					"Could not link to Dropbox. This app needs Dropbox access to work");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (sessionKeeper.isLinked()) {
			StreamActivity_.intent(this).start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Crouton.cancelAllCroutons();
	}

}
