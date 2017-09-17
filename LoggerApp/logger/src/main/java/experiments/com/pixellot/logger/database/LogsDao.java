package experiments.com.pixellot.logger.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created on 18.09.2017.
 */
@Dao
public interface LogsDao {
    @Delete
    void delete(DbLogModel user);

    @Query("SELECT * FROM logs WHERE uid=:id")
    DbLogModel findByName(int id);

    @Query("SELECT * FROM logs")
    List<DbLogModel> getAll();

    @Insert
    void insertAll(DbLogModel... logModels);
}
