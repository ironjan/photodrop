package com.github.ironjan.photodrop.fragments;

import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.github.ironjan.photodrop.*;
import com.github.ironjan.photodrop.helper.*;
import com.github.ironjan.photodrop.persistence.*;
import com.googlecode.androidannotations.annotations.*;

@EFragment(R.layout.frgmt_single_post)
public class SinglePostFragment extends SherlockFragment {

	@ViewById
	ImageView imgPhoto;

	@ViewById
	TextView txtDescription;

	@ViewById
	Button btnLocation;

	@SystemService
	WindowManager wm;

	@Bean
	PostCreator pc;

	private Post mPost;

	@AfterViews
	void bindPost() {
		Bundle arguments = getArguments();
		if (arguments != null) {
			String imagePath = (String) arguments
					.get(SinglePostActivity.EXTRA_IMAGE_PATH);
			this.mPost = pc.fromMetadataFile(imagePath + ".meta");

			Log.v("..", "width: " + imgPhoto.getWidth());

			final int maxImageSide = Math.min(2048, wm.getDefaultDisplay()
					.getWidth());

			imgPhoto.setImageBitmap(ImageOperations.loadScaledImage(
					mPost.imagePath, maxImageSide, maxImageSide));
			txtDescription.setText(mPost.metadata.comment);

			showLocation(mPost);

		}
	}

	void showLocation(Post p) {
		if (p.metadata.latitude != null && p.metadata.longitude != null) {
			btnLocation.setText(p.metadata.latitude
					+ ", " + p.metadata.longitude); //$NON-NLS-1$
			btnLocation.setVisibility(View.VISIBLE);
		}
	}

	@Click(R.id.btnLocation)
	void showOnMap() {
		PostMetadata metadata = mPost.metadata;
		final String uriString = "geo:" //$NON-NLS-1$
				+ metadata.latitude + "," + metadata.longitude; //$NON-NLS-1$
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
		getActivity().startActivity(intent);
	}
}
