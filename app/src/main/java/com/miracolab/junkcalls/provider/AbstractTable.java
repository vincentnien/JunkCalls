package com.miracolab.junkcalls.provider;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.miracolab.junkcalls.utils.LogUtil;

public abstract class AbstractTable {

	private static final String TAG = AbstractTable.class.getSimpleName();
	private int actionId = -1;
	private WeakReference<AbstractProvider> provider;
	private Map<String, String> columns = new HashMap<String, String>();

	public AbstractTable(AbstractProvider provider) {
		this.provider = new WeakReference<AbstractProvider>(provider);
	}

	protected void addColumn(String column, String format) {
		columns.put(column, format);
	}

	public void setActionId(int id) {
		actionId = id;
	}

	public int getActionId() {
		return actionId;
	}

	public String createTableSQL() {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(getTableName()).append("(");
		int counter = 0;
		int size = columns.size();
		for (String key : columns.keySet()) {
			sb.append(key).append(" ").append(columns.get(key));
			// if not last one, append ,
			if (++counter < size) {
				sb.append(",");
			} else {
				sb.append(")");
			}
		}
		return sb.toString();
	}

	public abstract void setupColumns();

	public abstract String getTableName();

	public String getPath() {
		return "/" + getTableName();
	}

	public Uri getContentUri() {
		return Uri.withAppendedPath(provider.get().getContentUri(), getPath());
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return provider.get().getDatabase()
				.delete(getTableName(), selection, selectionArgs);
	}

	public Uri insert(Uri uri, ContentValues values) {
		AbstractProvider ap = provider.get();
		long rowId = ap.getDatabase().replace(getTableName(), null, values);
		if (rowId >= 0) {
			ap.getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + uri + " id="
				+ rowId);
	}

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return provider
				.get()
				.getDatabase()
				.query(getTableName(), projection, selection, selectionArgs,
						null, null, sortOrder, null);
	}

	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		String tableName = getTableName();
		AbstractProvider ap = provider.get();
		SQLiteDatabase db = ap.getDatabase();
		count = db.update(tableName, values, selection, selectionArgs);
		Uri notifyUri = null;
		if (count > 0) {
			notifyUri = uri;
		} else {
			long rowId = db.replace(tableName, null, values);
			if (rowId >= 0) {
				ap.getContext().getContentResolver().notifyChange(uri, null);
				return 1;
			}
		}
		if (count > 0) {
			ap.getContext().getContentResolver().notifyChange(notifyUri, null);
		}
		return count;
	}

	public int bulkInsert(Uri uri, ContentValues[] values) {
		AbstractProvider ap = provider.get();
		SQLiteDatabase db = ap.getDatabase();
		int rowId = 0;
		try {
			db.beginTransaction();
			for (ContentValues value : values) {
				db.replace(getTableName(), null, value);
				rowId++;
			}
			db.setTransactionSuccessful();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
		}
		if (rowId >= 0) {
			ap.getContext().getContentResolver().notifyChange(uri, null);
		}
		LogUtil.d(TAG, "bulkInsert=", rowId);
		return rowId;
	}
}
