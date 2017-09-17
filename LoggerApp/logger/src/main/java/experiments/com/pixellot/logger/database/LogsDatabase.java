package experiments.com.pixellot.logger.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created on 18.09.2017.
 */

@Database(entities = {DbLogModel.class}, version = 1)
public abstract class LogsDatabase extends RoomDatabase {
    public abstract LogsDao logsDao();
}
