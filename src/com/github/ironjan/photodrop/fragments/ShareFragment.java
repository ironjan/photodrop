package com.github.ironjan.photodrop.fragments;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;

@EFragment(R.layout.frgmt_share)
public class ShareFragment extends SherlockFragment {

	private String mUri;

	@ViewById
	EditText txtComment;
	@ViewById
	TextView txtLocation;
	@ViewById
	ImageButton btnLocation;
	@ViewById
	ImageButton imgPhoto;

	// todo load image etc
	public void setUri(String uri) {
		this.mUri = uri;
		txtLocation.setText(mUri);
	}
}
