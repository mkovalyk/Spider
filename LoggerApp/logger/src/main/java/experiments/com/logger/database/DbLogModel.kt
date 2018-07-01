package experiments.com.logger.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created on 18.09.2017.
 */

@Entity(tableName = "logs")
class DbLogModel {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
    var logLevelStr: String? = null
    var logLevel: Byte? = null
    var tag: String? = null
    var message: String? = null
    var timeStr: String? = null
    var threadId: Long = 0
    var time: Long = 0
    var ex: String? = null

    constructor()
    @Ignore
    constructor(logLevel: Byte, logLevelStr: String, tag: String, message: String, threadId: Long, time: Long, timeStr: String?, ex: String) {
        this.logLevel = logLevel
        this.logLevelStr = logLevelStr
        this.tag = tag
        this.message = message
        this.threadId = threadId
        this.time = time
        this.timeStr = timeStr
        this.ex = ex
    }
}