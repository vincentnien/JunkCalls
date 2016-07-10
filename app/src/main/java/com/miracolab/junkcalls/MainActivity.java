package com.miracolab.junkcalls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.miracolab.junkcalls.json.DataImporter;
import com.miracolab.junkcalls.provider.LocalDbHelper;
import com.miracolab.junkcalls.provider.vo.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.wifi_only);
        boolean check = getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("wifi_only", true);
        checkBox.setChecked(check);

        RxCompoundButton
                .checkedChanges(checkBox)
                .throttleWithTimeout(500L, TimeUnit.MILLISECONDS)
                .subscribe(checked -> {
                    getSharedPreferences("settings", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("wifi_only", checked)
                            .apply();
                });

        boolean first = getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("first", true);
        if(first) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage(R.string.add_junkcalls)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        addDefaultData();
                    })
                    .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
                    })
                    .create()
                    .show();
        }
    }

    private void addDefaultData() {
        DataImporter
                .importData(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    if(success) {
                        getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("first", false).apply();
                    }
                    Toast.makeText(MainActivity.this, "success="+success, Toast.LENGTH_SHORT).show();
                });
    }
}
