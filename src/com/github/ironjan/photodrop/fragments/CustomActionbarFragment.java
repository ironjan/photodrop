package com.github.ironjan.photodrop.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.dbwrap.sync.DownSyncCallback;
import com.github.ironjan.photodrop.dbwrap.sync.SyncService_;
import com.github.ironjan.photodrop.dbwrap.sync.Syncer;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;

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

	private Bundle mSavedInstanceState;

	@Override
	public void onResume() {
		initCustomABViews();
		initCustomActionBar();
		setupCustomActionBarOnClick();
		setRetainInstance(true);
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mSavedInstanceState = savedInstanceState;
		super.onCreate(savedInstanceState);
	}

	private void initCustomABViews() {
		final LayoutInflater inflater = getLayoutInflater(mSavedInstanceState);
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

	@AfterViews
	void setDownSyncCallback() {
		mSyncer.setDownSyncCallback(this);
		startAutomaticSync();
	}

	private void startAutomaticSync() {
		SyncService_.intent(getSherlockActivity()).start();
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

	void refresh() {
		showProgressInAB();
		mSyncer.forceDownSync();
	}

	void showProgressInAB() {
		mActionBar.setCustomView(mCustumABProgress, sCustomABLayoutParams);
	}

	void showRefreshInAB() {
		mActionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
	}

	@Override
	public void syncFinished() {
		showRefreshInAB();
	}

	@Override
	public void syncStarted() {
		showProgressInAB();
	}
}
