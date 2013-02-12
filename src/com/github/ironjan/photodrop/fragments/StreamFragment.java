package com.github.ironjan.photodrop.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.github.ironjan.photodrop.OSLibsActivity_;
import com.github.ironjan.photodrop.PrefActivity_;
import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.ShareActivity_;
import com.github.ironjan.photodrop.crouton.CroutonW;
import com.github.ironjan.photodrop.dbwrap.SessionKeeper;
import com.github.ironjan.photodrop.model.DirKeeper;
import com.github.ironjan.photodrop.model.PostListAdapter;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

import de.keyboardsurfer.android.widget.crouton.Crouton;

@EFragment
@OptionsMenu(R.menu.main)
public class StreamFragment extends SherlockListFragment {

	@SuppressWarnings("nls")
	private static final String sImageContentType = "image/*";

	private static final int TAKE_REQUEST_CODE = 0;

	private static final int CHOOSE_REQUEST_CODE = 1;

	private static final int SHARE_PHOTO_REQUEST = 2;

	private static final String TAG = StreamFragment.class.getSimpleName();


	@Bean
	SessionKeeper sessionKeeper;

	@StringRes
	String noPhotoRightNow;
	
	@Bean
	PostListAdapter postLA;

	@ViewById
	ListView list;
	@Bean
	DirKeeper mDirKeeper;

	private Uri mUri;

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
		// todo grey out wihtout camera
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mUri = Uri.fromFile(mDirKeeper.createNewPhotofile());
		Log.w(TAG, String.format("%s", mUri)); //$NON-NLS-1$
		if (mUri != null) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
			startActivityForResult(intent, TAKE_REQUEST_CODE);
		} else {
			CroutonW.showAlert(getSherlockActivity(),
					noPhotoRightNow); 
		}
	}

	@OptionsItem(R.id.mnuChoose)
	void chooseExistingPicture() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(sImageContentType);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, CHOOSE_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOOSE_REQUEST_CODE:
			resultChooseExisting(resultCode, data);
			break;
		case TAKE_REQUEST_CODE:
			resultTakePhoto(resultCode);
			break;
		default:
			break;
		}
	}

	private void resultChooseExisting(int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			mUri = extractChooseExistingUri(data);
			copyImageToExtDir(mUri);
			sharePhoto();
		}
	}

	@Background 
	void copyImageToExtDir(Uri uri) {
		// todo implement
	}

	private static Uri extractChooseExistingUri(Intent data) {
		Uri selectedImage = null;
		if (data != null && data.getData() != null) {
			selectedImage = data.getData();
		}

		return selectedImage;
	}
	
	private void resultTakePhoto(int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			sharePhoto();
		}
	}

	/**
	 * This method creates an intent with uri-extra <code>mUri</code> to
	 * NewPostActivity.
	 */
	private void sharePhoto() {
		ShareActivity_.intent(getSherlockActivity()).photoUri(String.format("%s",  mUri)) //$NON-NLS-1$
				.startForResult(SHARE_PHOTO_REQUEST);
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
