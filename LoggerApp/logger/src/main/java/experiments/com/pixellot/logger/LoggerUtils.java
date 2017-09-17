package experiments.com.pixellot.logger;

import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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

    public static boolean copyToExternalStorage(Context context, String filename) {
        File destination = new File(Environment.getExternalStorageDirectory(), filename);
        File source = context.getDatabasePath(filename);
        try
        {
            FileUtils.copyFile(source, destination);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
