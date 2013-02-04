package com.github.ironjan.photodrop.fragments;

import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.StartActivity_;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FromHtml;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EFragment(R.layout.frgmt_os_libs)
@OptionsMenu(R.menu.dropbox)
public class OSLibsFragment extends SherlockFragment {

	@ViewById(R.id.libNameAA)
	@FromHtml(R.string.lib_name_aa)
	TextView libNameAA;

	@ViewById(R.id.libLinkAA)
	@FromHtml(R.string.lib_link_aa)
	TextView libLinkAA;

	@ViewById(R.id.libNoteAA)
	@FromHtml(R.string.lib_note_aa)
	TextView libNoteAA;

	@ViewById(R.id.libNameABS)
	@FromHtml(R.string.lib_name_abs)
	TextView libNameABS;

	@ViewById(R.id.libLinkABS)
	@FromHtml(R.string.lib_link_abs)
	TextView libLinkABS;

	@ViewById(R.id.libNoteABS)
	@FromHtml(R.string.lib_note_abs)
	TextView libNoteABS;

	@ViewById(R.id.libNameCrouton)
	@FromHtml(R.string.lib_name_crouton)
	TextView libNameCrouton;

	@ViewById(R.id.libLinkCrouton)
	@FromHtml(R.string.lib_link_crouton)
	TextView libLinkCrouton;

	@ViewById(R.id.libNoteCrouton)
	@FromHtml(R.string.lib_note_crouton)
	TextView libNoteCrouton;

	@ViewById(R.id.libNameHE)
	@FromHtml(R.string.lib_name_he)
	TextView libNameHE;

	@ViewById(R.id.libLinkHE)
	@FromHtml(R.string.lib_link_he)
	TextView libLinkHE;

	@ViewById(R.id.libNoteHE)
	@FromHtml(R.string.lib_note_he)
	TextView libNoteHE;

	@ViewById(R.id.libNameJson)
	@FromHtml(R.string.lib_name_json)
	TextView libNameJson;

	@ViewById(R.id.libLinkJson)
	@FromHtml(R.string.lib_link_json)
	TextView libLinkJson;

	@ViewById(R.id.libNoteJson)
	@FromHtml(R.string.lib_note_json)
	TextView libNoteJson;

	@ViewById(R.id.libNameDevGuide)
	@FromHtml(R.string.lib_name_devGuide)
	TextView libNameDevGuide;

	@ViewById(R.id.libLinkDevGuide)
	@FromHtml(R.string.lib_link_devGuide)
	TextView libLinkDevGuide;

	@ViewById(R.id.libNoteDevGuide)
	@FromHtml(R.string.lib_note_devGuide)
	TextView libNoteDevGuide;

	@ViewById(R.id.libNameDropbox)
	@FromHtml(R.string.lib_name_dbSDK)
	TextView libNameDropbox;

	@ViewById(R.id.libLinkDropbox)
	@FromHtml(R.string.lib_link_dbSDK)
	TextView libLinkDropbox;

	@ViewById(R.id.libNoteDropbox)
	@FromHtml(R.string.lib_note_dbSDK)
	TextView libNoteDropbox;

	@Bean
	SessionKeeper sessionKeeper;

	@OptionsItem(R.id.mnuDropboxUnlink)
	void unlinkDropbox() {
		sessionKeeper.unlink();
		StartActivity_.intent(getActivity()).start();
	}

}
