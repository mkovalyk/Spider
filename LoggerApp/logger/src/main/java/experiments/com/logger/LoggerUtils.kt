package experiments.com.logger

import android.content.Context
import android.os.Environment
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

/**
 * Created on 17.02.17.
 */

object LoggerUtils {
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
