import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.JunkCallService;
import com.miracolab.junkcalls.PhoneCallReceiver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.shadows.support.v4.Shadows.shadowOf;


/**
 * Created by vincent on 2016/1/4.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PhoneCallReceiverTest {

    private void setUpConnection(int type, boolean isConnected) {
        ConnectivityManager mgr = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager smgr = Shadows.shadowOf(mgr);
        setActiveNetworkState(smgr, type, isConnected);
    }

    private void setActiveNetworkState(ShadowConnectivityManager smgr, int type, boolean connected) {
        smgr.setActiveNetworkInfo(
                ShadowNetworkInfo.newInstance(
                        NetworkInfo.DetailedState.CONNECTED,
                        type,
                        0,
                        true,
                        connected)
        );
    }

    @Test
    public void testReceiverRegistered() {
        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();

        assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound) {
                receiverFound = PhoneCallReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
            }
        }

        assertTrue(receiverFound); //will be false if not found
    }

    private Intent getPhoneCallIntent(String state) {
        Bundle bundle1 = new Bundle();
        bundle1.putString(TelephonyManager.EXTRA_STATE, state);
        bundle1.putString(TelephonyManager.EXTRA_INCOMING_NUMBER, "0976927469");//"0976927469"

        Intent intent = new Intent();
        intent.setAction("android.intent.action.PHONE_STATE");
        intent.putExtras(bundle1);

        return intent;
    }


    @Test
    public void testOnReceiveRingingNoConnection() {
        setUpConnection(ConnectivityManager.TYPE_WIFI, false);

        /** TEST 1
         ----------
         We defined the Broadcast receiver with a certain action, so we should check if we have
         receivers listening to the defined action
         */
        Intent intent = getPhoneCallIntent(TelephonyManager.EXTRA_STATE_RINGING);

        ShadowApplication app = ShadowApplication.getInstance();
        /**
         * TEST 2
         * ----------
         * Lets be sure that we only have a single receiver assigned for this intent
         */
        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);
        /**
         * TEST 3
         * ----------
         * Fetch the Broadcast receiver and cast it to the correct class.
         * Next call the "onReceive" method and check if the MyBroadcastIntentService was started
         */
        PhoneCallReceiver receiver = (PhoneCallReceiver) receiversForIntent.get(0);
        receiver.onReceive(app.getApplicationContext(), intent);

        Intent serviceIntent = app.peekNextStartedService();
        assertEquals("Expected no service to be invoked", null, serviceIntent);

    }

    @Test
    public void testOnReceiveRinging() {
        setUpConnection(ConnectivityManager.TYPE_WIFI, true);

        /** TEST 1
         ----------
         We defined the Broadcast receiver with a certain action, so we should check if we have
         receivers listening to the defined action
         */
        Intent intent = getPhoneCallIntent(TelephonyManager.EXTRA_STATE_RINGING);

        ShadowApplication app = ShadowApplication.getInstance();
        assertTrue(app.hasReceiverForIntent(intent));

        /**
         * TEST 2
         * ----------
         * Lets be sure that we only have a single receiver assigned for this intent
         */
        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);

        assertEquals("Expected one broadcast receiver", 1, receiversForIntent.size());

        /**
         * TEST 3
         * ----------
         * Fetch the Broadcast receiver and cast it to the correct class.
         * Next call the "onReceive" method and check if the MyBroadcastIntentService was started
         */
        PhoneCallReceiver receiver = (PhoneCallReceiver) receiversForIntent.get(0);
        receiver.onReceive(app.getApplicationContext(), intent);

        Intent serviceIntent = app.peekNextStartedService();
        assertEquals("Expected the MyBroadcast service to be invoked",
                JunkCallService.class.getCanonicalName(),
                serviceIntent.getComponent().getClassName());

    }

    @Test
    public void testOnReceiveIdleNoConnection() {
        setUpConnection(ConnectivityManager.TYPE_WIFI, false);

        ShadowApplication app = ShadowApplication.getInstance();

        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        ShadowLocalBroadcastManager lbm = shadowOf(instance);

        Intent intent = getPhoneCallIntent(TelephonyManager.EXTRA_STATE_IDLE);

        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);

        PhoneCallReceiver receiver = (PhoneCallReceiver) receiversForIntent.get(0);
        receiver.onReceive(app.getApplicationContext(), intent);

        List<Intent> sent = lbm.getSentBroadcastIntents();
        assertEquals("should no action: ", 0, sent.size());
    }

    @Test
    public void testOnReceiveIdle() {
        setUpConnection(ConnectivityManager.TYPE_WIFI, true);

        ShadowApplication app = ShadowApplication.getInstance();

        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        ShadowLocalBroadcastManager lbm = shadowOf(instance);

        Intent intent = getPhoneCallIntent(TelephonyManager.EXTRA_STATE_IDLE);

        assertTrue(app.hasReceiverForIntent(intent));

        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);
        assertEquals("Expected one broadcast receiver", 1, receiversForIntent.size());

        PhoneCallReceiver receiver = (PhoneCallReceiver) receiversForIntent.get(0);
        receiver.onReceive(app.getApplicationContext(), intent);

        List<Intent> sent = lbm.getSentBroadcastIntents();
        assertEquals("should sent 1 intent: ", 1, sent.size());
    }

    @Test
    public void testOnReceiveOffhookNoConnection() {
        setUpConnection(ConnectivityManager.TYPE_WIFI, false);

        ShadowApplication app = ShadowApplication.getInstance();

        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        ShadowLocalBroadcastManager lbm = shadowOf(instance);

        Intent intent = getPhoneCallIntent(TelephonyManager.EXTRA_STATE_OFFHOOK);

        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);

        PhoneCallReceiver receiver = (PhoneCallReceiver) receiversForIntent.get(0);
        receiver.onReceive(app.getApplicationContext(), intent);

        List<Intent> sent = lbm.getSentBroadcastIntents();
        assertEquals("should no action: ", 0, sent.size());
    }

    @Test
    public void testOnReceiveOffhook() {
        setUpConnection(ConnectivityManager.TYPE_WIFI, true);

        ShadowApplication app = ShadowApplication.getInstance();

        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        ShadowLocalBroadcastManager lbm = shadowOf(instance);

        Intent intent = getPhoneCallIntent(TelephonyManager.EXTRA_STATE_OFFHOOK);
        assertTrue(app.hasReceiverForIntent(intent));

        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);
        assertEquals("Expected one broadcast receiver", 1, receiversForIntent.size());

        PhoneCallReceiver receiver = (PhoneCallReceiver) receiversForIntent.get(0);
        receiver.onReceive(app.getApplicationContext(), intent);

        List<Intent> sent = lbm.getSentBroadcastIntents();
        assertEquals("should sent 1 intent: ", 1, sent.size());
    }
}
