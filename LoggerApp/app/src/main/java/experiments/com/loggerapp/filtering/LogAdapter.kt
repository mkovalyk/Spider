package experiments.com.loggerapp.filtering

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import experiments.com.logger.LogLevel
import experiments.com.logger.LogModel
import experiments.com.loggerapp.R
import kotlinx.android.synthetic.main.layout_log_item.view.*
import java.util.*

/**
 * Created on 29.06.2018.
 */
class LogAdapter(val logs: MutableList<LogModel>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LogViewHolder(layoutInflater.inflate(R.layout.layout_log_item, parent, false))
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    inner class LogViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(log: LogModel) {
            val color = when (log.logLevel) {
                LogLevel.VERBOSE -> R.color.verbose
                LogLevel.DEBUG -> R.color.debug
                LogLevel.INFO -> R.color.info
                LogLevel.WARNING -> R.color.warning
                LogLevel.ERROR -> R.color.error
                else -> android.R.color.transparent
            }
            view.date.text = Date(log.time).toString()
            view.log.text = log.message
            view.log.setTextColor(view.resources.getColor(color))
        }
    }
}