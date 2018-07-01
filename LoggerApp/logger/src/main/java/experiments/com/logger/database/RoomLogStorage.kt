package experiments.com.logger.database

import experiments.com.logger.LogModel
import experiments.com.logger.LogStorage
import experiments.com.logger.filter.Filter
import experiments.com.logger.toDbModel
import experiments.com.logger.toModel

/**
 * Created on 03.03.2018.
 */
class RoomLogStorage(private val dao: LogsDao) : LogStorage {
    override fun removeOlderThan(lowerBound: Long) {
        dao.deleteOlderThan(lowerBound)
    }

    override fun delete(id: Long) {
        dao.delete(id)
    }

    override fun applyFilter(filter: Filter): List<LogModel> {
        return dao.getAll(filter.startDate, filter.endDate, filter.level)
                .map { it -> it.toModel() }
    }

    override fun insertAll(vararg logModels: LogModel) {
        dao.insertAll(logModels.map { it -> it.toDbModel() })
    }
}