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
public class NetworkUtilTest {

    @Test
    public void isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager smgr = shadowOf(mgr);

        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, false);
        assertFalse(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application));

        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, true);
        assertTrue(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application));

        smgr.setActiveNetworkInfo(null);
        assertFalse(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application));
    }

    @Test
    public void isWifiConnected() {
        ConnectivityManager mgr = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager smgr = shadowOf(mgr);
        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, false);

        assertFalse(NetworkUtil.isWifiConnected(RuntimeEnvironment.application));

        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, true);
        assertFalse(NetworkUtil.isWifiConnected(RuntimeEnvironment.application));

        setActiveNetworkState(smgr, ConnectivityManager.TYPE_WIFI, false);
        assertFalse(NetworkUtil.isWifiConnected(RuntimeEnvironment.application));

        setActiveNetworkState(smgr, ConnectivityManager.TYPE_WIFI, true);
        assertTrue(NetworkUtil.isWifiConnected(RuntimeEnvironment.application));

        smgr.setActiveNetworkInfo(null);
        assertFalse(NetworkUtil.isWifiConnected(RuntimeEnvironment.application));
    }

    @Test
    public void isNetworkConnected2() {
        ConnectivityManager mgr = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager smgr = shadowOf(mgr);
//
//        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, true);
//        assertTrue(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application, false));
//
//        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, true);
//        assertFalse(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application, true));
//
//        setActiveNetworkState(smgr, ConnectivityManager.TYPE_WIFI, false);
//        assertFalse(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application, true));
//
//        setActiveNetworkState(smgr, ConnectivityManager.TYPE_WIFI, true);
//        assertTrue(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application, true));
//
//        smgr.setActiveNetworkInfo(null);
//        assertFalse(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application, false));

        smgr.setActiveNetworkInfo(null);
        assertFalse(NetworkUtil.isNetworkConnected(RuntimeEnvironment.application, true));
    }

    @Test
    public void isWifiNetwork() {
        ConnectivityManager mgr = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager smgr = shadowOf(mgr);
//
//        setActiveNetworkState(smgr, ConnectivityManager.TYPE_MOBILE, true);
//        assertFalse(NetworkUtil.isWifiNetwork(RuntimeEnvironment.application));
//
//        setActiveNetworkState(smgr, ConnectivityManager.TYPE_WIFI, true);
//        assertTrue(NetworkUtil.isWifiNetwork(RuntimeEnvironment.application));

        smgr.setActiveNetworkInfo(null);
        assertFalse(NetworkUtil.isWifiNetwork(RuntimeEnvironment.application));
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
}
