package experiments.com.logger.filter

import experiments.com.logger.Logger
import java.util.*

/**
 * Created on 24.02.2018.
 */
class Filter constructor(val startDate: Long = Long.MAX_VALUE, val endDate: Long = Long.MIN_VALUE,
                         val level: Int = Logger.INFO, val tags: List<String>)

class Builder {
    var startDate: Long = Long.MIN_VALUE
    var endDate: Long = Long.MAX_VALUE
    @Logger.LogLevel
    var logLevel: Int = Logger.INFO
    var tags: List<String> = emptyList()

    fun startingFrom(date: Date)= apply{
        startDate = date.time
    }

    fun endsWith(date: Date) = apply {
        endDate = date.time
    }

    fun withLevel(level: Int) = apply{
        logLevel = level
    }

    fun withTags(tags: List<String>) = apply{
        this.tags = tags
    }

    fun create(): Filter {
        return Filter(startDate, endDate, logLevel, tags)
    }
}