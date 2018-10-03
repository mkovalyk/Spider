@file:Suppress("unused")

package experiments.com.logger

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created on 17.02.17.
 */
@SuppressWarnings("Unused")
class Logger
constructor(private val logStorage: LogStorage, private val logLevel: LogLevel = LogLevel.DEBUG) {
    private val gregorianCalendar = GregorianCalendar()
    private val stringBuilder = StringBuilder()
    private lateinit var handlerThread: HandlerThread
    private val logModels = LinkedBlockingQueue<LogModel>()
    private val isRunning = AtomicBoolean(false)
    private val callback = Handler.Callback { msg ->
        when (msg.what) {
            MESSAGE_WAKE_UP -> {
                wakeUpAndLog()
                return@Callback true
            }
            MESSAGE_QUIT -> {
                quit()
            }
        }
        false
    }
    private lateinit var handler: Handler

    @Volatile
    var isInitialized: AtomicBoolean = AtomicBoolean(false)
    var listener: Listener? = null

    fun d(tag: String, message: String) {
        send(LogLevel.DEBUG, tag, message, Thread.currentThread().id, null)
    }

    fun e(tag: String, message: String, ex: Throwable? = null) {
        send(LogLevel.ERROR, tag, message, Thread.currentThread().id, ex)
    }

    fun i(tag: String, message: String) {
        send(LogLevel.INFO, tag, message, Thread.currentThread().id, null)
    }

    private fun quit() {
        if (handlerThread.quitSafely()) {
            Log.d(TAG, "Exited safely")
            isInitialized.set(false)
        } else {
            Log.w(TAG, "Couldn't exit safely")
        }
    }

    fun v(tag: String, message: String) {
        send(LogLevel.VERBOSE, tag, message, Thread.currentThread().id, null)
    }

    fun w(tag: String, message: String, ex: Throwable? = null) {
        send(LogLevel.WARNING, tag, message, Thread.currentThread().id, ex)
    }

    private fun initHandler() {
        isInitialized.set(true)
        handlerThread = HandlerThread("LoggerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper, callback)
    }

    private fun log(model: LogModel) {
        gregorianCalendar.timeInMillis = model.time
        stringBuilder.setLength(0) // reuse single StringBuilder.. for smaller memory footprint

        stringBuilder
                .append(gregorianCalendar.get(Calendar.DAY_OF_MONTH)).append(".")
                .append(gregorianCalendar.get(Calendar.MONTH) + 1).append(" ") //starts from 0
                .append(gregorianCalendar.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(gregorianCalendar.get(Calendar.MINUTE)).append(":")
                .append(gregorianCalendar.get(Calendar.SECOND)).append(".")
                .append(gregorianCalendar.get(Calendar.MILLISECOND)).append(" ")
        model.timeStr = stringBuilder.toString()

        logStorage.insertAll(model)
        listener?.log(model)
    }

    private fun send(level: LogLevel, tag: String, message: String, threadId: Long, ex: Throwable?) {
        if (logLevel.priority <= level.priority) {
            val currentTimeMillis = System.currentTimeMillis()
            val model = LogModel(level, tag, message, threadId, currentTimeMillis, ex)
            logModels.offer(model)
            if (!isInitialized.get()) {
                initHandler()
            }
            if (!isRunning.get()) {
                val handlerMessage = Message.obtain(handler, MESSAGE_WAKE_UP)
                handler.sendMessage(handlerMessage)
                // set it here to make sure that during next iteration
                // we won't enter to this block.
                isRunning.set(true)
            }
        }
    }

    private fun wakeUpAndLog() {
        Log.d(TAG, "wakeUpAndLog{${Thread.currentThread().id}}")
        isRunning.set(logModels.size > 0)
        handler.removeMessages(MESSAGE_QUIT)
        do {
            val model = logModels.poll(30, TimeUnit.SECONDS)
            if (model != null) {
                log(model)
                Log.d(model.tag, model.message + " " + logModels.size)
            }
        } while (logModels.size > 0)
        isRunning.set(false)
        val exitMessage = Message.obtain(handler, MESSAGE_QUIT)
        handler.sendMessageDelayed(exitMessage, DELAY_BEFORE_SLEEP)
    }

    interface Listener {
        fun log(logModel: LogModel)
    }

    companion object {
        const val MESSAGE_WAKE_UP = 1
        const val MESSAGE_QUIT = 2
        val TAG = Logger::class.java.simpleName
        /**
         * Time after which handler goes to sleep mode.
         */
        private val DELAY_BEFORE_SLEEP = TimeUnit.SECONDS.toMillis(10)

        const val VERBOSE = 1.toByte()
        const val DEBUG = 2.toByte()
        const val INFO = 3.toByte()
        const val WARN = 4.toByte()
        const val ERROR = 5.toByte()

        fun flush() {
            // TODO implement in future
        }

        fun sleep() {
            // TODO implement in future.
        }
    }
}
