package com.github.ironjan.photodrop;

import android.support.v4.app.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.github.ironjan.photodrop.fragments.*;
import com.googlecode.androidannotations.annotations.*;

@EActivity(R.layout.act_single_post)
public class SinglePostActivity extends SherlockFragmentActivity {
	@Extra
	String imagePath;

	public final static String EXTRA_IMAGE_PATH = "imagePath";

	@ViewById(android.R.id.content)
	FrameLayout content;

	@AfterViews
	void showData() {
		SinglePostFragment postFragment = SinglePostFragment_.builder().build();
		if (getIntent() != null && getIntent().getExtras() != null) {
			postFragment.setArguments(getIntent().getExtras());
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(android.R.id.content, postFragment);
		ft.commit();

	}
}
