package com.github.ironjan.photodrop.crouton;

import android.app.Activity;
import android.util.Log;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class CroutonW {

	public static void showAlert(Activity activity, String message){
		Crouton.makeText(activity, message, Style.ALERT).show();
	}

	public static void showConfirm(Activity activity, String message) {
		Crouton.makeText(activity, message, Style.CONFIRM).show();
	}

	public static void showAlert(Activity activity, Exception e) {
		final String message = e.getMessage();
		showAlert(activity, message);
		Log.e(activity.getClass().getSimpleName(), message, e);
	}

	public static void showInfo(Activity activity,
			String message) {
		Crouton.makeText(activity, message, Style.INFO).show();
	}
}
