package com.github.ironjan.photodrop;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity
public class StartActivity extends SherlockActivity{
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	TextView tv = new TextView(this);
	tv.setText("Photodrop started"); //$NON-NLS-1$
	setContentView(tv);
}
}
