package experiments.com.logger.filter

import experiments.com.logger.Logger
import java.io.File

/**
 * Created on 03.03.2018.
 */

class LogManager constructor(val logger: Logger) {
    fun createFileWithFilter(filter: Filter, outputPath: String): File {
        logger.context
        return File(outputPath)
    }
}
