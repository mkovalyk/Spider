package experiments.com.pixellot.logger;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
    public static final String KEY_MESSAGE = "value";
    public static final String KEY_STACKTRACE = "exception";
    public static final String KEY_TAG = "tag";
    public static final String KEY_THREAD_ID = "threadId";
    public static final String KEY_TIME = "time";
    public static final String TAG = Logger.class.getSimpleName();
    private static final int DEBUG = 2;
    private static final long DELAY_BEFORE_FLUSH = TimeUnit.SECONDS.toMillis(2);
    /**
     * Time after which handler goes to sleep mode.
     */
    private static final long DELAY_BEFORE_SLEEP = TimeUnit.SECONDS.toMillis(10);
    private static final int ERROR = 5;
    private static final int FINISH_BATCH = 100;
    private static final int INFO = 3;
    private static final int SLEEP = 101;
    private static final int VERBOSE = 1;
    private static final int WARN = 4;
    private static Logger instance;
    private final File file;
    private final GregorianCalendar gregorianCalendar = new GregorianCalendar();
    private final int logLevel;
    private final StringBuilder stringBuilder = new StringBuilder();
    //    private Handler handler;
    private Thread handlerThread;
    private Queue<LogModel> logModels = new LinkedBlockingQueue<>();
    private BufferedOutputStream fileOutputStream;
    private final Object object = new Object();
    private final Runnable sleepRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "sleep");
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private final Runnable finishBatchRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "flush");
            try {
                fileOutputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                // TODO create some more optimal solution if file can not be found.
                // most likely we don't have permission to write.
                // We are on the HandlerThread so we cannot throw exception directly as it may
                // cause application to crash.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private long timeOflastLoggedMessage;
    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            String logLevel = "unknown!!!";
            final LogModel model = (LogModel) message.obj;
            switch (message.what) {
                case SLEEP:
//                    handler.removeCallbacks(sleepRunnable);
//                    handler.removeCallbacks(finishBatchRunnable);
                    finishBatchRunnable.run();
                    sleepRunnable.run();
                    return true;
                case FINISH_BATCH:
//                    handler.removeCallbacks(finishBatchRunnable);
                    finishBatchRunnable.run();
                    return true;
                case VERBOSE:
                    logLevel = "V";
                    Log.v(model.tag, model.message);
                    break;
                case DEBUG:
                    logLevel = "D";
                    Log.d(model.tag, model.message);
                    break;
                case INFO:
                    logLevel = "I";
                    Log.i(model.tag, model.message);
                    break;
                case WARN:
                    logLevel = "W";
                    Log.w(model.tag, model.message);
                    break;
                case ERROR:
                    logLevel = "***ERROR***";
                    Log.e(model.tag, model.message);
                    break;
            }
//            long time = data.getLong(KEY_TIME);
//            long threadId = data.getLong(KEY_THREAD_ID);

            log(logLevel, model.tag, model.message, model.threadId, model.time, Log.getStackTraceString(model.ex));
            return true;
        }
    };
    private boolean isRunning = true;

    private Logger(Context context) throws IOException {
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

        initHandler();
    }

    public static void d(String tag, String message) {
        instance.send(DEBUG, tag, message, Thread.currentThread().getId(), null);
    }

    public static void e(String tag, String message) {
        instance.send(ERROR, tag, message, Thread.currentThread().getId(), null);
    }

    public static void flush() {
//        instance.handler.sendMessage(instance.handler.obtainMessage(FINISH_BATCH));
    }

    public static void i(String tag, String message) {
        instance.send(INFO, tag, message, Thread.currentThread().getId(), null);
    }

    public static void init(Context context) throws IOException {
        if (instance == null) {
            instance = new Logger(context);
        }
    }

    public static void sleep() {
//        instance.handler.sendMessage(instance.handler.obtainMessage(SLEEP));
    }

    public static void v(String tag, String message) {
        instance.send(VERBOSE, tag, message, Thread.currentThread().getId(), null);
    }

    public static void w(String tag, String message) {
        instance.send(WARN, tag, message, Thread.currentThread().getId(), null);
    }

    private void initHandler() throws FileNotFoundException {
        handlerThread = new Thread("Logger") {
            @Override
            public void run() {
                while (isRunning || logModels.size() > 0) {
                    LogModel model = logModels.poll();
                    if (model != null) {
                        log("D", model.tag, model.message, model.threadId, model.time, Log.getStackTraceString(model.ex));
                        Log.d(model.tag, model.message);
                    }
                    if (logModels.size() == 0)
                        try {
                            synchronized (object){
                                object.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }
        };
        handlerThread.start();
//        handler = new Handler(callback);
        timeOflastLoggedMessage = System.currentTimeMillis();
        fileOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
    }

    private void log(String level, String tag, String message, long threadId, long time, @Nullable String stackTrace) {
//        handler.removeCallbacks(sleepRunnable);
//        handler.removeCallbacks(finishBatchRunnable);
        gregorianCalendar.setTimeInMillis(time);
        stringBuilder.setLength(0);

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
//        handler.postDelayed(finishBatchRunnable, DELAY_BEFORE_FLUSH);
//        handler.postDelayed(sleepRunnable, DELAY_BEFORE_SLEEP);
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
//            Message handlerMessage = handler.obtainMessage(level, model);
//
//            Bundle bundle = new Bundle();
//            bundle.putLong(KEY_THREAD_ID, threadId);
//            bundle.putString(KEY_TAG, tag);
//            bundle.putString(KEY_MESSAGE, message);
//            bundle.putLong(KEY_TIME, currentTimeMillis);
//            if (ex != null) {
//                bundle.putString(KEY_STACKTRACE, Log.getStackTraceString(ex));
//            }
//            handlerMessage.setData(bundle);
//            handler.sendMessage(handlerMessage);
            logModels.offer(model);
            synchronized (object) {
                object.notify();
            }
        }
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface LogLevel {
    }
}
