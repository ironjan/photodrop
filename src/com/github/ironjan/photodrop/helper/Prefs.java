package com.github.ironjan.photodrop.helper;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultInt;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface Prefs {
	
	@DefaultInt(Constants.HOUR)
	public int syncIntervall();

	@DefaultBoolean(true)
	public boolean automaticSync();
	
	@DefaultBoolean(true)
	public boolean syncOnlyOnWifi();
	
	@DefaultString("")
	public String folderHash();
}
