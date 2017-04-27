package experiments.com.pixellot.logger;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created on 17.02.17.
 */

public class Logger {
    public static final String TAG = Logger.class.getSimpleName();
    private static final int DEBUG = 2;
    /**
     * Time after which handler goes to sleep mode.
     */
    private static final long DELAY_BEFORE_SLEEP = TimeUnit.SECONDS.toMillis(10);
    private static final int ERROR = 5;
    private static final int INFO = 3;
    private static final int VERBOSE = 1;
    private static final int WARN = 4;
    //    private static Logger instance;
    private final File file;
    private final GregorianCalendar gregorianCalendar = new GregorianCalendar();
    private final int logLevel;
    private final StringBuilder stringBuilder = new StringBuilder();
    private Thread handlerThread;
    private Queue<LogModel> logModels = new LinkedBlockingQueue<>();
    private BufferedOutputStream fileOutputStream;
    private long timeOflastLoggedMessage;
    private boolean isRunning = true;

    public Logger(Context context) throws IOException {
        logLevel = DEBUG;

        File folder = new File(Environment.getExternalStorageDirectory(), "logsFolder");
        if (folder.exists() && folder.isFile()) {
            folder.delete();
        }
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.w(TAG, "");
            }
        }

        file = new File(folder, LoggerUtils.getDefaultFileName());
        Log.d(TAG, "Logger: " + file.getAbsolutePath());

//        initHandler();
    }

    public void d(String tag, String message) {
        send(DEBUG, tag, message, Thread.currentThread().getId(), null);
    }

    public void e(String tag, String message) {
        send(ERROR, tag, message, Thread.currentThread().getId(), null);
    }

    public static void flush() {
//        instance.handler.sendMessage(instance.handler.obtainMessage(FINISH_BATCH));
    }

    public void i(String tag, String message) {
        send(INFO, tag, message, Thread.currentThread().getId(), null);
    }

    public void init(Context context) throws IOException {
//        if (instance == null) {
//            instance = new Logger(context);
//        }
    }

    public static void sleep() {
//        instance.handler.sendMessage(instance.handler.obtainMessage(SLEEP));
    }

    public void v(String tag, String message) {
        send(VERBOSE, tag, message, Thread.currentThread().getId(), null);
    }

    public void w(String tag, String message) {
        send(WARN, tag, message, Thread.currentThread().getId(), null);
    }

    private void initHandler() throws FileNotFoundException {
        handlerThread = new Thread("LoggerThread") {
            @Override
            public void run() {
                int counter = 0;
                while (isRunning || logModels.size() > 0) {
                    LogModel model = logModels.poll();
                    if (model != null) {
                        log("D", model.tag, model.message, model.threadId, model.time, Log.getStackTraceString(model.ex));
                        Log.d(model.tag, model.message);
                    } else {
                        continue;
                    }
                    counter++;
                    if (counter == 50) {
                        try {
                            fileOutputStream.flush();
                            counter = 0;
                        } catch (IOException e) {
                            Log.w(TAG, "run: ", e);
                        }
                    }
                }
            }
        };
        handlerThread.start();
        timeOflastLoggedMessage = System.currentTimeMillis();
        fileOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
    }

    private void log(String level, String tag, String message, long threadId, long time, @Nullable String stackTrace) {
        gregorianCalendar.setTimeInMillis(time);
        stringBuilder.setLength(0); //

        stringBuilder
                .append(gregorianCalendar.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(gregorianCalendar.get(Calendar.MINUTE)).append(":")
                .append(gregorianCalendar.get(Calendar.SECOND)).append(".")
                .append(gregorianCalendar.get(Calendar.MILLISECOND)).append(" ")
                .append(level).append("/").append(threadId).append(" ")
                .append(tag).append("--").append(message);
        if (stackTrace != null) {
            stringBuilder.append(" \n").append(stackTrace);
        }
        stringBuilder.append("\n");
        byte[] buffer = stringBuilder.toString().getBytes();
        try {
            fileOutputStream.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeOflastLoggedMessage = System.currentTimeMillis();
    }

    private void send(@LogLevel int level, String tag, String message, long threadId, Exception ex) {
        if (logLevel <= level) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - timeOflastLoggedMessage > DELAY_BEFORE_SLEEP) {
                try {
                    initHandler();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            LogModel model = new LogModel(tag, message, threadId, currentTimeMillis, ex);
            logModels.offer(model);
        }
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface LogLevel {
    }
}
