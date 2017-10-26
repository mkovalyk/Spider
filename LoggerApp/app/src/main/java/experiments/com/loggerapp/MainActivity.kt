package experiments.com.loggerapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import experiments.com.logger.C
import experiments.com.logger.LogModel
import experiments.com.logger.Logger
import experiments.com.logger.LoggerUtils.copyToExternalStorage
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    internal lateinit var logger: Logger
    internal var counter = 0
    internal var startTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            logger = Logger(this)
            logger.setListener(object : Logger.Listener {
                override fun log(logModel: LogModel) {
                    if (counter == 0) {
                        startTime = System.nanoTime()
                        Log.d("LoggerResult", "Start Logging:" + Thread.currentThread().id)
                    }
                    counter++
                    if (counter == SIZE) {
                        Log.d("LoggerResult", "End Logging.Time:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime))
                        counter = 0
                    }
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Please make sure you have permission to access file", Toast.LENGTH_LONG).show()
        }

        findViewById<View>(R.id.button).setOnClickListener { testBenchmark() }
        findViewById<View>(R.id.copy).setOnClickListener { copy() }
        findViewById<View>(R.id.clearAll).setOnClickListener { clearAll()}
    }

    private fun clearAll() {
        val db = logger.db
        val logsDao = db.logsDao()
        Thread({
            logsDao.clearAll()
        }).start()
    }

    private fun copy() {
        copyToExternalStorage(C.DB_NAME)
        Toast.makeText(this, "File is copied. Please find file ${C.DB_NAME} in external storage", Toast.LENGTH_LONG).show()
    }

    private fun testBenchmark() {
        val startTime = System.nanoTime()
        for (i in 0 until SIZE) {
            when {
                i % 25 == 0 -> logger.e("Logger-error", "Message", IllegalStateException("Test"))
                i % 23 == 0 -> logger.w("Logger-warn", "Message", IllegalArgumentException("Something is wrong"))
                else -> logger.d("Logger", "Logger->Message" + System.currentTimeMillis())
            }
        }
        Log.d("LoggerResult", "Logger ->Time:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime))
    }

    companion object {
        val SIZE = 1000
    }
}
