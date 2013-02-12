package com.github.ironjan.photodrop.dbwrap;

import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class DropboxRevsHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String TAG = DropboxRevsHelper.class.getSimpleName();

	@SuppressWarnings("nls")
	public static final String TABLE_DBREVS_NAME = "dropboxRevs",
			COLUMN_ID = "_id", COLUMN_FILE = "file", COLUMN_REV = "rev";

	@SuppressWarnings("nls")
	private static final String DATABASE_NAME = "dropboxRevsDB",
			UPGRADE_MESSAGE_BASE = "Upgrading database from version %d  to %d, which will destroy all old data",
			DROP_TABLES = "DROP TABLE IF EXISTS " + TABLE_DBREVS_NAME,
			tableDbRevsCreate = "CREATE TABLE " + TABLE_DBREVS_NAME + " ("
					+ COLUMN_ID + " integer primary key autoincrement, "
					+ COLUMN_FILE + " text not null, " + COLUMN_REV + " text);";

	DropboxRevsHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(tableDbRevsCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String upgradeMessage = String.format(Locale.GERMAN,
				UPGRADE_MESSAGE_BASE, Integer.valueOf(oldVersion),
				Integer.valueOf(newVersion));
		Log.w(TAG, upgradeMessage);

		db.execSQL(DROP_TABLES);
		onCreate(db);
	}

}
