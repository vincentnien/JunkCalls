package com.miracolab.junkcalls.json;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracolab.junkcalls.provider.LocalDbHelper;
import com.miracolab.junkcalls.provider.vo.Record;
import com.miracolab.junkcalls.utils.EasyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by vince on 2016/7/10.
 */
public class DataImporter {

    public static Observable<Boolean> importData(Context context) {
        return getRecordList(context)
                .subscribeOn(Schedulers.io())
                .map(records -> {
                    LocalDbHelper.insert(context, records);
                    return records.size()>0;
                });
    }

    private static Observable<List<Record>> getRecordList(Context context) {
        List<Record> data = null;
        InputStream is = null;
        try {
            Type clz = new TypeToken<List<Record>>(){}.getType();
            is = context.getAssets().open("data.json");
            Gson gson = new Gson();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            data = gson.fromJson(reader, clz);
        } catch (IOException e) {
            return Observable.error(e);
        } finally {
            EasyUtil.close(is);
        }
        return Observable.just(data);
    }
}
