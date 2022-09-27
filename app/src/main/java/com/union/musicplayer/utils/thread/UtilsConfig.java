package com.union.musicplayer.utils.thread;

import android.os.Process;

import java.util.concurrent.TimeUnit;

public class UtilsConfig {

    private volatile static LogCallback logCallback;

    private volatile static boolean crashOnBugDetected;

    private volatile static int computationThreadsCount = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private volatile static int ioThreadsCount = 40;
    private static long lightWorkMaxDuration = TimeUnit.SECONDS.toMillis(1);
    private static long heavyWorkMaxDuration = TimeUnit.SECONDS.toMillis(10);
    private static int threadPriority = Process.THREAD_PRIORITY_DEFAULT;
    private static int ioCorePoolSize = Math.max(4, Runtime.getRuntime().availableProcessors() / 2 + 1);

    private volatile static boolean enableTracing = false;
    private static final LogCallback dummylogCallback = new LogCallback() {
        @Override
        public void v(String format, Object... args) {

        }

        @Override
        public void d(String format, Object... args) {

        }

        @Override
        public void i(String format, Object... args) {

        }

        @Override
        public void w(String format, Object... args) {

        }

        @Override
        public void e(String format, Object... args) {

        }

        @Override
        public void v(String message, Throwable e) {

        }

        @Override
        public void d(String message, Throwable e) {

        }

        @Override
        public void i(String message, Throwable e) {

        }

        @Override
        public void w(String message, Throwable e) {

        }

        @Override
        public void e(String message, Throwable e) {

        }

        @Override
        public void printStackTrace(String tag) {

        }
    };

    public static LogCallback getLogCallback() {
        LogCallback local = UtilsConfig.logCallback;
        return local == null ? dummylogCallback : local;
    }

    public static void setLogCallback(LogCallback logCallback) {
        UtilsConfig.logCallback = logCallback;
    }

    public static boolean isCrashOnBugDetected() {
        return crashOnBugDetected;
    }

    public static void setCrashOnBugDetected(boolean crashOnBugDetected) {
        UtilsConfig.crashOnBugDetected = crashOnBugDetected;
    }

    public static int getComputationThreadsCount() {
        return computationThreadsCount;
    }

    public static void setComputationThreadsCount(int computationThreadsCount) {
        UtilsConfig.computationThreadsCount = computationThreadsCount;
    }

    public static int getIoCorePoolSize() {
        return ioCorePoolSize;
    }

    public static void setIoCorePoolSize(int ioCorePoolSize) {
        UtilsConfig.ioCorePoolSize = ioCorePoolSize;
    }

    public static int getIoThreadsCount() {
        return ioThreadsCount;
    }

    public static void setIoThreadsCount(int ioThreadsCount) {
        UtilsConfig.ioThreadsCount = ioThreadsCount;
    }

    public static long getLightWorkMaxDuration() {
        return lightWorkMaxDuration;
    }

    public static void setLightWorkMaxDuration(long lightWorkMaxDuration) {
        UtilsConfig.lightWorkMaxDuration = lightWorkMaxDuration;
    }

    public static long getHeavyWorkMaxDuration() {
        return heavyWorkMaxDuration;
    }

    public static void setHeavyWorkMaxDuration(long heavyWorkMaxDuration) {
        UtilsConfig.heavyWorkMaxDuration = heavyWorkMaxDuration;
    }

    /**
     * @return the ones in android.os.Process, such as Process.THREAD_PRIORITY_DEFAULT
     */
    public static int getThreadPriority() {
        return threadPriority;
    }

    /**
     * @param threadPriority the ones in android.os.Process, such as Process.THREAD_PRIORITY_DEFAULT
     */
    public static void setThreadPriority(int threadPriority) {
        UtilsConfig.threadPriority = threadPriority;
    }

    public static boolean isEnableTracing() {
        return enableTracing;
    }

    public static void setEnableTracing(boolean enableTracing) {
        UtilsConfig.enableTracing = enableTracing;
    }
}
