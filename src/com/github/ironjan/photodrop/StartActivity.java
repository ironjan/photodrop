package com.github.ironjan.photodrop;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

import de.keyboardsurfer.android.widget.crouton.Crouton;

@EActivity(R.layout.act_authentification)
public class StartActivity extends SherlockActivity {

	private static final String TAG = StartActivity.class.getSimpleName();
	@Bean
	SessionKeeper sessionKeeper;
	@StringRes
	String authentificationNoInternet, authentificationError,
			authentificationSuccessful, linkDropbox, unlinkDropbox,
			dropboxUnlinkSucces;

	@ViewById
	View content;

	@ViewById
	Button btnLink;

	private boolean mLoggedIn;

	@Click(R.id.btnLink)
	void bntLinkClicked() {
		if (mLoggedIn) {
			sessionKeeper.unlink();
			CroutonW.showInfo(this, dropboxUnlinkSucces);
			setLoginStatus(false);
		} else {
			authenticate();
		}
		
		
	}

	void authenticate() {
		try {
			sessionKeeper.startAuthentication(StartActivity.this);
		} catch (IllegalStateException e) {
			CroutonW.showAlert(this, e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			sessionKeeper.finishAuthentication();
			CroutonW.showConfirm(this, authentificationSuccessful);
		} catch (IllegalStateException e) {
			Log.e(TAG,
					"The user is not authentificated. This exception can normally be ignored.", //$NON-NLS-1$
					e);
		}

		setLoginStatus(sessionKeeper.isLinked());
	}

	private void setLoginStatus(boolean loggedIn) {
		mLoggedIn = loggedIn;
		if (loggedIn) {
			btnLink.setText(unlinkDropbox);
		} else {
			btnLink.setText(linkDropbox);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Crouton.cancelAllCroutons();
	}

}
