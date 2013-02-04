package com.github.ironjan.photodrop.fragments;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.github.ironjan.photodrop.OSLibsActivity_;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.StartActivity_;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.github.ironjan.photodrop.model.PostListAdapter;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.keyboardsurfer.android.widget.crouton.Crouton;

@EFragment
@OptionsMenu({ R.menu.dropbox, R.menu.main })
public class StreamFragment extends SherlockListFragment {

	private static final com.actionbarsherlock.app.ActionBar.LayoutParams sCustomABLayoutParams = new ActionBar.LayoutParams(
			Gravity.CENTER_VERTICAL | Gravity.RIGHT);

	@SuppressWarnings("nls")
	private static final String NYI = "Not yet implemented";

	@Bean
	SessionKeeper sessionKeeper;

	@Bean
	PostListAdapter postLA;

	@ViewById
	ListView list;

	private ImageButton btnRefresh;

	private View mCustomABRefresh;

	private View mCustumABProgress;

	private ProgressBar progress;

	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initCustomABViews(savedInstanceState);
		initCustomActionBar();
		setupCustomActionBarOnClick();
		super.onCreate(savedInstanceState);
	}

	void initCustomABViews(Bundle savedInstanceState) {
		final LayoutInflater inflater = getLayoutInflater(savedInstanceState);
		mCustomABRefresh = inflater.inflate(R.layout.actionbar_refresh, null);
		mCustumABProgress = inflater.inflate(R.layout.actionbar_progress, null);
	}

	ActionBar initCustomActionBar() {
		actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

		btnRefresh = (ImageButton) mCustomABRefresh
				.findViewById(R.id.btnRefresh);
		progress = (ProgressBar) mCustumABProgress
				.findViewById(android.R.id.progress);

		return actionBar;
	}

	void setupCustomActionBarOnClick() {
		// we need to set up click listener because btnRefresh is not found by
		// AA

		if (btnRefresh != null) {
			btnRefresh.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					refresh();
				}
			});
		}
	}

	@AfterViews
	void showSomeContent() {
		setListAdapter(postLA);
		postLA.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				invalidateList();
			}
		});
	}

	void invalidateList() {
		list.invalidate();
	}

	@OptionsItem(R.id.mnuDropboxUnlink)
	void unlinkDropbox() {
		sessionKeeper.unlink();
		StartActivity_.intent(getActivity()).start();
	}

	@OptionsItem(R.id.mnuPhoto)
	void takePhoto() {
		CroutonW.showInfo(getActivity(), NYI);
	}

	@OptionsItem(R.id.mnuChoose)
	void chooseExistingPicture() {
		CroutonW.showInfo(getActivity(), NYI);
	}

	@OptionsItem(R.id.mnuAbout)
	void mnuAboutClicked() {
		OSLibsActivity_.intent(getActivity()).start();
	}

	void refresh() {
		CroutonW.showInfo(getActivity(), "showing progress for nothing...");
		showProgressInAB();
	}

	@UiThread
	void showProgressInAB() {
		actionBar.setCustomView(mCustumABProgress, sCustomABLayoutParams);
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
		actionBar.setCustomView(mCustomABRefresh, sCustomABLayoutParams);
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
