import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.R;
import com.miracolab.junkcalls.provider.vo.Record;
import com.miracolab.junkcalls.utils.FloatingWindow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by vincent on 2016/1/5.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class FloatingWindowTest {

    FloatingWindow window;

    @Before
    public void setUp() {
        window = new FloatingWindow();
        window.create(RuntimeEnvironment.application);
    }

    @Test
    public void testFloatingWindow() {
        assertFalse(window.isEnded());
        assertTrue(window.getView() != null);
    }

    @Test
    public void testSetMissingCall() {
        window.setMissingCall(RuntimeEnvironment.application);

        assertTrue(window.isEnded());
        assertTrue(window.getView() == null);
    }

    @Test
    public void testSetPickupCall() {
        window.setPickupCall(RuntimeEnvironment.application);

        assertTrue(window.isEnded());
        assertTrue(window.getView() == null);
    }

    @Test
    public void testSetResult1() {
        String number = "0912345678";
        int count = 100;
        String myTitle = "d1/d2/d3";
        Record record = new Record(number, myTitle, count);
        window.setResult(RuntimeEnvironment.application, record);

        View view = window.getView();
        String title = ((TextView)view.findViewById(R.id.text_title)).getText().toString();
        String body = ((TextView)view.findViewById(R.id.text_body)).getText().toString();

        assertEquals(myTitle, title);

        String myBody = number + RuntimeEnvironment.application.getString(R.string.report_count, count);
        assertEquals(myBody, body);
    }

    @Test
    public void testSetResult() {
        String number = "0912345678";
        List<String> list = Arrays.asList("a", "b", "c");
        window.setResult(RuntimeEnvironment.application, list, number);

        View view = window.getView();
        String title = ((TextView)view.findViewById(R.id.text_title)).getText().toString();
        String body = ((TextView)view.findViewById(R.id.text_body)).getText().toString();

        int count = list.size();
        String myTitle = TextUtils.join("/", list.subList(0, min(5, count)));
        assertEquals(myTitle, title);

        String myBody = number + RuntimeEnvironment.application.getString(R.string.report_count, count);
        assertEquals(myBody, body);
    }

    @Test
    public void testSetResult0() {
        String number = "0912345678";
        List<String> list = Arrays.asList();
        window.setResult(RuntimeEnvironment.application, list, number);

        View view = window.getView();
        String title = ((TextView)view.findViewById(R.id.text_title)).getText().toString();
        String body = ((TextView)view.findViewById(R.id.text_body)).getText().toString();

        String myTitle = RuntimeEnvironment.application.getString(R.string.no_data);
        assertEquals(myTitle, title);

        String myBody = number + RuntimeEnvironment.application.getString(R.string.no_report);
        assertEquals(myBody, body);
    }
}
