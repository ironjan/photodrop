package com.github.ironjan.photodrop;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.ironjan.photodrop.fragments.ShareFragment;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.FragmentById;

@EActivity(R.layout.act_share)
public class ShareActivity extends SherlockFragmentActivity {
	@Extra
	String photoUri;

	@FragmentById(R.id.frgmtShare)
	ShareFragment mFragment;

	@AfterViews
	void passData() {
		mFragment.setUri(photoUri);
	}
}
