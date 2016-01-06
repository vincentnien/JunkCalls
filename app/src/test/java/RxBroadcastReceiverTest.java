import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.rx.RxBroadcastReceiver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.shadows.support.v4.Shadows.shadowOf;

/**
 * Created by vincent on 2016/1/5.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RxBroadcastReceiverTest {

    @Test
    public void testFromBroadcastLocal() {
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        ShadowLocalBroadcastManager mgr = shadowOf(instance);
        CountDownLatch latch = new CountDownLatch(1);

        IntentFilter filter = new IntentFilter("action.test");
        RxBroadcastReceiver
                .fromBroadcast(instance, filter)
                .first()
                .subscribe(
                        intentWithContext -> {
                            assertTrue(true);
                            latch.countDown();
                        },
                        throwable -> {
                            fail(throwable.toString());
                            latch.countDown();
                        }
                );

        Intent intent = new Intent("action.test");
        mgr.sendBroadcast(intent);

        try {
            latch.await(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("interrupted exception");
        }

        assertEquals(0, mgr.getRegisteredBroadcastReceivers().size());
    }

    @Test
    public void testFromBroadcast() {
        CountDownLatch latch = new CountDownLatch(1);

        IntentFilter filter = new IntentFilter("action.test");
        RxBroadcastReceiver
                .fromBroadcast(RuntimeEnvironment.application, filter)
                .first()
                .subscribe(
                        intentWithContext -> {
                            assertTrue(true);
                            latch.countDown();
                        },
                        throwable -> {
                            fail(throwable.toString());
                            latch.countDown();
                        }
                );


        /** TEST 1
         ----------
         We defined the Broadcast receiver with a certain action, so we should check if we have
         receivers listening to the defined action
         */
        Intent intent = new Intent("action.test");

        ShadowApplication app = ShadowApplication.getInstance();
        assertTrue(app.hasReceiverForIntent(intent));

        /**
         * TEST 2
         * ----------
         * Lets be sure that we only have a single receiver assigned for this intent
         */
        List<BroadcastReceiver> receiversForIntent = app.getReceiversForIntent(intent);

        assertEquals("Expected one broadcast receiver", 1, receiversForIntent.size());

        receiversForIntent.get(0).onReceive(app.getApplicationContext(), intent);

        try {
            latch.await(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("interrupted exception");
        }

        /**
         * TEST3
         * -----------
         * make sure unregister is happened
         */
        assertFalse(app.hasReceiverForIntent(intent));
    }

}
