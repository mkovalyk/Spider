package experiments.com.logger

import experiments.com.logger.filter.Filter

/**
 * Just a bunch of functions that serve as an interface for communication with persistence storage.
 *
 * Created on 03.03.2018.
 */
interface LogStorage {
    fun delete(id: Long)

    fun removeOlderThan(lowerBound: Long)

    fun insertAll(vararg logModels: LogModel)

    fun applyFilter(filter: Filter): List<LogModel>
}