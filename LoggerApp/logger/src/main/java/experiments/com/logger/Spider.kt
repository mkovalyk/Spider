package experiments.com.logger

/**
 * Created on 03.07.2018.
 */
object Spider {
    var logger: Logger? = null
    fun init(logStorage: LogStorage, level: LogLevel = LogLevel.DEBUG) {
        if (logger != null) {
            throw IllegalStateException("Can not reinitialize Logger.. At least for now...")
        }
        logger = Logger(logStorage, level)
    }

    fun d(tag: String, message: String) {
        logger.nonNullValue.d(tag, message)
    }

    fun e(tag: String, message: String, ex: Throwable? = null) {
        logger.nonNullValue.e(tag, message, ex)
    }

    fun i(tag: String, message: String) {
        logger.nonNullValue.i(tag, message)
    }

    fun v(tag: String, message: String) {
        logger.nonNullValue.v(tag, message)
    }

    fun w(tag: String, message: String, ex: Throwable? = null) {
        logger.nonNullValue.w(tag, message, ex)
    }
}