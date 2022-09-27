package com.union.musicplayer.utils.thread;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {

    private static final String THREAD_POOL_NAME_PREFIX = "StartupThreadPool_";
    private static ExecutorService startupThreadPool = new ThreadPoolExecutor(0, 2,
            0, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new StartUpFactory(THREAD_POOL_NAME_PREFIX), new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService getIoThreadPool() {
        return Threads.getIoThreadPool();
    }

    public static int getMaxThreadsOfIoThreadPool() {
        return Threads.getMaxThreadsOfIoThreadPool();
    }

    public static void destroyStartupPool() {
        startupThreadPool.shutdown();
        startupThreadPool = null;
    }

    public static ExecutorService getComputationThreadPool() {
        return Threads.getComputationThreadPool();
    }

    /**
     * 启动时使用
     */
    public static ExecutorService getStartupThreadPool() {
        return startupThreadPool;
    }

    public static int getMaxThreadsOfComputationThreadPool() {
        return Threads.getMaxThreadsOfComputationThreadPool();
    }

    public static Handler getLightWorkHandler() {
        return Threads.getLightWorkHandler();
    }

    public static Handler getWorkHandler() {
        return Threads.getHeavyWorkHandler();
    }

    /**
     * Helps to trace performance
     */
    public static void postInMainThread(Runnable runnable) {
        Threads.postInMainThread(runnable);
    }

    /**
     * Helps to trace performance
     */
    public static void postDelayedInMainThread(Runnable runnable, long delayMillis) {
        Threads.postDelayedInMainThread(runnable, delayMillis);
    }

    /**
     * Helpes to trace performance
     */
    public static void removeCallbacksInMainThread(Runnable runnable) {
        Threads.removeCallbacksInMainThread(runnable);
    }

    /**
     * Print log or throw if caller is not in thread.
     */
    public static void verifyThread() {
        Threads.verifyThread();
    }

    public static void verifyMainThread() {
        Threads.verifyMainThread();
    }

    public static boolean isThread() {
        return Threads.isThread();
    }

    public static boolean isMainThread() {
        return Threads.isMainThread();
    }

    /**
     * Convenient tool to check the methods run in the same thread,
     * otherwise :
     * throw exception for debug version,
     * output error log for release version.
     * <p>
     * Usage : checker = SameThreadChecker.get();
     * checker.setupThread()  // the first time calling.
     * checker.check() // other times calling
     * checker.check() // more calling
     * <p>
     * or
     * checker.check()
     */
    public static class SameThreadChecker {
        ThreadsImplementation.SameThreadChecker checker;

        private SameThreadChecker(String name) {
            checker = ThreadsImplementation.SameThreadChecker.get(name);
        }

        public static SameThreadChecker get(String name) {
            return new SameThreadChecker(name);
        }

        public void setupThread() {
            checker.setupThread();
        }

        public void check() {
            checker.check();
        }

        public boolean notSameThread() {
            return checker.notSameThread();
        }
    }

    private static class StartUpFactory implements ThreadFactory {

        private final AtomicInteger counter = new AtomicInteger(0);

        private final String prefix;

        StartUpFactory(String prefix) {
            this.prefix = prefix;
        }

        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, prefix + this.counter.incrementAndGet());
            thread.setPriority(5);
            return thread;
        }
    }
}
