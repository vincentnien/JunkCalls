package com.miracolab.junkcalls.provider.vo;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.miracolab.junkcalls.provider.table.TableRecord;

/**
 * Created by vince on 2016/7/2.
 */
public class Record {

    public String number;
    public String description;
    public int report;


    private static int idxNumber = -1;
    private static int idxDescription;
    private static int idxReport;

    public Record(String n, String d, int r) {
        number = n;
        description = d;
        report = r;
    }

    public static Record empty() {
        return new Record(null, null, 0);
    }

    public static Record fromCursor(Cursor cursor) {
        if(cursor == null) {
            return empty();
        }
        if(idxNumber == -1) {
            idxNumber = cursor.getColumnIndex(TableRecord.Columns.NUMBER);
            idxDescription = cursor.getColumnIndex(TableRecord.Columns.DESCRIPTION);
            idxReport = cursor.getColumnIndex(TableRecord.Columns.REPORT);
        }
        String number = cursor.getString(idxNumber);
        String description = cursor.getString(idxDescription);
        int report = cursor.getInt(idxReport);
        return new Record(number, description, report);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(number);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableRecord.Columns.NUMBER, number);
        values.put(TableRecord.Columns.DESCRIPTION, description);
        values.put(TableRecord.Columns.REPORT, report);
        return values;
    }
}
