package com.miracolab.junkcalls.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.miracolab.junkcalls.provider.table.TableRecord;
import com.miracolab.junkcalls.provider.vo.Record;
import com.miracolab.junkcalls.utils.LogUtil;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class LocalDbHelper {

    public static int getRecordCount(Context context) {
        Cursor cursor = context.getContentResolver().query(JunkcallProvider.getContentUri(TableRecord.class), null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            try {
                return cursor.getCount();
            } finally {
                cursor.close();
            }
        }
        return 0;
    }

    public static Observable<Record> getRecord(Context context, String number) {
        return Observable.just(number)
                .map(s -> {
                    ContentResolver resolver = context.getContentResolver();
                    Cursor cursor = resolver.query(JunkcallProvider.getContentUri(TableRecord.class), null,
                            TableRecord.Columns.NUMBER + "=?",
                            new String[]{s}, null);
                    if(cursor != null && cursor.moveToFirst()) {
                        try {
                            return Record.fromCursor(cursor);
                        } finally {
                            cursor.close();
                        }
                    }
                    return Record.empty();
                })
                .subscribeOn(Schedulers.io());
    }

    public static void insert(Context context, List<Record> data) {
        ContentResolver resolver = context.getContentResolver();

        int size = data.size();
        ContentValues[] values = new ContentValues[size];
        for(int i=0; i<size; ++i) {
            Record record = data.get(i);
            values[i] = record.toContentValues();
        }
        resolver.bulkInsert(JunkcallProvider.getContentUri(TableRecord.class), values);
    }
}
