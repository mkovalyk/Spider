package experiments.com.pixellot.logger.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created on 18.09.2017.
 */

@Database(entities = arrayOf(DbLogModel::class), version = 1)
abstract class LogsDatabase : RoomDatabase() {
    abstract fun logsDao(): LogsDao
}
