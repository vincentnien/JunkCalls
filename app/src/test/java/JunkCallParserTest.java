import com.miracolab.junkcalls.BuildConfig;
import com.miracolab.junkcalls.parser.JunkCall;
import com.miracolab.junkcalls.parser.JunkCallParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;

import static org.junit.Assert.assertTrue;

/**
 * Created by vincent on 2016/1/5.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class JunkCallParserTest {

    // FIXME: compare real data
    @Test
    public void testQueryJunkCall() {
        TestSubscriber subscriber = new TestSubscriber();
        JunkCallParser
                .queryPhoneNumber("0987352706")
                .distinct(JunkCall::description)
                .map(JunkCall::description)
                .toList()
                .toBlocking()
                .subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
//                .subscribe(junks -> {
//                    assertTrue(junks.size() >= 2);
//                });
    }
}
