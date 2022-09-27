package com.union.musicplayer.utils.thread;

public interface LogCallback {
    void v(String format, Object... args);

    void d(String format, Object... args);

    void i(String format, Object... args);

    void w(String format, Object... args);

    void e(String format, Object... args);

    void v(String message, Throwable e);

    void d(String message, Throwable e);

    void i(String message, Throwable e);

    void w(String message, Throwable e);

    void e(String message, Throwable e);

    void printStackTrace(String tag);
}
