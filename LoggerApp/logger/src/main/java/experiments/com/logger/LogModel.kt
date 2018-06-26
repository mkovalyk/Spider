package experiments.com.logger

/**
 * Created on 18.02.17.
 */

data class LogModel(val logLevel: Int, val tag: String, val message: String, val threadId: Long, val time: Long, val ex: Throwable?, var timeStr: String = "")
