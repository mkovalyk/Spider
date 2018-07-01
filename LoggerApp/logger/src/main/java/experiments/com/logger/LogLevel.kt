package experiments.com.logger

/**
 * Created on 01.07.2018.
 */
enum class LogLevel(val priority: Byte, val string: String) {
    VERBOSE(1, "V"),
    DEBUG(2, "D"),
    INFO(3, "I"),
    WARNING(4, "W"),
    ERROR(5, "E");

    companion object {
        fun getFromString(value: String): LogLevel {
            return when (value) {
                VERBOSE.string -> VERBOSE
                DEBUG.string -> DEBUG
                INFO.string -> INFO
                WARNING.string -> WARNING
                ERROR.string -> ERROR
                else -> throw IllegalStateException("Can not parse $value")
            }
        }
    }
}