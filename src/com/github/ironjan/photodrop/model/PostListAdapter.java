package com.github.ironjan.photodrop.model;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.github.ironjan.photodrop.helper.ImageOperations;
import com.github.ironjan.photodrop.persistence.Post;
import com.github.ironjan.photodrop.persistence.PostCreator;
import com.github.ironjan.photodrop.views.PostView;
import com.github.ironjan.photodrop.views.PostView_;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostListAdapter extends BaseAdapter {

	private static final String TAG = PostListAdapter.class.getSimpleName();

	@Bean
	DropboxWrapper mDbxWrapper;
	private TreeMap<DbxFileInfo, Post> mPosts = new TreeMap<DbxFileInfo, Post>();

	@RootContext
	Context context;

	@Bean
	PostCreator mPostCreator;

	@Bean
	DropboxWrapper mDbWrapper;

	private Vector<Bitmap> mCachedBms = new Vector<Bitmap>();
	@Bean
	ImageOperations mImOp;

	private Vector<DbxFileInfo> mPosToFi = new Vector<DbxFileInfo>();

	boolean mIsRefreshing = false;

	@AfterInject
	@Background
	public void refresh() {
		if (mIsRefreshing) {
			return;
		}

		mIsRefreshing = true;
		try {
			List<DbxFileInfo> tFiles = mDbxWrapper.listFiles();

			for (DbxFileInfo fi : tFiles) {
				if (isMetaFile(fi)) {
					mPosToFi.add(fi);
					updatePostFromFileInfo(fi);

				}
			}

			notifyDataSetChanged();
		} catch (DbxException e) {
			Log.e(TAG, "Could not load posts", e); //$NON-NLS-1$
		}

		mIsRefreshing = false;
	}

	void updatePostFromFileInfo(DbxFileInfo fi) {
		Post p = mPostCreator.fromDropboxMetaFileInfo(fi);
		if (p != null) {
			mPosts.put(fi, p);
			notifyDataSetChanged();
		}
	}

	@Override
	@UiThread
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private static boolean isMetaFile(DbxFileInfo fi) {
		return fi.path.getName().endsWith(".meta"); //$NON-NLS-1$
	}

	@Override
	public int getCount() {
		return mPosts.size();
	}

	@Override
	public Post getItem(int position) {
		return mPosts.get(mPosToFi.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PostView pv;

		if (convertView == null) {
			pv = PostView_.build(context);
			convertView = pv;
		} else {
			pv = (PostView) convertView;
		}

		pv.bind(getItem(position));

		if (!isCached(position)) {
			ImageView imageView = pv.getImageView();
			loadInBackground(position, imageView);
		}
		pv.setThumb(getCachedBm(position));
		return pv;
	}

	private Bitmap getCachedBm(int position) {
		if (position < 0 || mCachedBms.size() <= position) {
			return null;
		}
		return mCachedBms.get(position);
	}

	private boolean isCached(int position) {
		if (mCachedBms.size() <= position) {
			return false;
		}

		return getCachedBm(position) != null;
	}

	void loadInBackground(int position, ImageView imageView) {
		try {
			mCachedBms.add(position,
					mImOp.loadScaledImage(getItem(position).imagePath));
		} catch (Exception e) {
			// fixme show loading fail
		}
	}

}
