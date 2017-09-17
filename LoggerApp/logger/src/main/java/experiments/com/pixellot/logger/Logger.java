package experiments.com.pixellot.logger;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 17.02.17.
 */

public class Logger {
    public static final int MESSAGE_WAKE_UP = 1;
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
    private HandlerThread handlerThread;
    private Queue<LogModel> logModels = new LinkedBlockingQueue<>();
    private BufferedOutputStream fileOutputStream;
    private long timeOflastLoggedMessage;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Listener listener;
    private Handler handler;

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
        file.delete();
        file.createNewFile();
        Log.d(TAG, "Logger: " + file.getAbsolutePath());

//        initHandler();
    }

    public static void flush() {
//        instance.handler.sendMessage(instance.handler.obtainMessage(FINISH_BATCH));
    }

    public static void sleep() {
//        instance.handler.sendMessage(instance.handler.obtainMessage(SLEEP));
    }

    public void d(String tag, String message) {
        send(DEBUG, tag, message, Thread.currentThread().getId(), null);
    }

    public void e(String tag, String message) {
        send(ERROR, tag, message, Thread.currentThread().getId(), null);
    }

    public void i(String tag, String message) {
        send(INFO, tag, message, Thread.currentThread().getId(), null);
    }

    public void init(Context context) throws IOException {
//        if (instance == null) {
//            instance = new Logger(context);
//        }
    }

    public boolean isInitialized() {
        return handlerThread != null && handler != null;
    }

    public void quit() {
        if (isInitialized()) {
            handlerThread.quitSafely();
        } else {
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void v(String tag, String message) {
        send(VERBOSE, tag, message, Thread.currentThread().getId(), null);
    }

    public void w(String tag, String message) {
        send(WARN, tag, message, Thread.currentThread().getId(), null);
    }

    @NonNull
    private String getStringValueForLevel(@LogLevel int logLevel) {
        switch (logLevel) {
            case DEBUG:
                return "D";
            case ERROR:
                return "E";
            case INFO:
                return "I";
            case VERBOSE:
                return "V";
            case WARN:
                return "W";
            default:
                return "WTF";
        }
    }

    private void initHandler() throws FileNotFoundException {
        Log.d(TAG, "initHandler: ");
        if (handlerThread == null) {
            handlerThread = new HandlerThread("LoggerThread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                        case MESSAGE_WAKE_UP:
//                                    Log.d(TAG, "handleMessage: " + msg);
                            wakeUpAndLog();
                            return true;
                    }
                    return false;
                }
            });
            timeOflastLoggedMessage = System.currentTimeMillis();
            fileOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
        }
    }

    private void log(LogModel model) {
        gregorianCalendar.setTimeInMillis(model.time);
        stringBuilder.setLength(0); // reuse single StringBuilder..

        stringBuilder
                .append(gregorianCalendar.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(gregorianCalendar.get(Calendar.MINUTE)).append(":")
                .append(gregorianCalendar.get(Calendar.SECOND)).append(".")
                .append(gregorianCalendar.get(Calendar.MILLISECOND)).append(" ")
                .append(getStringValueForLevel(model.logLevel)).append("/").append(model.threadId).append(" ")
                .append(model.tag).append("--").append(model.message);
        if (model.ex != null) {
            stringBuilder.append(" \n").append(Log.getStackTraceString(model.ex));
        }
        stringBuilder.append("\n");
        byte[] buffer = stringBuilder.toString().getBytes();
        try {
            fileOutputStream.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "log: " + Thread.currentThread().getId());
        if (listener != null) {
            listener.log(model);
        }
        timeOflastLoggedMessage = System.currentTimeMillis();
    }

    private void send(@LogLevel int level, String tag, String message, long threadId, Exception ex) {
        if (logLevel <= level) {
            long currentTimeMillis = System.currentTimeMillis();
            LogModel model = new LogModel(level, tag, message, threadId, currentTimeMillis, ex);
            logModels.offer(model);
            if (isInitialized()) {
                if (!isRunning.get()) {
                    Message handlerMessage = Message.obtain(handler, MESSAGE_WAKE_UP);
                    handler.sendMessage(handlerMessage);
                    // set it here to make sure that during next iteration
                    // we won't enter to this block.
                    isRunning.set(true);
                }
            } else {
                try {
                    initHandler();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void wakeUpAndLog() {
//        Log.d(TAG, "wakeUpAndLog: " + logModels.size());
        int counter = 0;
        isRunning.set(logModels.size() > 0);
        while (logModels.size() > 0) {
            LogModel model = logModels.poll();
            if (model != null) {
//                Log.d(TAG, "wakeUpAndLog: " + model + ". Size:" + logModels.size());
                log(model);
                Log.d(model.tag, model.message + " " + logModels.size());
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
        isRunning.set(false);
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface LogLevel {
    }

    public interface Listener {
        void log(LogModel logModel);
    }
}
