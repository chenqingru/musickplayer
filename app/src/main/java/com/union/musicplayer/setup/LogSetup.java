package com.union.musicplayer.setup;

import android.content.Context;

import androidx.annotation.NonNull;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.internal.SystemCompat;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * name:chenqingru
 * data:
 * des:
 */
public class LogSetup {

    public static final String TAG = "Setting";
    private static final int M_20 = 20 * 1024 * 1024;
    private static final String MM_DD_HH_MM_SS_SSS = "MM-dd HH:mm:ss SSS";
    private static final String LOG_FILE = "setting.log";

    private static volatile String processName;

    public static void setupLog(Context context) {
        LogConfiguration logConfiguration = new LogConfiguration.Builder()
                .threadFormatter(Thread::getName)
                .t()
                .b()
                .borderFormatter(strings -> {
                    if (strings == null || strings.length != 3) {
                        return null;
                    }

                    String thread = strings[0];
                    thread = processName + "/" + thread;
                    String stackTrace = strings[1];
                    String msg = strings[2];
                    return (thread != null ? "[" + thread + "] " : "")
                            + (stackTrace != null ? stackTrace + SystemCompat.lineSeparator : "")
                            + msg;
                })
                .logLevel(LogLevel.ALL)
                .tag(TAG)
                .build();

        String logFile = LOG_FILE;
        processName = "com.union.musicplayer";
        logFile = String.format("%s.log", processName);

        final int size = M_20;
        Printer androidPrinter = new AndroidPrinter();
        Printer filePrinter = new FilePrinter.Builder(getLogDirectory(context))
                .fileNameGenerator(new ChangelessFileNameGenerator(logFile))
                .backupStrategy(new FileSizeBackupStrategy(size))
                .flattener((timeMillis, logLevel, tag, message) -> new SimpleDateFormat(MM_DD_HH_MM_SS_SSS, Locale.CHINA).format(timeMillis)
                        + '|' + LogLevel.getLevelName(logLevel) + '|' + tag + '|' + message)
                .build();

        XLog.init(logConfiguration, androidPrinter, filePrinter);
    }

    public static String getExternalStoragePath(@NonNull Context context) {
        File path = context.getExternalFilesDir(null);

        if (path == null) {
            path = context.getFilesDir();
        }

        return path.getAbsolutePath();
    }

    public static String getLogDirectory(@NonNull Context context) {
        return getExternalStoragePath(context) + File.separator + "log";
    }
}
