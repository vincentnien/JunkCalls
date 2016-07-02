package com.miracolab.junkcalls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.miracolab.junkcalls.utils.NetworkUtil;

/**
 * Created by vincent on 2015/12/8.
 */
public class PhoneCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isWifiOnly = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("wifi_only", true);
        if(NetworkUtil.isNetworkConnected(context, isWifiOnly)) {
            handleIncomingCall(context, intent);
        }
    }

    private void handleIncomingCall(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr)) {
            Intent service = new Intent(context, JunkCallService.class);
            service.putExtra(JunkCallService.EXTRA_STATE, stateStr);
            service.putExtra(JunkCallService.EXTRA_NUMBER, number);

            context.startService(service);
        } else {
            Intent broadcast = new Intent(JunkCallService.ACTION_CALL_STATE);
            broadcast.putExtra(JunkCallService.EXTRA_STATE, stateStr);
            LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
        }
    }

}
