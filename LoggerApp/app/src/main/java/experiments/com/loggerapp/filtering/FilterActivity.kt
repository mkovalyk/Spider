package experiments.com.loggerapp.filtering

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import experiments.com.logger.Logger
import experiments.com.logger.filter.Builder
import experiments.com.loggerapp.R
import experiments.com.loggerapp.TestApplication
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.content_filter.*

class FilterActivity : AppCompatActivity() {

    val levels = mapOf(Pair("Verbose", Logger.VERBOSE), Pair("Debug", Logger.DEBUG), Pair("Info", Logger.INFO),
            Pair("Warning", Logger.WARN), Pair("Error", Logger.ERROR))

    val logStorage by lazy { (application as TestApplication).logStorage }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(toolbar)

        val logsAdapter = LogAdapter(mutableListOf())
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = logsAdapter
        list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ArrayAdapter<String>(this, R.layout.layout_level, R.id.level, levels.keys.toTypedArray())
        level.adapter = adapter
        level.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(this@FilterActivity, "Text: $p2", Toast.LENGTH_LONG).show()
                val filter = Builder().apply {
                    logLevel = levels[adapter.getItem(p2)]!!
                }.create()
                Thread {
                    logsAdapter.logs.clear()
                    logsAdapter.logs.addAll(logStorage.applyFilter(filter))
                    list.post {
                        logsAdapter.notifyDataSetChanged()
                    }
                }.start()
            }
        }
    }
}
