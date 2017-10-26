package experiments.com.logger;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("experiments.com.logger.test", appContext.getPackageName());
    }
    @Test
    public void testLotOfLogs() throws IOException {
        Logger.init(InstrumentationRegistry.getTargetContext());
        long startTime = System.nanoTime();
        for(int i = 0; i< 1000; i++) {
            Logger.d("Logger", "Logger->Message" + i);
        }
        Log.d("LoggerResult", "Logger ->Time:" +(System.nanoTime() - startTime));

        startTime = System.nanoTime();
        for(int i = 0; i < 1000; i++) {
            Log.v("Logger", "Default->Message" + i);
        }
        Log.d("DefaultLoggerResult", "Time:" +(System.nanoTime() - startTime));
    }
}
