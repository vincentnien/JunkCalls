package com.miracolab.junkcalls.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.miracolab.junkcalls.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractProvider extends ContentProvider {

	private SparseArray<AbstractTable> mTableMap = new SparseArray<AbstractTable>();
	private AtomicInteger mCounter = new AtomicInteger(1);
	
	protected Context mContext;
	private SQLiteDatabase mDatabase;
	
	private static AbstractProvider sInstance;

	private static Map<Class<?>, String> sClassMaps = new HashMap<Class<?>, String>();
	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	protected void addTable(AbstractTable table) {
		int index = mCounter.getAndIncrement();
		table.setActionId(index);
		table.setupColumns();

		mTableMap.append(index, table);
		sClassMaps.put(table.getClass(), table.getTableName());

		sUriMatcher.addURI(getAuthority(), table.getTableName(), index);
	}
	
	public static String getTableName(Class<?> table) {
		if ( sClassMaps.containsKey(table) ) {
			return sClassMaps.get(table);
		}
		return null;
	}


    public static Uri getContentUri(Class<?> table) {
        return getContentUri(getTableName(table));
    }
    
    public static Uri getContentUri(String table) {
        return Uri.withAppendedPath(sInstance.getContentUri(), table);
    }
	
	public SQLiteDatabase getDatabase() {
		return mDatabase;
	}

	protected abstract String getDBName();

	protected abstract int getDBVersion();

	protected abstract String getAuthority();

	protected abstract Uri getContentUri();

	public class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase sqlitedb) {
			LogUtil.d("onCreate");
			for (int index = 0; index < mTableMap.size(); ++index) {
				AbstractTable table = mTableMap.valueAt(index);
				sqlitedb.execSQL(table.createTableSQL());
			}
		}

		@Override
		public void onDowngrade(SQLiteDatabase sqlitedb, int oldVersion,
				int newVersion) {
			
			for (int index = 0; index < mTableMap.size(); ++index) {
				AbstractTable table = mTableMap.valueAt(index);
				sqlitedb.execSQL("DROP TABLE IF EXISTS " + table.getTableName());
			}

			onCreate(sqlitedb);
		}

		
		@Override
		public void onUpgrade(SQLiteDatabase sqlitedb, int oldver, int newver) {
			LogUtil.d("onUpgrade");
			
			for (int index = 0; index < mTableMap.size(); ++index) {
				AbstractTable table = mTableMap.valueAt(index);
				sqlitedb.execSQL("DROP TABLE IF EXISTS " + table.getTableName());
			}

			onCreate(sqlitedb);

		}
	}

	@Override
	public boolean onCreate() {
		mContext = getContext();
		
		sInstance = this;

		DatabaseHelper dbhelper = new DatabaseHelper(mContext, getDBName(),
				null, getDBVersion());
		mDatabase = dbhelper.getWritableDatabase();
		
		return mDatabase != null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int index = sUriMatcher.match(uri);
		try {
			return mTableMap.get(index).delete(uri, selection, selectionArgs);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int index = sUriMatcher.match(uri);
		LogUtil.d("Provider", uri);
		try {
			return mTableMap.get(index).insert(uri, values);
		} catch (NullPointerException e) {
			Log.e("Provider", e.toString());
			return null;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int index = sUriMatcher.match(uri);
		try {
			return mTableMap.get(index).query(uri, projection, selection,
					selectionArgs, sortOrder);
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int index = sUriMatcher.match(uri);
		try {
			return mTableMap.get(index).update(uri, values, selection,
					selectionArgs);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int index = sUriMatcher.match(uri);
		try {
			return mTableMap.get(index).bulkInsert(uri, values);
		} catch (NullPointerException e) {
			return 0;
		}
	}

}
