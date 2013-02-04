package com.github.ironjan.photodrop.fragments;

import android.database.DataSetObserver;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.github.ironjan.photodrop.OSLibsActivity_;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.StartActivity_;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.github.ironjan.photodrop.model.PostListAdapter;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EFragment
@OptionsMenu({R.menu.dropbox, R.menu.main})
public class StreamFragment extends SherlockListFragment {

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

	@OptionsItem(R.id.mnuDropboxUnlink)
	void unlinkDropbox() {
		sessionKeeper.unlink();
		StartActivity_.intent(getActivity()).start();
	}
	
	@OptionsItem(R.id.mnuAbout)
	void mnuAboutClicked(){
		OSLibsActivity_.intent(getActivity()).start();
	}

	void invalidateList() {
		list.invalidate();
	}

}
