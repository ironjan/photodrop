package com.github.ironjan.photodrop.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ironjan.photodrop.R;
import com.github.ironjan.photodrop.persistence.Post;
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

	private Bitmap mThumbnail;

	public PostView(Context context) {
		super(context);
	}

	public void bind(Post p) {
		txtComment.setText(p.metadata.comment);
		loadThumb();
	}

	void loadThumb() {
		// fixme load image
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
