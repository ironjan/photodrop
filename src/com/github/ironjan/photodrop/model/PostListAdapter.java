package com.github.ironjan.photodrop.model;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

@EBean
public class PostListAdapter extends BaseAdapter implements DirObserverCallback {
	private static final Integer ZERO = Integer.valueOf(0);
	HashMap<String, Integer> changesHM = new HashMap<String, Integer>();

	@RootContext
	Context context;

	@Bean
	DirObserver mDirObserver;

	@Bean
	DirKeeper mDirKeeper;

	@AfterInject
	@Background
	void print() {
		mDirObserver.setCallback(this);
		loadDirContent();
		mDirObserver.startWatching();
	}

	private void loadDirContent() {
		File filesDir = mDirKeeper.getExtFilesDir();
		String[] files = filesDir.list();

		for (String file : files) {
			changesHM.put(file, ZERO);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return changesHM.size();
	}

	@Override
	public String getItem(int position) {
		final Object key = changesHM.keySet().toArray()[position];
		return String.format("%s : %s", key, changesHM.get(key)); //$NON-NLS-1$
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
		Integer changes = changesHM.get(path);

		if (changes == null) {
			changes = ZERO;
		} else {
			changes = Integer.valueOf(changes.intValue() + 1);
		}
		changesHM.put(path, changes);
		notifyDataSetChanged();
	}

	@Override
	public void fileDeleted(String path) {
		changesHM.remove(path);
		notifyDataSetChanged();
	}

	@Override
	@UiThread
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
}
