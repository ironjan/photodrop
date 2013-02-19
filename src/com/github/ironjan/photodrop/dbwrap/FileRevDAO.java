package com.github.ironjan.photodrop.dbwrap;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class FileRevDAO {

	@SuppressWarnings("nls")
	private static final String FILE_REV_DELETED_WITH_ID = "FileRev deleted with id: ",
			NO_RESULTS = "no results";

	private static final String TAG = FileRevDAO.class.getSimpleName();

	// Database fields
	private SQLiteDatabase database;

	@Bean
	DropboxRevsHelper dbHelper;

	private final String[] allColumns = { DropboxRevsHelper.COLUMN_ID,
			DropboxRevsHelper.COLUMN_FILE, DropboxRevsHelper.COLUMN_REV };

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Log.w("FileRevDAO", "opened database connection: " + database);
	}

	public void close() {
		dbHelper.close();
	}

	public long count() {
		return DatabaseUtils.queryNumEntries(database,
				DropboxRevsHelper.TABLE_DBREVS_NAME);
	}

	public FileRev createFileRev(final String file) {
		long insertId = insertNewFileRev(file);
		FileRev newFileRev = findFileRevById(insertId);
		Log.v(TAG, "Created " + newFileRev); //$NON-NLS-1$
		return newFileRev;
	}

	private long insertNewFileRev(final String file) {
		ContentValues values = new ContentValues();
		values.put(DropboxRevsHelper.COLUMN_FILE, file);
		values.putNull(DropboxRevsHelper.COLUMN_REV);

		long insertId = database.insert(DropboxRevsHelper.TABLE_DBREVS_NAME,
				null, values);

		return insertId;
	}

	private FileRev findFileRevById(long id) {
		Log.w("FileRevDAO", "database is " + database);

		final String table = DropboxRevsHelper.TABLE_DBREVS_NAME;
		final String selection = DropboxRevsHelper.COLUMN_ID + " = " + id;
		Cursor cursor = database.query(table, allColumns, selection, null,
				null, null, null);
		Log.v(TAG,
				"find fileRev by _id = " + id + "; " + cursor.getCount() + " results"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		if (cursor.getCount() == 0) {
			Log.v(TAG, NO_RESULTS);
			cursor.close();
			return null;
		}

		cursor.moveToFirst();
		FileRev fileRev = cursorToFileRev(cursor);
		cursor.close();
		return fileRev;
	}

	public void deleteFileRev(FileRev fileRev) {
		long id = fileRev._id;
		Log.v(TAG, FILE_REV_DELETED_WITH_ID + id);
		database.beginTransaction();
		database.delete(DropboxRevsHelper.TABLE_DBREVS_NAME,
				DropboxRevsHelper.COLUMN_ID + " = " + id, null); //$NON-NLS-1$
		database.endTransaction();
	}

	public void clear() {
		dbHelper.onUpgrade(database, -1, 0);
	}

	public FileRev findOrCreateFileRevByName(String fileName) {
		FileRev fileRev = findFileRevByName(fileName);

		if (fileRev == null) {
			fileRev = createFileRev(fileName);
		}

		return fileRev;
	}

	private FileRev findFileRevByName(final String name) {

		if (database == null) {
			Log.w("FileRevDAO", "database is null");
		}
		final String table = DropboxRevsHelper.TABLE_DBREVS_NAME;
		final String selection = DropboxRevsHelper.COLUMN_FILE + " = ?"; //$NON-NLS-1$
		String[] selectionArgs = { name };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		String limit = null;

		Cursor cursor = database.query(table, allColumns, selection,
				selectionArgs, groupBy, having, orderBy, limit);
		Log.v(TAG,
				"find fileRev by name = " + name + "; " + cursor.getCount() + " results"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		if (cursor.getCount() == 0) {
			Log.v(TAG, NO_RESULTS);
			cursor.close();
			return null;
		}

		cursor.moveToFirst();
		FileRev fileRev = cursorToFileRev(cursor);
		cursor.close();
		return fileRev;
	}

	public Vector<FileRev> findDirtyFileRevs() {
		Cursor cursor = database
				.query(DropboxRevsHelper.TABLE_DBREVS_NAME,
						allColumns,
						DropboxRevsHelper.COLUMN_REV + " IS NULL", null, null, null, null); //$NON-NLS-1$

		Log.v(TAG, "find dirty fileRevs; " + cursor.getCount() + " results"); //$NON-NLS-1$//$NON-NLS-2$

		Vector<FileRev> fileRevs = new Vector<FileRev>();

		while (cursor.moveToNext()) {
			FileRev tmp = cursorToFileRev(cursor);
			fileRevs.add(tmp);
		}

		cursor.close();

		return fileRevs;
	}

	public void update(FileRev fileRev) {
		Log.v(TAG, "update " + fileRev); //$NON-NLS-1$
		ContentValues values = new ContentValues();
		values.put(DropboxRevsHelper.COLUMN_FILE, fileRev.file);
		String[] selectionArgs = { fileRev._id + "" }; //$NON-NLS-1$
		database.beginTransaction();
		database.update(DropboxRevsHelper.TABLE_DBREVS_NAME, values,
				DropboxRevsHelper.COLUMN_ID + " = ?", selectionArgs); //$NON-NLS-1$
		database.endTransaction();
	}

	private static FileRev cursorToFileRev(Cursor cursor) {
		if (cursor.getColumnCount() != 3) {
			Log.w(TAG, "ColumnCount is not 3"); //$NON-NLS-1$
			return null;
		}

		FileRev fileRev = new FileRev();
		fileRev._id = cursor.getLong(0);
		fileRev.file = cursor.getString(1);
		fileRev.rev = cursor.getString(2);

		Log.v(TAG, "found " + fileRev); //$NON-NLS-1$
		return fileRev;
	}
}
