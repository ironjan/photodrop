package com.github.ironjan.photodrop.helper;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.api.Scope;

@EBean(scope = Scope.Singleton)
public class ConnectionBean {

	@SystemService
	ConnectivityManager cm;

	public boolean hasInternetConnection() {
		return (cm.getActiveNetworkInfo() != null);
	}

	public boolean hasWifiOrEthernet() {
		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info == null) {
			return false;
		}

		return info.getType() == ConnectivityManager.TYPE_WIFI
				|| info.getType() == ConnectivityManager.TYPE_ETHERNET;
	}
}
