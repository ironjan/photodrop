package com.github.ironjan.photodrop.model;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.ironjan.photodrop.persistence.Post;
import com.github.ironjan.photodrop.persistence.PostCreator;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

@EBean
public class PostListAdapter extends BaseAdapter implements DirObserverCallback {
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
			addPathToPosts(file.getPath());
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mPosts.size();
	}

	@Override
	public String getItem(int position) {
		final Object key = mPosts.keySet().toArray()[position];
		return String.format("%s : %s", key, mPosts.get(key)); //$NON-NLS-1$
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;

		if (convertView == null) {
			tv = new TextView(context);
			convertView = tv;
		} else {
			tv = (TextView) convertView;
		}
		tv.setText(getItem(position).toString());
		return tv;
	}

	@Override
	public void fileChanged(String path) {
		String tPath = path;
		if (!tPath.endsWith(".meta")) { //$NON-NLS-1$
			tPath = tPath.concat(".meta");//$NON-NLS-1$
		}
		addPathToPosts(tPath);
	}

	void addPathToPosts(String tPath) {
		Post p = mPostCreator.fromMetadataFile(tPath);

		if (p != null) {
			mPosts.put(tPath, p);
			notifyDataSetChanged();
		}
	}

	@Override
	public void fileDeleted(String path) {
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
