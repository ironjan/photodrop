package com.github.ironjan.photodrop.fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.helper.ImageStorage;
import com.github.ironjan.photodrop.helper.LocBeanCallback;
import com.github.ironjan.photodrop.helper.LocationBean;
import com.github.ironjan.photodrop.model.PostSharer;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.DrawableRes;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EFragment(R.layout.frgmt_share)
@OptionsMenu(R.menu.share)
public class ShareFragment extends SherlockFragment implements LocBeanCallback {

	private static final String TAG = ShareFragment.class.getSimpleName();

	private Uri mUri;

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

	@StringRes
	String nyi, noCommentGiven;

	@ViewById(android.R.id.progress)
	ProgressBar progress;

	private boolean mAfterViews;

	@Bean
	ImageStorage mImageStorage;

	@Bean
	LocationBean mLocationBean;

	@Bean
	PostSharer mSharer;

	private String mUriString;

	private Activity mActivity;

	@AfterInject
	void loadActivity() {
		this.mActivity = getActivity();
	}

	public void setUri(String uri) {
		this.mUri = Uri.parse(uri);
		this.mUriString = uri;
		Log.v(TAG, String.format("Starting share of %s", mUri)); //$NON-NLS-1$
		if (mAfterViews) {
			loadImage();
		}
	}

	@AfterViews
	void loadImage() {
		mAfterViews = true;
		if (mUri != null) {
			imgPhoto.setImageBitmap(mImageStorage.getThumbnail(mUriString));
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

	@Override
	public void onPause() {
		mLocationBean.stopListening();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		if(mLocationIsAdded){
			mLocationBean.startListening(this);
		}
		super.onResume();
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

	@OptionsItem(R.id.mnuCancel)
	void cancel() {
		nyi();
		// todo clean up
	}

	@OptionsItem(R.id.mnuShare)
	void share() {
		String comment = txtComment.getText().toString();
		if(comment.equals("")){ //$NON-NLS-1$
			comment = noCommentGiven;
		}
		mSharer.share(mUri, mLocation, comment);
		mActivity.setResult(Activity.RESULT_OK);
		mActivity.finish();
	}

	private void nyi() {
		CroutonW.showInfo(getSherlockActivity(), nyi);
	}

}
