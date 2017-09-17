package experiments.com.pixellot.logger.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created on 18.09.2017.
 */

@Entity(tableName = "logs")
public class DbLogModel {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int logLevel;
    private String tag;
    private String message;
    private long threadId;
    private long time;
    private String ex;

    public DbLogModel() {
    }
    @Ignore
    public DbLogModel(int logLevel, String tag, String message, long threadId, long time, String ex) {
        this.logLevel = logLevel;
        this.tag = tag;
        this.message = message;
        this.threadId = threadId;
        this.time = time;
        this.ex = ex;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
