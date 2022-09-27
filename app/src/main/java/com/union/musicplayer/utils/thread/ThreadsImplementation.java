package com.union.musicplayer.utils.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class ThreadsImplementation {
    private static final String THREAD_POOL_NAME_PREFIX = "mico-thread-pool";

    private static final String WORK_HANDLER_THREAD_NAME = "mico_work_handler_thread";

    private final MyThreadFactory threadFactoryIo = new MyThreadFactory(MyThreadFactory.TYPE_IO);

    private final MyThreadFactory threadFactoryComputation = new MyThreadFactory(MyThreadFactory.TYPE_COMPUTATION);
    private final Object workHandlerLock = new Object();
    @GuardedBy("workHandlerLock")
    private final AtomicReference<Handler> lightWorkHandler = new AtomicReference<>(null);
    @GuardedBy("workHandlerLock")
    private final AtomicReference<Handler> heavyWorkHandler = new AtomicReference<>(null);
    private final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    /**
     * traced tasks.
     * key is Runnable or Callable, value is enter timestamp.
     * be removed when task finished.
     */
    private final Map<Object, TimeAndType> tracedTasks = Collections.synchronizedMap(new IdentityHashMap<Object, TimeAndType>());
    private final RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Handler workHandler = getLightWorkHandler();
            workHandler.post(r);
        }
    };
    private final TracingThreadPool ioTracingThreadPool
            = new TracingThreadPool(UtilsConfig.getIoCorePoolSize(), UtilsConfig.getIoThreadsCount(),
            TimeUnit.SECONDS.toMillis(10),
            threadFactoryIo,
            rejectedExecutionHandler);
    private final TracingThreadPool computationTracingThreadPool
            = new TracingThreadPool(UtilsConfig.getComputationThreadsCount(),
            TimeUnit.SECONDS.toMillis(2),
            threadFactoryComputation,
            rejectedExecutionHandler);
    private final ScheduledExecutorService scheduledThreadPoolExecutor
            = new ScheduledThreadPoolExecutor(1,
            new MyThreadFactory(),
            rejectedExecutionHandler);

    ThreadsImplementation() {
        if (!UtilsConfig.isEnableTracing()) {
            return;
        }
        long delay = Math.min(UtilsConfig.getHeavyWorkMaxDuration(), UtilsConfig.getLightWorkMaxDuration()) / 2 + 1;
        getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (UtilsConfig.isEnableTracing()) {
                    checkTracedTasks();
                }
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }

    ExecutorService getIoThreadPool() {
        return ioTracingThreadPool;
    }

    int getMaxThreadsOfIoThreadPool() {
        return UtilsConfig.getIoThreadsCount();
    }

    ExecutorService getComputationThreadPool() {
        return computationTracingThreadPool;
    }

    int getMaxThreadsOfComputationThreadPool() {
        return UtilsConfig.getComputationThreadsCount();
    }

    ScheduledExecutorService getScheduledExecutorService() {
        return scheduledThreadPoolExecutor;
    }

    private Handler peekLightWorkHandler() {
        return lightWorkHandler.get();
    }

    Handler getLightWorkHandler() {
        return createWorkHandlerIfNeed(lightWorkHandler, UtilsConfig.getLightWorkMaxDuration());
    }

    private Handler peekHeavyWorkHandler() {
        return heavyWorkHandler.get();
    }

    Handler getHeavyWorkHandler() {
        return createWorkHandlerIfNeed(heavyWorkHandler, UtilsConfig.getHeavyWorkMaxDuration());
    }

    private Handler createWorkHandlerIfNeed(@NonNull AtomicReference<Handler> workHandler, final long maxDurationForWarning) {
        Handler handler = workHandler.get();
        if (handler != null) {
            return handler;
        }

        // lock to avoid multiple HandlerThread created.
        synchronized (workHandlerLock) {
            handler = workHandler.get();
            if (handler != null) {
                return handler;
            }

            HandlerThread handlerThread = new HandlerThread(WORK_HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();
            Handler ret = new Handler(handlerThread.getLooper()) {
                @Override
                public void dispatchMessage(Message msg) {
                    Runnable runnable = msg.getCallback();
                    if (runnable != null) {
                        addTraceTask(runnable, maxDurationForWarning);
                    }
                    super.dispatchMessage(msg);
                    if (runnable != null) {
                        removeTraceTask(runnable);
                    }
                }
            };
            workHandler.set(ret);

            return ret;
        }
    }

    void removeCallbacksInLightWorkThread(Runnable runnable) {
        Handler light = peekLightWorkHandler();
        if (light != null) {
            light.removeCallbacks(runnable);
        }
    }

    void removeCallbacksInHeavyWorkThread(Runnable runnable) {
        Handler light = peekHeavyWorkHandler();
        if (light != null) {
            light.removeCallbacks(runnable);
        }
    }

    /**
     * Helps to trace performance
     */
    void postInMainThread(Runnable runnable) {
        MAIN_HANDLER.post(runnable);
    }

    /**
     * Helpes to trace performance
     */
    void postDelayedInMainThread(Runnable runnable, long delayMillis) {
        MAIN_HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * Helpes to trace performance
     */
    void removeCallbacksInMainThread(Runnable runnable) {
        MAIN_HANDLER.removeCallbacks(runnable);
    }

    /**
     * Print log or throw if caller is not in thread.
     */
    public void verifyThread() {
        if (isThread()) {
            return;
        }

        if (UtilsConfig.isCrashOnBugDetected()) {
            throw new IllegalStateException("Unexpectedly in main thread");
        } else {
            LogCallback localLogCallback = getLogCallback();
            if (localLogCallback != null) {
                localLogCallback.e("Unexpectedly in main thread");
            }
            UtilsConfig.getLogCallback().printStackTrace("BlockMainThreadError");
        }
    }

    void verifyMainThread() {
        if (isMainThread()) {
            return;
        }

        if (UtilsConfig.isCrashOnBugDetected()) {
            throw new IllegalStateException("Non main thread");
        } else {
            Thread thread = Thread.currentThread();
            LogCallback localLogCallback = getLogCallback();
            if (localLogCallback != null) {
                localLogCallback.e("Not main thread %s %s", thread.getName(), thread.getId());
            }
            UtilsConfig.getLogCallback().printStackTrace("NotMainThreadError");
        }
    }

    private boolean isThread() {
        return (Looper.getMainLooper() != Looper.myLooper());
    }

    private boolean isMainThread() {
        return (Looper.getMainLooper() == Looper.myLooper());
    }

    private LogCallback getLogCallback() {
        return UtilsConfig.getLogCallback();
    }

    private void addTraceTask(Object task, long maxDuration) {
        TimeAndType timeAndType = new TimeAndType();
        timeAndType.enterTimeMillis = time();
        timeAndType.maxDuration = maxDuration;
        tracedTasks.put(task, timeAndType);
    }

    private void checkTracedTasks() {
        LogCallback log = getLogCallback();
        Set<Map.Entry<Object, TimeAndType>> entrySet = tracedTasks.entrySet();
        int n = tracedTasks.size();
        for (Map.Entry<Object, TimeAndType> entry : entrySet) {
            if (time() - entry.getValue().enterTimeMillis < entry.getValue().maxDuration) {
                continue;
            }

            log.w("Task %s running duration exceeds threshold %s", entry.getKey(), entry.getValue().maxDuration);
        }
    }

    private void removeTraceTask(Object task) {
        tracedTasks.remove(task);
    }

    private long time() {
        return SystemClock.uptimeMillis();
    }

    static class TimeAndType {
        /**
         * Task start to be processed.
         */
        long enterTimeMillis;
        /**
         * Task estimated to be processed in this millis,
         * if exceeds, will warn in log.
         */
        long maxDuration;
    }

    /**
     * Convenient tool to check the methods run in the same thread,
     * otherwise :
     * throw exception for debug version,
     * output error log for release version.
     * <p>
     * Usage : checker = SameThreadChecker.get();
     * checker.setupThread() // the first time calling.
     * checker.check() // other times calling
     * checker.check() // more calling
     * <p>
     * or
     * checker.check()
     */
    public static class SameThreadChecker {
        private final int IMPOSSIBLE_THREAD_ID = Integer.MIN_VALUE;
        private final String name;
        private volatile long threadId;

        private SameThreadChecker(String name) {
            this.name = name;
        }

        public static SameThreadChecker get(String name) {
            return new SameThreadChecker(name);
        }

        public void setupThread() {
            Thread thread = Thread.currentThread();
            threadId = thread.getId();
            UtilsConfig.getLogCallback().i("%s Initial thread %s %s", name, thread.getName(), thread.getId());
        }

        public void check() {
            if (!notSameThread()) {
                return;
            }

            if (UtilsConfig.isCrashOnBugDetected()) {
                throw new IllegalStateException("Not same thread");
            } else {
                Thread thread = Thread.currentThread();
                UtilsConfig.getLogCallback().e("Not same thread %s %s", thread.getName(), thread.getId());
            }
        }

        public boolean notSameThread() {
            Thread thread = Thread.currentThread();
            long id = thread.getId();
            return id != IMPOSSIBLE_THREAD_ID && id != threadId;
        }
    }

    private class TracingThreadPool extends ThreadPoolExecutor {

        private final long maxDurationForWarning;

        private final AtomicInteger tasksCount = new AtomicInteger(0);

        private final Set<Object> tasks = new CopyOnWriteArraySet<>();

        TracingThreadPool(int maxThreads, long maxDurationForWarning, ThreadFactory factory, RejectedExecutionHandler rejectedExecutionHandler) {
            super(1, maxThreads, 1, TimeUnit.MINUTES, new SynchronousQueue<Runnable>(), factory, rejectedExecutionHandler);
            this.maxDurationForWarning = maxDurationForWarning;
        }

        TracingThreadPool(int corePoolSize, int maxThreads, long maxDurationForWarning, ThreadFactory factory, RejectedExecutionHandler rejectedExecutionHandler) {
            super(corePoolSize, maxThreads, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), factory, rejectedExecutionHandler);
            this.maxDurationForWarning = maxDurationForWarning;
        }

        @Override
        @NonNull
        public Future<?> submit(Runnable task) {
            return super.submit(wrapRunnable(task));
        }

        @Override
        @NonNull
        public <T> Future<T> submit(Runnable task, T result) {
            getLogCallback().i("submit task %s", task);
            return super.submit(wrapRunnable(task), result);
        }

        @Override
        @NonNull
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(wrapCallable(task));
        }

        @Override
        public void execute(Runnable command) {
            super.execute(wrapRunnable(command));
        }

        private Runnable wrapRunnable(final Runnable runnable) {
            if (!UtilsConfig.isEnableTracing()) {
                return runnable;
            }
            return new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(UtilsConfig.getThreadPriority());
                    long addTime = addTask(runnable);
                    runnable.run();
                    removeTask(addTime, runnable);
                }

                @Override
                public String toString() {
                    return runnable.toString();
                }
            };
        }

        private <T> Callable<T> wrapCallable(final Callable<T> callable) {
            if (!UtilsConfig.isEnableTracing()) {
                return callable;
            }
            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    Process.setThreadPriority(UtilsConfig.getThreadPriority());
                    T fruit;
                    long addTime = 0;
                    try {
                        addTime = addTask(callable);
                        fruit = callable.call();
                    } catch (Exception e) {
                        getLogCallback().e("PossibleFC %s", e);
                        throw e;
                    } finally {
                        removeTask(addTime, callable);

                    }
                    return fruit;
                }
            };
        }

        private void dumpTasks() {
            for (Object obj : tasks) {
                getLogCallback().i("task %s", obj);
            }
        }

        /**
         * @param task Runnable or Callable.
         * @return Add time
         */
        private long addTask(Object task) {
            tasks.add(task);
            tasksCount.incrementAndGet();
            addTraceTask(task, maxDurationForWarning);
            return time();
        }

        private void removeTask(long addTime, Object task) {
            tasks.remove(task);
            removeTraceTask(task);
            int tasksCount = this.tasksCount.decrementAndGet();
            int maximumPoolSize = getMaximumPoolSize();
            if (tasksCount >= maximumPoolSize - 1) {
                getLogCallback().e("heavy thread pool load, max thread pool size %s, pending tasks %s",
                        maximumPoolSize, tasksCount);
                dumpTasks();
            }

            long end = time();
            long duration = end - addTime;
            if (duration > maxDurationForWarning) {
                getLogCallback().i("task %s run time costs %s millis", task, duration);
            }
        }
    }

    private class MyThreadFactory implements ThreadFactory {

        static final int TYPE_IO = 0;
        static final int TYPE_COMPUTATION = 1;
        static final int TYPE_SCHEDULE = 2;
        private final int type;
        private final AtomicInteger counter = new AtomicInteger(0);

        MyThreadFactory(int type) {
            this.type = type;
        }

        MyThreadFactory() {
            this.type = TYPE_SCHEDULE;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Runnable testRunnable = r;
            if (type == TYPE_IO || type == TYPE_COMPUTATION) {
                TracingThreadPool threadPool = type == TYPE_IO ? ioTracingThreadPool : computationTracingThreadPool;
                testRunnable = threadPool.wrapRunnable(r);
            }
            Thread thread = new Thread(testRunnable, THREAD_POOL_NAME_PREFIX + counter.incrementAndGet());
            thread.setPriority(Thread.NORM_PRIORITY);
//            Log.d("MyThreadFactory:", "type:" + this.getTypeString() + " counter:" + this.counter);
            return thread;
        }

        private String getTypeString() {
            switch (this.type) {
                case TYPE_IO:
                    return "Io";
                case TYPE_COMPUTATION:
                    return "Computation";
                case TYPE_SCHEDULE:
                    return "Schedule";
            }
            return "unknown";
        }
    }
}
