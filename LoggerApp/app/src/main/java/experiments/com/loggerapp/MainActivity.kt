package experiments.com.loggerapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import experiments.com.logger.C
import experiments.com.logger.LogModel
import experiments.com.logger.Logger
import experiments.com.logger.copyToExternalStorage
import experiments.com.loggerapp.filtering.FilterActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    internal val logger by lazy { (application as TestApplication).logger }
    internal var counter = 0
    internal var startTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            logger.listener = object : Logger.Listener {
                override fun log(logModel: LogModel) {
                    if (counter == 0) {
                        startTime = System.nanoTime()
                        Log.d("LoggerResult", "Start Logging:" + Thread.currentThread().id)
                    }
                    if (counter == SIZE - 1) {
                        Log.d("LoggerResult", "End Logging.Time:${TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)}")
                        counter = 0
                    }
                    counter++
                }
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Please make sure you have permission to access file", Toast.LENGTH_LONG).show()
        }

        button.setOnClickListener { testBenchmark() }
        copy.setOnClickListener { copy() }
        clearAll.setOnClickListener { clearAll() }
        filter.setOnClickListener { startActivity(Intent(this@MainActivity, FilterActivity::class.java)) }
    }

    private fun clearAll() {
//        val db = logger.db
//        val logsDao = db.logsDao()
//        Thread({
//            logsDao.clearAll()
//        }).start()
    }

    private fun copy() {
        copyToExternalStorage(C.DB_NAME)
        Toast.makeText(this, "File is copied. Please find file ${C.DB_NAME} in external storage", Toast.LENGTH_LONG).show()
    }

    private fun testBenchmark() {
        val startTime = System.nanoTime()
        for (i in 0 until SIZE) {
//            when {
//                i % 25 == 0 -> logger.e("Logger-error", "Message", IllegalStateException("Test"))
//                i % 23 == 0 -> logger.w("Logger-warn", "Message", IllegalArgumentException("Something is wrong"))
//                else -> logger.d("Logger", "Logger->Message" + System.currentTimeMillis())
            logger.d("Logger", "Logger->Message" + System.currentTimeMillis())
//            }
        }
        Log.d("LoggerResult", "Logger ->Time:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime))
    }

    companion object {
        const val SIZE = 10
    }
}
