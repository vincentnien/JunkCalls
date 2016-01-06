import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.MainActivity;
import com.miracolab.junkcalls.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeUnit;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by vincent on 2016/1/5.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    @Test
    public void testDefaultChecked() {
        SharedPreferences sharedPrefs = RuntimeEnvironment.application.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean checked = sharedPrefs.getBoolean("wifi_only", true);

        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        CheckBox box = (CheckBox) activity.findViewById(R.id.wifi_only);

        assertEquals(checked, box.isChecked());
    }


    @Test
    public void testDefaultChecked1() {
        SharedPreferences sharedPrefs = RuntimeEnvironment.application.getSharedPreferences("settings", Context.MODE_PRIVATE);
        sharedPrefs.edit().putBoolean("wifi_only", false).commit();
        boolean checked = sharedPrefs.getBoolean("wifi_only", true);

        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        CheckBox box = (CheckBox) activity.findViewById(R.id.wifi_only);

        assertEquals(checked, box.isChecked());
    }

    @Test
    public void testClickCheckBox() {
        SharedPreferences sharedPrefs = RuntimeEnvironment.application.getSharedPreferences("settings", Context.MODE_PRIVATE);
        sharedPrefs.edit().putBoolean("wifi_only", true).commit();

        boolean isWifiOnly = sharedPrefs.getBoolean("wifi_only", true);

        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        CheckBox box = (CheckBox) activity.findViewById(R.id.wifi_only);
        box.performClick();

        boolean isWifiOnlyNext = sharedPrefs.getBoolean("wifi_only", true);
        assertTrue(isWifiOnly == isWifiOnlyNext);

        Observable.just(null)
                .delay(550L, TimeUnit.MILLISECONDS)
                .toBlocking()
                .subscribe(o -> {
                    boolean isWifiOnlyNext2 = sharedPrefs.getBoolean("wifi_only", true);
                    assertFalse(isWifiOnly == isWifiOnlyNext2);
                });
    }
}
