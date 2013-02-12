package com.github.ironjan.photodrop.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.helper.ImageOperations;
import com.github.ironjan.photodrop.helper.ImageStorage;
import com.github.ironjan.photodrop.persistence.Post;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

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
		imgPhoto.setImageBitmap(ImageOperations.loadScaledImage(mPost.imagePath, 512, 512));
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

}
