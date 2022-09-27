package com.union.musicplayer.utils.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;

public class Threads {

    private static ThreadsImplementation implementation = new ThreadsImplementation();

    private Threads() {
        implementation = new ThreadsImplementation();
    }

    public static ExecutorService getIoThreadPool() {
        return implementation.getIoThreadPool();
    }

    public static int getMaxThreadsOfIoThreadPool() {
        return implementation.getMaxThreadsOfIoThreadPool();
    }

    public static ExecutorService getComputationThreadPool() {
        return implementation.getComputationThreadPool();
    }

    public static int getMaxThreadsOfComputationThreadPool() {
        return implementation.getMaxThreadsOfComputationThreadPool();
    }

//    public static ScheduledExecutorService getScheduledExecutorService() {
//        return implementation.getScheduledExecutorService();
//    }

    public static Handler getLightWorkHandler() {
        return implementation.getLightWorkHandler();
    }

    public static Handler getHeavyWorkHandler() {
        return implementation.getHeavyWorkHandler();
    }

    /**
     * Helps to trace performance
     */
    public static void postInMainThread(Runnable runnable) {
        implementation.postInMainThread(runnable);
    }

    /**
     * Helpes to trace performance
     */
    public static void postDelayedInMainThread(Runnable runnable, long delayMillis) {
        implementation.postDelayedInMainThread(runnable, delayMillis);
    }

    /**
     * Helpes to trace performance
     */
    public static void removeCallbacksInMainThread(Runnable runnable) {
        implementation.removeCallbacksInMainThread(runnable);
    }

    /**
     * Print log or throw if caller is not in thread.
     */
    public static void verifyThread() {
        implementation.verifyThread();
    }

    public static void verifyMainThread() {
        implementation.verifyMainThread();
    }

    public static boolean isThread() {
        return (Looper.getMainLooper() != Looper.myLooper());
    }

    public static boolean isMainThread() {
        return (Looper.getMainLooper() == Looper.myLooper());
    }

    public static LogCallback getLogCallback() {
        return UtilsConfig.getLogCallback();
    }

    public void removeCallbacksInLightWorkThread(Runnable runnable) {
        implementation.removeCallbacksInLightWorkThread(runnable);
    }

    public void removeCallbacksInHeavyWorkThread(Runnable runnable) {
        implementation.removeCallbacksInHeavyWorkThread(runnable);
    }

}
