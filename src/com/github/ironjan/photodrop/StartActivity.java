package com.github.ironjan.photodrop;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@EActivity(R.layout.act_authentification)
@OptionsMenu(R.menu.main)
public class StartActivity extends SherlockActivity {

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
			Crouton.showText(this, e.getMessage(), Style.ALERT);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			sessionKeeper.finishAuthentication();
			if (sessionKeeper.isLinked()) {
				StreamActivity_.intent(this).start();
			}
		} catch (IllegalStateException e) {
			sessionKeeper.unlink();
			Crouton.showText(this, e.getMessage(), Style.ALERT);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Crouton.cancelAllCroutons();
	}

}
