package com.github.ironjan.photodrop.dbwrap;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.github.ironjan.photodrop.helper.ConnectionBean;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class SessionKeeper {
	private static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private static final String TAG = SessionKeeper.class.getSimpleName();

	@StringRes
	String appKey, appSecret, authentificationNoInternet,
			authentificationError;

	@Pref
	DBKeys_ dbKeys;

	@Bean
	ConnectionBean connectionBean;

	@AfterInject
	void createMDBApi() {
		AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);

		AndroidAuthSession session = new AndroidAuthSession(appKeys,
				ACCESS_TYPE, getAccessTokenPair());

		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
	}

	private DropboxAPI<AndroidAuthSession> mDBApi;

	public void startAuthentication(Context context) {
		if (connectionBean.hasInternetConnection()) {
			getSession().startAuthentication(context);
		} else {
			throw new IllegalStateException(authentificationNoInternet);
		}
	}

	public void finishAuthentication() throws IllegalStateException {
		if(getSession().isLinked()){
			return;
		}
		if (getSession().authenticationSuccessful()) {
			getSession().finishAuthentication();

			AccessTokenPair tokens = getSession().getAccessTokenPair();

			// Provide your own storeKeys to persist the access token pair
			// A typical way to store tokens is using SharedPreferences
			storeKeys(tokens.key, tokens.secret);
		} else {
			throw new IllegalStateException(authentificationError);
		}
	}

	public void unlink() {
		getSession().unlink();
		dbKeys.clear();
	}

	private void storeKeys(String key, String secret) {
		Log.v(TAG, "Saving access tokens."); //$NON-NLS-1$
		dbKeys.edit().accessKey().put(key).accessSecret().put(secret).apply();
	}

	private AndroidAuthSession getSession() {
		return mDBApi.getSession();
	}

	private AccessTokenPair getAccessTokenPair() {
		if (dbKeys.accessKey().exists() && dbKeys.accessSecret().exists()) {
			Log.v(TAG, "Using saved access tokens."); //$NON-NLS-1$
			String accessKey = dbKeys.accessKey().get();
			String accessSecret = dbKeys.accessSecret().get();
			AccessTokenPair atp = new AccessTokenPair(accessKey, accessSecret);
			return atp;
		}

		Log.v(TAG, "Access tokens not yet saved."); //$NON-NLS-1$
		return null;
	}

	public boolean isLinked() {
		return getSession().isLinked();
	}

}
