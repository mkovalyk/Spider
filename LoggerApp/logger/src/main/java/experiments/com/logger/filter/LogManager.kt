package experiments.com.logger.filter

import experiments.com.logger.LogModel
import experiments.com.logger.LogStorage
import java.io.File

/**
 * Created on 03.03.2018.
 */

class LogManager constructor(val logStorage: LogStorage) {
    fun createFileWithFilter(filter: Filter, outputPath: String): File {
        return File(outputPath)
    }

    fun applyFilter(filter: Filter): List<LogModel> {
        return logStorage.applyFilter(filter)
    }
}
