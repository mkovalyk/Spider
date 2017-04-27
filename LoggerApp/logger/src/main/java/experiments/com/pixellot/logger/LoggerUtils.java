package experiments.com.pixellot.logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created on 17.02.17.
 */

public class LoggerUtils {
    public static String getDefaultFileName() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        return C.PREFIX + gregorianCalendar.get(Calendar.MONTH) + gregorianCalendar.get(Calendar.YEAR) + ".txt";
    }
}
