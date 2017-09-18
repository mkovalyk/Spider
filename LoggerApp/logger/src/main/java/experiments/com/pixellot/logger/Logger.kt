@file:Suppress("unused")

package experiments.com.pixellot.logger

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.support.annotation.IntDef
import android.util.Log
import experiments.com.pixellot.logger.database.DbLogModel
import experiments.com.pixellot.logger.database.LogsDao
import experiments.com.pixellot.logger.database.LogsDatabase
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created on 17.02.17.
 */
@SuppressWarnings("Unused")
class Logger
constructor(context: Context) {
    private val db: LogsDatabase
    //    private static Logger instance;
//    private val file: File
    private val gregorianCalendar = GregorianCalendar()
    private val logLevel: Long
    private val logsDao: LogsDao
    private val stringBuilder = StringBuilder()
    private var handlerThread: HandlerThread? = null
    private val logModels = LinkedBlockingQueue<LogModel>()
    //    private var fileOutputStream: BufferedOutputStream? = null
//    private var timeOflastLoggedMessage: Long = 0
    private val isRunning = AtomicBoolean(false)
    private var listener: Listener? = null
    private var handler: Handler? = null

    init {
        logLevel = DEBUG

//        val folder = File(Environment.getExternalStorageDirectory(), "logsFolder")
//        if (folder.exists() && folder.isFile) {
//            folder.delete()
//        }
//        if (!folder.exists()) {
//            if (!folder.mkdir()) {
//                Log.w(TAG, "")
//            }
//        }
        db = Room.databaseBuilder<LogsDatabase>(context.applicationContext, LogsDatabase::class.java, C.DB_NAME).build()
        logsDao = db.logsDao()
//        file = File(folder, LoggerUtils.defaultFileName)
//        file.delete()
//        file.createNewFile()
//        Log.d(TAG, "Logger: " + file.absolutePath)
        //        initHandler();
    }

    fun d(tag: String, message: String) {
        send(DEBUG, tag, message, Thread.currentThread().id, null)
    }

    fun e(tag: String, message: String) {
        send(ERROR, tag, message, Thread.currentThread().id, null)
    }

    fun i(tag: String, message: String) {
        send(INFO, tag, message, Thread.currentThread().id, null)
    }

    @Throws(IOException::class)
    fun init(context: Context) {
        //        if (instance == null) {
        //            instance = new Logger(context);
        //        }
    }

    val isInitialized: Boolean
        get() = handlerThread != null && handler != null

    fun quit() {
        if (isInitialized) {
            handlerThread!!.quitSafely()
        } else {
            if (handlerThread != null) {
                handlerThread!!.quitSafely()
            }
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun v(tag: String, message: String) {
        send(VERBOSE, tag, message, Thread.currentThread().id, null)
    }

    fun w(tag: String, message: String) {
        send(WARN, tag, message, Thread.currentThread().id, null)
    }

    private fun getStringValueForLevel(@LogLevel logLevel: Long): String {
        return when (logLevel) {
            DEBUG -> "D"
            ERROR -> "E"
            INFO -> "I"
            VERBOSE -> "V"
            WARN -> "W"
            else -> "WTF"
        }
    }

    private fun initHandler() {
        Log.d(TAG, "initHandler: ")
        if (handlerThread == null) {
            handlerThread = HandlerThread("LoggerThread")
            handlerThread!!.start()
            handler = Handler(handlerThread!!.looper, Handler.Callback { msg ->
                when (msg.what) {
                    MESSAGE_WAKE_UP -> {
                        //                                    Log.d(TAG, "handleMessage: " + msg);
                        wakeUpAndLog()
                        return@Callback true
                    }
                }
                false
            })
//            timeOflastLoggedMessage = System.currentTimeMillis()
//            fileOutputStream = BufferedOutputStream(FileOutputStream(file, true))
        }
    }

    private fun log(model: LogModel) {
        gregorianCalendar.timeInMillis = model.time
        stringBuilder.setLength(0) // reuse single StringBuilder..

        stringBuilder
                .append(gregorianCalendar.get(Calendar.DAY_OF_MONTH)).append(".")
                .append(gregorianCalendar.get(Calendar.MONTH) + 1).append(" ") //starts from 0
                .append(gregorianCalendar.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(gregorianCalendar.get(Calendar.MINUTE)).append(":")
                .append(gregorianCalendar.get(Calendar.SECOND)).append(".")
                .append(gregorianCalendar.get(Calendar.MILLISECOND)).append(" ")
        //                .append(getStringValueForLevel(model.logLevel)).append("/").append(model.threadId).append(" ")
        //                .append(model.tag).append("--").append(model.message);
        //        if (model.ex != null) {
        //            stringBuilder.append(" \n").append(Log.getStackTraceString(model.ex));
        //        }
        //        stringBuilder.append("\n");
        //        byte[] buffer = stringBuilder.toString().getBytes();
        //        try {
        //            fileOutputStream.write(buffer, 0, buffer.length);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        val stacktrace: String =
                if (model.ex != null) {
                    Log.getStackTraceString(model.ex)
                } else ""

        val dbLogModel = DbLogModel(getStringValueForLevel(model.logLevel), model.tag, model.message,
                model.threadId, model.time, stringBuilder.toString(), stacktrace)
        logsDao.insertAll(dbLogModel)
        //        List<DbLogModel> items = logsDao.getAll();
        //        Log.d(TAG, "log: " + items.size());
        listener?.log(model)
//        timeOflastLoggedMessage = System.currentTimeMillis()
    }

    private fun send(@LogLevel level: Long, tag: String, message: String, threadId: Long, ex: Exception?) {
        if (logLevel <= level) {
            val currentTimeMillis = System.currentTimeMillis()
            val model = LogModel(level, tag, message, threadId, currentTimeMillis, ex)
            logModels.offer(model)
            if (isInitialized) {
                if (!isRunning.get()) {
                    val handlerMessage = Message.obtain(handler, MESSAGE_WAKE_UP)
                    handler!!.sendMessage(handlerMessage)
                    // set it here to make sure that during next iteration
                    // we won't enter to this block.
                    isRunning.set(true)
                }
            } else {
                try {
                    initHandler()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun wakeUpAndLog() {
        //        Log.d(TAG, "wakeUpAndLog: " + logModels.size());
        isRunning.set(logModels.size > 0)
        while (logModels.size > 0) {
            val model = logModels.poll()
            if (model != null) {
                //                Log.d(TAG, "wakeUpAndLog: " + model + ". Size:" + logModels.size());
                log(model)
                Log.d(model.tag, model.message + " " + logModels.size)
            } else {
                continue
            }
        }
        isRunning.set(false)
    }

    @IntDef(VERBOSE, DEBUG, INFO, WARN, ERROR)
    @kotlin.annotation.Retention(value = AnnotationRetention.SOURCE)
    internal annotation class LogLevel

    interface Listener {
        fun log(logModel: LogModel)
    }

    companion object {
        val MESSAGE_WAKE_UP = 1
        val TAG = Logger::class.java.simpleName
        const val DEBUG = 2L
        /**
         * Time after which handler goes to sleep mode.
         */
        private val DELAY_BEFORE_SLEEP = TimeUnit.SECONDS.toMillis(10)
        const val ERROR = 5L
        const val INFO = 3L
        const val VERBOSE = 1L
        const val WARN = 4L

        fun flush() {
            //        instance.handler.sendMessage(instance.handler.obtainMessage(FINISH_BATCH));
        }

        fun sleep() {
            //        instance.handler.sendMessage(instance.handler.obtainMessage(SLEEP));
        }
    }
}
