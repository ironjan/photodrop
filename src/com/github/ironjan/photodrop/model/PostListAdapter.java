package com.github.ironjan.photodrop.model;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.github.ironjan.photodrop.dbwrap.DropboxWrapper;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class PostListAdapter extends BaseAdapter  {
	
	@Bean
	DropboxWrapper mDbxWrapper;
	private List<DbxFileInfo> mFiles;

	@RootContext
	Context context;
	
	@AfterInject
	public void refresh() {
			try {
				this.mFiles = mDbxWrapper.listFiles();
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Override
	public int getCount() {
		return mFiles.size();
	}

	@Override
	public String getItem(int position) {
		return mFiles.get(position).path.getName();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		
		if(convertView == null){
			tv = new TextView(context);
			convertView = tv;
		}
		else{
			tv = (TextView) convertView;
		}
		
		tv.setText(getItem(position));
		
		return tv;
	}

}
