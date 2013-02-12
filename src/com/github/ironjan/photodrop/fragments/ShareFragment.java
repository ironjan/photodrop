package com.github.ironjan.photodrop.fragments;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.helper.ImageStorage;
import com.github.ironjan.photodrop.helper.LocBeanCallback;
import com.github.ironjan.photodrop.helper.LocationBean;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.DrawableRes;

@EFragment(R.layout.frgmt_share)
public class ShareFragment extends SherlockFragment implements LocBeanCallback {

	private String mUri;

	@ViewById
	EditText txtComment;
	@ViewById
	TextView txtLocation;
	@ViewById
	ImageButton btnLocation;
	@ViewById
	ImageView imgPhoto;

	@DrawableRes
	Drawable ic_location_found_dark, ic_location_searching_dark,
			ic_location_off_dark;

	@ViewById(android.R.id.progress)
	ProgressBar progress;

	private boolean mAfterViews;

	@Bean
	ImageStorage mImageStorage;

	@Bean
	LocationBean mLocationBean;

	public void setUri(String uri) {
		this.mUri = uri;
		if (mAfterViews) {
			loadImage();
		}
	}

	@AfterViews
	void loadImage() {
		mAfterViews = true;
		if (mUri != null) {
			imgPhoto.setImageBitmap(mImageStorage.getThumbnail(mUri));
			progress.setVisibility(View.GONE);
		}
	}

	private Location mLocation;

	private boolean mLocationIsAdded;

	@SystemService
	LocationManager locMan;

	@Click
	void btnLocationClicked() {
		if (mLocationIsAdded) {
			removeLocation();
		} else {
			addLocation();
		}
	}

	private void removeLocation() {
		mLocationBean.stopListening();
		btnLocation.setImageDrawable(ic_location_off_dark);
		mLocation = null;
		txtLocation.setText(""); //$NON-NLS-1$
		mLocationIsAdded = false;
	}

	private void addLocation() {
		mLocationBean.startListening(this);
		btnLocation.setImageDrawable(ic_location_searching_dark);
		mLocationIsAdded = true;
	}

	@Override
	public void updateLocation(Location location) {
		if (mLocationIsAdded) {
			mLocation = location;
			btnLocation.setImageDrawable(ic_location_found_dark);
			txtLocation.setText(mLocation.getLatitude() + "," //$NON-NLS-1$
					+ mLocation.getLongitude());
		}
	}

}
