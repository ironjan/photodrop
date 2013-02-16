package com.github.ironjan.photodrop.model;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.github.ironjan.photodrop.persistence.Post;
import com.github.ironjan.photodrop.persistence.PostCreator;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostListAdapter extends BaseAdapter {

	@Bean
	DropboxWrapper mDbxWrapper;
	private List<Post> mPosts = new Vector<Post>();

	@RootContext
	Context context;

	@Bean
	PostCreator mPostCreator;

	@Bean
	DropboxWrapper mDbWrapper;

	@AfterInject
	public void refresh() {
		try {
			List<DbxFileInfo> tFiles = mDbxWrapper.listFiles();

			mPosts.clear();

			for (DbxFileInfo fi : tFiles) {
				if (isMetaFile(fi)) {
					Post p = mPostCreator.fromDropboxMetaFileInfo(fi);
					if (p != null) {
						mPosts.add(p);
						notifyDataSetChanged();
					}
				}
			}

			notifyDataSetChanged();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public String getItem(int position) {
		return mPosts.get(position).toString();
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

		tv.setText(getItem(position));

		return tv;
	}

}
