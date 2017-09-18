package experiments.com.pixellot.logger

import android.content.Context
import android.os.Environment
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created on 17.02.17.
 */

object LoggerUtils {
    val defaultFileName: String
        get() {
            val gregorianCalendar = GregorianCalendar()
            return C.PREFIX + gregorianCalendar.get(Calendar.MONTH) + gregorianCalendar.get(Calendar.YEAR) + ".txt"
        }

    fun Context.copyToExternalStorage(filename: String): Boolean {
        val destination = File(Environment.getExternalStorageDirectory(), filename)
        val source = this.getDatabasePath(filename)
        try {
            FileUtils.copyFile(source, destination)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }
}
