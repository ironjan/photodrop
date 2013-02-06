package com.github.ironjan.photodrop.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;

@EFragment(R.layout.view_emptry)
public class CustomActionbarFragment extends SherlockFragment{
	private static final com.actionbarsherlock.app.ActionBar.LayoutParams sCustomABLayoutParams = new ActionBar.LayoutParams(
			Gravity.CENTER_VERTICAL | Gravity.RIGHT);

	private ImageButton btnRefresh;

	private View mCustomABRefresh;

	private View mCustumABProgress;

	private ActionBar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initCustomABViews(savedInstanceState);
		initCustomActionBar();
		setupCustomActionBarOnClick();
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	void initCustomABViews(Bundle savedInstanceState) {
		final LayoutInflater inflater = getLayoutInflater(savedInstanceState);
		mCustomABRefresh = inflater.inflate(R.layout.actionbar_refresh, null);
		mCustumABProgress = inflater.inflate(R.layout.actionbar_progress, null);
	}

	ActionBar initCustomActionBar() {
		mActionBar = getSherlockActivity().getSupportActionBar();
		mActionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

		btnRefresh = (ImageButton) mCustomABRefresh
				.findViewById(R.id.btnRefresh);

		return mActionBar;
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
		CroutonW.showInfo(getActivity(), "showing progress for nothing..."); //$NON-NLS-1$
		showProgressInAB();
	}

	@UiThread
	void showProgressInAB() {
		mActionBar.setCustomView(mCustumABProgress, sCustomABLayoutParams);
		doSomeCounting();
	}

	@Background
	void doSomeCounting() {
		long start = System.currentTimeMillis();
		long counter = 0;
		while ((System.currentTimeMillis() - start) < 5000) {
			counter++;
		}
		showRefreshInAB();
	}

	@UiThread
	void showRefreshInAB() {
		mActionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
	}

}
