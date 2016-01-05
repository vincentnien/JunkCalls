import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.utils.NetworkUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by vincent on 2016/1/4.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PhoneCallReceiverTest {

    @Test
    public void testOnReceive() {

    }
}
