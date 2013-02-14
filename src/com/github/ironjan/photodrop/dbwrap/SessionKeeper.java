package com.github.ironjan.photodrop.dbwrap;

import android.app.Activity;
import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileSystem;
import com.github.ironjan.photodrop.StartActivity;
import com.github.ironjan.photodrop.helper.ConnectionBean;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class SessionKeeper {
	private DbxAccountManager mDbxAcctMgr;

	@StringRes
	String appKey, appSecret, authentificationNoInternet,
			authentificationError;

	@Pref
	DBKeys_ dbKeys;

	@Bean
	ConnectionBean connectionBean;

	@RootContext
	Context context;

	private DbxFileSystem mDbxFs;

	@AfterInject
	void createMDBApi() {
		mDbxAcctMgr = DbxAccountManager.getInstance(context, appKey, appSecret);
	}

	public void startAuthentication(Activity activity) {
		if (connectionBean.hasInternetConnection()) {
			mDbxAcctMgr.startLink(activity, StartActivity.REQUEST_LINK_TO_DBX);
		} else {
			throw new IllegalStateException(authentificationNoInternet);
		}
	}

	/**
	 * Unlinks the user.
	 */
	public void unlink() {
		mDbxAcctMgr.unlink();
	}

	public boolean isLinked() {
		return mDbxAcctMgr.hasLinkedAccount();
	}

	public DbxFileSystem getDropboxFilesystem() throws Unauthorized {
		if (mDbxAcctMgr.hasLinkedAccount()) {
			this.mDbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());
		}
		return mDbxFs;
	}
}
