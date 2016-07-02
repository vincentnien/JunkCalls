package com.miracolab.junkcalls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

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
    }

}
