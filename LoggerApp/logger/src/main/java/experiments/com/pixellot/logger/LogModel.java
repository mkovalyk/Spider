package experiments.com.pixellot.logger;

/**
 * Created on 18.02.17.
 */

public class LogModel {
    public final int logLevel;
    public final String tag;
    public final String message;
    public final long threadId;
    public final long time;
    public final Exception ex;

    public LogModel(int logLevel, String tag, String message, long threadId, long time, Exception ex) {
        this.logLevel = logLevel;
        this.tag = tag;
        this.message = message;
        this.threadId = threadId;
        this.time = time;
        this.ex = ex;
    }
}
