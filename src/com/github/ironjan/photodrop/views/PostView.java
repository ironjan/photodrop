package com.github.ironjan.photodrop.views;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import com.github.ironjan.photodrop.*;
import com.github.ironjan.photodrop.helper.*;
import com.github.ironjan.photodrop.persistence.*;
import com.googlecode.androidannotations.annotations.*;

@EViewGroup(R.layout.view_post_in_stream)
public class PostView extends RelativeLayout {

	@ViewById
	ImageView imgPhoto;

	@ViewById
	TextView txtComment;

	@ViewById(android.R.id.progress)
	ProgressBar progress;

	@Bean
	ImageStorage mImageStorage;

	private Bitmap mThumbnail;

	private Post mPost;

	public PostView(Context context) {
		super(context);
	}

	public void bind(Post p) {
		this.mPost = p;
		txtComment.setText(p.metadata.comment);
		loadThumb();
	}

	void loadThumb() {
		imgPhoto.setImageBitmap(ImageOperations.loadScaledImage(
				mPost.imagePath, 512, 512));
		progress.setVisibility(View.GONE);
	}

	public ImageView getImageView() {
		return imgPhoto;
	}

	@UiThread
	public void setThumb(Bitmap bitmap) {
		this.mThumbnail = bitmap;
		imgPhoto.setImageBitmap(mThumbnail);
		progress.setVisibility(View.GONE);
	}

	@Click({ R.id.imgPhoto, R.id.txtComment })
	void click() {
		SinglePostActivity_.intent(getContext()).imagePath(mPost.imagePath)
				.start();
	}
}
