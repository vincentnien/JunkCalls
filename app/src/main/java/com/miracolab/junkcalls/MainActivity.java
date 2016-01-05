package com.miracolab.junkcalls;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.miracolab.junkcalls.utils.SharedPreferencesUtil;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.wifi_only);
        RxCompoundButton
                .checkedChanges(checkBox)
                .throttleWithTimeout(500L, TimeUnit.MILLISECONDS)
                .subscribe(checked -> {
                    SharedPreferencesUtil.set(MainActivity.this, "wifi_only", checked);
                });
    }

}
