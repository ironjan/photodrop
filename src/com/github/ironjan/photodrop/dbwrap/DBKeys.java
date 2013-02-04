package com.github.ironjan.photodrop.dbwrap;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface DBKeys {

	public String accessKey();
	public String accessSecret();
}
