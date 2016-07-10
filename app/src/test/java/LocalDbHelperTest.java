import android.content.ContentResolver;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ibm.icu.text.RelativeDateTimeFormatter;
import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.R;
import com.miracolab.junkcalls.provider.JunkcallProvider;
import com.miracolab.junkcalls.provider.LocalDbHelper;
import com.miracolab.junkcalls.provider.vo.Record;
import com.miracolab.junkcalls.utils.FloatingWindow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by vincent on 2016/1/5.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LocalDbHelperTest {

    private ContentResolver mContentResolver;
    private ShadowContentResolver mShadowContentResolver;
    private JunkcallProvider mProvider;

    @Before
    public void setup() {
        mProvider = new JunkcallProvider();
        mContentResolver = RuntimeEnvironment.application.getContentResolver();
        mShadowContentResolver = Shadows.shadowOf(mContentResolver);
        mProvider.onCreate();
        ShadowContentResolver.registerProvider(JunkcallProvider.AUTHORITY, mProvider);
    }


    @Test
    public void testInsert() {
        List<Record> data = new ArrayList<>();
        int count = (int)(Math.random()*20);
        for(int i=0; i<count; ++i) {
            String number = "0912" + String.format("%06d", (int)(Math.random()*999999));
            String description = "abcd/efghi";
            int report = (int)(Math.random()*100);
            data.add(new Record(number, description, report));
        }

        LocalDbHelper.insert(RuntimeEnvironment.application, data);

        assertEquals(count, LocalDbHelper.getRecordCount(RuntimeEnvironment.application));
    }

    @Test
    public void testGetRecord() {
        CountDownLatch latch = new CountDownLatch(1);

        List<Record> data = new ArrayList<>();
        String number = "0912345678";
        String description = "aaa/bbb";
        data.add(new Record(number, description, 10));
        LocalDbHelper.insert(RuntimeEnvironment.application, data);

        LocalDbHelper.getRecord(RuntimeEnvironment.application, number)
                .subscribe(record -> {
                    assertTrue(record.isValid());
                    assertEquals(record.number, number);
                    assertEquals(record.description, description);
                    assertEquals(record.report, 10);
                    latch.countDown();
                });

        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
