package com.github.ironjan.photodrop.fragments;

import android.database.DataSetObserver;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.github.ironjan.photodrop.OSLibsActivity_;
import com.github.ironjan.photodrop.PrefActivity_;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.github.ironjan.photodrop.model.PostListAdapter;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

import de.keyboardsurfer.android.widget.crouton.Crouton;

@EFragment
@OptionsMenu(R.menu.main)
public class StreamFragment extends SherlockListFragment {
 
	@SuppressWarnings("nls")
	private static final String NYI = "Not yet implemented";

	@Bean
	SessionKeeper sessionKeeper;

	@Bean
	PostListAdapter postLA;

	@ViewById
	ListView list;

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

	@OptionsItem(R.id.mnuPhoto)
	void takePhoto() {
		CroutonW.showInfo(getActivity(), NYI);
	}

	@OptionsItem(R.id.mnuChoose)
	void chooseExistingPicture() {
		CroutonW.showInfo(getActivity(), NYI);
	}

	@OptionsItem(R.id.mnuSettings)
	void mnuSettingsClicked() {
		PrefActivity_.intent(getActivity()).start();
	}
	
	@OptionsItem(R.id.mnuAbout)
	void mnuAboutClicked() {
		OSLibsActivity_.intent(getActivity()).start();
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
