package experiments.com.logger.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created on 18.09.2017.
 */
@Dao
interface LogsDao {
    @Query("DELETE FROM logs")
    fun clearAll()

    @Delete
    fun delete(model: DbLogModel)

    @Query("DELETE FROM logs WHERE uid=:id")
    fun delete(id: Long)

    @Query("DELETE FROM logs WHERE time<:maxTime")
    fun deleteOlderThan(maxTime: Long)

    @Insert
    fun insertAll(logModels: List<DbLogModel>)
}
