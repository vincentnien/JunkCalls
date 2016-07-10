package com.miracolab.junkcalls.provider;

import android.net.Uri;

import com.miracolab.junkcalls.provider.table.TableRecord;
import com.miracolab.junkcalls.utils.LogUtil;

public class JunkcallProvider extends AbstractProvider {
    private static final String DB_NAME = "junkcall.db";
    private static final int DB_VERSION = 1;

    public static final String AUTHORITY = "com.miracolab.junkcalls.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    @Override
    public boolean onCreate() {
        LogUtil.d("Provider", "onCreate");
        addTable(new TableRecord(this));
        return super.onCreate();
    }

    @Override
    protected String getDBName() {
        return DB_NAME;
    }

    @Override
    protected int getDBVersion() {
        return DB_VERSION;
    }

    @Override
    protected String getAuthority() {
        return AUTHORITY;
    }

    @Override
    protected Uri getContentUri() {
        return CONTENT_URI;
    }
}
