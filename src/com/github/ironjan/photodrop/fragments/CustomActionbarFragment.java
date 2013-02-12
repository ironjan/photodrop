package com.github.ironjan.photodrop.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.dbwrap.DownSyncCallback;
import com.github.ironjan.photodrop.dbwrap.Syncer;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;

@EFragment(R.layout.view_emptry)
public class CustomActionbarFragment extends SherlockFragment implements
		DownSyncCallback {

	private static final com.actionbarsherlock.app.ActionBar.LayoutParams sCustomABLayoutParams = new ActionBar.LayoutParams(
			Gravity.CENTER_VERTICAL | Gravity.RIGHT);

	private ImageButton btnRefresh;

	private View mCustomABRefresh;

	private View mCustumABProgress;

	@Bean
	Syncer mSyncer;

	private ActionBar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initCustomABViews(savedInstanceState);
		initCustomActionBar();
		setupCustomActionBarOnClick();
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	private void initCustomABViews(Bundle savedInstanceState) {
		final LayoutInflater inflater = getLayoutInflater(savedInstanceState);
		mCustomABRefresh = inflater.inflate(R.layout.actionbar_refresh, null);
		mCustumABProgress = inflater.inflate(R.layout.actionbar_progress, null);
	}

	private ActionBar initCustomActionBar() {
		mActionBar = getSherlockActivity().getSupportActionBar();
		mActionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

		btnRefresh = (ImageButton) mCustomABRefresh
				.findViewById(R.id.btnRefresh);

		return mActionBar;
	}

	@AfterInject
	void setDownSyncCallback(){
		mSyncer.setDownSyncCallback(this);
	}
	void setupCustomActionBarOnClick() {
		// we need to set up click listener because btnRefresh is not found by
		// AndroidAnnotations

		if (btnRefresh != null) {
			btnRefresh.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					refresh();
				}
			});
		}
	}

	@Background
	void refresh() {
		showProgressInAB();
		mSyncer.forceDownSync();
	}

	@UiThread
	void showProgressInAB() {
		mActionBar.setCustomView(mCustumABProgress, sCustomABLayoutParams);
	}

	@UiThread
	void showRefreshInAB() {
		mActionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
	}

	@Override
	public void syncFinished() {
		showRefreshInAB();
	}

}
