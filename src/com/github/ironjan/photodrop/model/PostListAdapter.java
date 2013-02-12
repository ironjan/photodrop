package com.github.ironjan.photodrop.model;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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

@EBean
public class PostListAdapter extends BaseAdapter implements DirObserverCallback {
	private static final String TAG = PostListAdapter.class.getSimpleName();

	@RootContext
	Context context;

	@Bean
	DirObserver mDirObserver;

	@Bean
	DirKeeper mDirKeeper;

	@Bean
	PostCreator mPostCreator;
	private SortedMap<String, Post> mPosts = new TreeMap<String, Post>();

	@AfterInject
	@Background
	void print() {
		mDirObserver.setCallback(this);
		loadDirContent();
		mDirObserver.startWatching();
	}

	private void loadDirContent() {
		File filesDir = mDirKeeper.getExtFilesDir();

		File[] files = filesDir.listFiles();

		for (File file : files) {
			updatePost(file.getPath());
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mPosts.size();
	}

	@Override
	public Post getItem(int position) {
		final Object key = mPosts.keySet().toArray()[position];
		return mPosts.get(key);
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
		} else {
			pv = (PostView) convertView;
		}

		Post p = getItem(position);
		pv.bind(p);
		return pv;
	}

	@SuppressWarnings("static-method")
	@UiThread
	void imageLoaded(ImageView imageView, final Bitmap bm) {
		imageView.setImageBitmap(bm);
	}

	@Override
	public void fileChanged(String path) {
		String tPath = path;
		if (!tPath.endsWith(".meta")) { //$NON-NLS-1$
			tPath = tPath.concat(".meta");//$NON-NLS-1$
		}
		updatePost(tPath);
	}

	void updatePost(String tPath) {
		Post p = mPostCreator.fromMetadataFile(tPath);

		if (p != null) {
			mPosts.remove(tPath);
			mPosts.put(tPath, p);
			notifyDataSetChanged();
		}
	}

	@Override
	public void fileDeleted(String path) {
		Log.v(TAG, String.format(
				"%s has been deleted. Removed from displayed posts", path));
		String tPath = path;
		if (!tPath.endsWith(".meta")) {//$NON-NLS-1$
			tPath = tPath.concat(".meta");//$NON-NLS-1$
		}
		mPosts.remove(tPath);
		notifyDataSetChanged();
	}

	@Override
	@UiThread
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
