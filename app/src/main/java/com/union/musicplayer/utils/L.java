package com.union.musicplayer.utils;

import android.content.Context;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.union.musicplayer.AppApplication;

/**
 * name:chenqingru
 * data:
 * des:
 */
public class L {
    private static Logger build(String tag) {
        Logger logger = XLog.tag(tag).build();
        return logger;
    }

    public static Logger base = build("player.base");
    public static Logger api = build("player.api");
    public static Logger eventbus = build("player.eventbus");
    public static Logger login = build("player.login");
}
