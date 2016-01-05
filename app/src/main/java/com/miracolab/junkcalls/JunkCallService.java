package com.miracolab.junkcalls;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.miracolab.junkcalls.parser.JunkCall;
import com.miracolab.junkcalls.parser.JunkCallParser;
import com.miracolab.junkcalls.rx.RxBroadcastReceiver;
import com.miracolab.junkcalls.utils.FloatingWindow;
import com.miracolab.junkcalls.utils.LogUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vincent on 2015/12/16.
 */
public class JunkCallService extends Service {
    private static final String TAG = JunkCallService.class.getSimpleName();

    public static final String ACTION_CALL_STATE = "action.call_state";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_NUMBER = "number";

    private FloatingWindow mWindow;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindow = new FloatingWindow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        mWindow.create(this);

        RxBroadcastReceiver
                .fromBroadcast(LocalBroadcastManager.getInstance(this), new IntentFilter(ACTION_CALL_STATE))
                .map(intentWithContext -> intentWithContext.getIntent().getStringExtra(EXTRA_STATE))
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .subscribe(
                        s -> {
                            if (TelephonyManager.EXTRA_STATE_IDLE.equals(s)) {
                                mWindow.setMissingCall(JunkCallService.this);
                            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(s)) {
                                mWindow.setPickupCall(JunkCallService.this);
                            }
                        },
                        throwable -> {
                        },
                        this::stopSelf
                );

        String number = intent.getStringExtra(EXTRA_NUMBER);
        JunkCallParser
                .queryPhoneNumber(number)
                .distinct(JunkCall::description)
                .map(JunkCall::description)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(junkCalls -> {
                    mWindow.setResult(JunkCallService.this, junkCalls, number);
                }, throwable -> {
                    Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
                });

        return Service.START_NOT_STICKY;
    }

}
