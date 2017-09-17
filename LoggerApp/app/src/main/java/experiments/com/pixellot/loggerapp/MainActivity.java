package experiments.com.pixellot.loggerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import experiments.com.pixellot.logger.LogModel;
import experiments.com.pixellot.logger.Logger;

public class MainActivity extends AppCompatActivity {

    Logger logger;
    int counter = 0;
    long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            logger = new Logger(this);
            logger.setListener(new Logger.Listener() {
                @Override
                public void log(LogModel logModel) {
                    if(counter == 0)
                    {
                        startTime = System.nanoTime();
                        Log.d("LoggerResult","Start Logging:" + Thread.currentThread().getId());
                    }
                    counter++;
                    if (counter == 1000) {
                        Log.d("LoggerResult","End Logging.Time:" +TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
                        counter =0;
                    }
                }
            });
//            Logger.init(this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please make sure you have permission to access file", Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testBenchmark();
            }
        });
    }

    private void testBenchmark() {
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            logger.d("Logger", "Logger->Message" + i);
        }
        Log.d("LoggerResult", "Logger ->Time:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));

//        startTime = System.nanoTime();
//        for (int i = 0; i < 1000; i++) {
//            Log.d("Logger", "Default->Message" + i);
//        }
//        Log.d("DefaultLoggerResult", "Time:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
    }
}
