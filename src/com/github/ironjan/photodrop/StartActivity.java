package com.github.ironjan.photodrop;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.res.StringRes;

import de.keyboardsurfer.android.widget.crouton.Crouton;

@EActivity(R.layout.act_authentification)
@OptionsMenu(R.menu.main)
public class StartActivity extends SherlockActivity {

	public static final int REQUEST_LINK_TO_DBX = 0;

	@Bean
	DropboxWrapper sessionKeeper;

	@StringRes
	String couldNotLinkMsg;

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
			CroutonW.showAlert(this, couldNotLinkMsg);
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

	@OptionsItem(R.id.mnuAbout)
	void mnuAboutClicked() {
		OSLibsActivity_.intent(this).start();
	}
}
