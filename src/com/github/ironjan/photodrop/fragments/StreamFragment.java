package com.github.ironjan.photodrop.fragments;

import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.StartActivity_;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;

@EFragment
@OptionsMenu(R.menu.dropbox)
public class StreamFragment extends SherlockListFragment {
	@Bean
	SessionKeeper sessionKeeper;

	@AfterViews
	void showSomeContent() {
		String[] content = "Stream fragments list is filled with text".split(" "); //$NON-NLS-1$//$NON-NLS-2$
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, content));
	}

	@OptionsItem(R.id.mnuDropboxUnlink)
	void unlinkDropbox() {
		sessionKeeper.unlink();
		StartActivity_.intent(getActivity()).start();
	}
}
