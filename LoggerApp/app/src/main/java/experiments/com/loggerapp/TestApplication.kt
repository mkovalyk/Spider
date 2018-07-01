package experiments.com.loggerapp

import android.app.Application
import android.arch.persistence.room.Room
import experiments.com.logger.C
import experiments.com.logger.LogStorage
import experiments.com.logger.Logger
import experiments.com.logger.database.LogsDatabase
import experiments.com.logger.database.RoomLogStorage

/**
 * Created on 29.06.2018.
 */
class TestApplication : Application() {
    lateinit var logStorage: LogStorage
        private set
    lateinit var logger: Logger
        private set

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder<LogsDatabase>(applicationContext, LogsDatabase::class.java, C.DB_NAME).build()
        logStorage = RoomLogStorage(db.logsDao())
        logger = Logger(this, logStorage)
    }
}