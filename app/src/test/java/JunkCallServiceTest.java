import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.JunkCallService;
import com.miracolab.junkcalls.parser.JunkCall;
import com.miracolab.junkcalls.parser.JunkCallParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;
import org.robolectric.util.ServiceController;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;
import static org.robolectric.shadows.support.v4.Shadows.shadowOf;

/**
 * Created by vincent on 2016/1/5.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class JunkCallServiceTest {
    ServiceController<JunkCallService> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildService(JunkCallService.class);
    }

    // TODO: find a way to test it
    @Test
    public void testOnStartCommand() {
        Intent intent = new Intent();
        intent.putExtra(JunkCallService.EXTRA_NUMBER, "0987352706");
        controller
                .attach()
                .create()
                .withIntent(intent)
                .startCommand(0, 0);
    }

    @Test
    public void testReceiveCallState() {
        TestSubscriber testSubscriber = new TestSubscriber<String>();
        controller.get().receiveCallState()
                .subscribe(testSubscriber);

        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        ShadowLocalBroadcastManager mgr = shadowOf(instance);

        Intent intent = new Intent(JunkCallService.ACTION_CALL_STATE);
        intent.putExtra(JunkCallService.EXTRA_STATE, "test_state");
        mgr.sendBroadcast(intent);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue("test_state");
    }

    @Test
    public void testStartQuery() {
        CountDownLatch latch = new CountDownLatch(1);
        controller.get().startQuery("0987352706")
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertTrue(false);
                        latch.countDown();
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        assertTrue(strings.size()>=2);
                    }
                });

        // Flush all worker tasks out of queue and force them to execute.
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.getBackgroundThreadScheduler().idleConstantly(true);
        // Flush all UI tasks out of queue and force them to execute.
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.getForegroundThreadScheduler().idleConstantly(true);

        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
