package com.github.ironjan.photodrop;

import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;

@EActivity(R.layout.act_os_libs)
public class OSLibsActivity extends SherlockFragmentActivity {

	@AfterViews
	void setHomeAsUp() {
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@OptionsItem
	void homeSelected() {
		NavUtils.navigateUpTo(this, getParentActivityIntent());
	}
	
}
