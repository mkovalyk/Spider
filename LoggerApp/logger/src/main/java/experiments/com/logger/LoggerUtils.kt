package experiments.com.logger

import android.content.Context
import android.os.Environment
import android.util.Log
import experiments.com.logger.database.DbLogModel
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

/**
 * Created on 03.03.2018.
 */
//object LoggerUtils {
fun Context.copyToExternalStorage(filename: String) {
    val destination = File(Environment.getExternalStorageDirectory(), filename)
    val source = this.getDatabasePath(filename)
    try {
        FileUtils.copyFile(source, destination)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

// todo limiting by log level
fun Byte.getStringLevel(): String {
    return when (this) {
        Logger.DEBUG -> "D"
        Logger.ERROR -> "E"
        Logger.INFO -> "I"
        Logger.VERBOSE -> "V"
        Logger.WARN -> "W"
        else -> "WTF"
    }
}

fun LogModel.toDbModel(): DbLogModel {
    val stacktrace: String =
            if (ex != null) {
                Log.getStackTraceString(ex)
            } else ""
    return DbLogModel(logLevel.getStringLevel(), tag, message, threadId, this.time, timeStr, stacktrace)
}

fun DbLogModel.toModel(): LogModel {
    return LogModel(logLevel!!.toLogLevel(), tag!!, message!!, threadId, time, Exception(ex))
}

fun String.toLogLevel(): Byte {
    return when (this) {
        "D" -> Logger.DEBUG
        "E" -> Logger.ERROR
        "I" -> Logger.INFO
        "V" -> Logger.VERBOSE
        "W" -> Logger.WARN
        else -> throw IllegalStateException("Can find log level for $this")
    }
}


//}
