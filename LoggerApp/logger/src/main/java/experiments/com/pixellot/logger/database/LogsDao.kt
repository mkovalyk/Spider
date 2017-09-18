package experiments.com.pixellot.logger.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created on 18.09.2017.
 */
@Dao
interface LogsDao {
    @Delete
    fun delete(user: DbLogModel)

    @Query("SELECT * FROM logs WHERE uid=:id")
    fun findByName(id: Int): DbLogModel

    @get:Query("SELECT * FROM logs")
    val all: List<DbLogModel>

    @Insert
    fun insertAll(vararg logModels: DbLogModel)
}
