package com.union.musicplayer.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.DrawableRes;
import androidx.annotation.UiThread;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.union.musicplayer.AppApplication;
import com.union.musicplayer.utils.thread.ThreadUtil;


public class UiUtils {
    public static final long WAIT_LAYOUT_DELAY_MILLIS = 200;

    public static final long PERIOD_DEFAULT = 500;

    private static long lastClickTime;

    public static void waitLayoutComplete(final OnLayoutCompleteListener onGlobalLayoutListener, final View view) {
        if (isLayoutComplete(view)) {
            if (onGlobalLayoutListener != null) {
                onGlobalLayoutListener.onLayoutComplete(true);
            }
            return;
        }

        ViewTreeObserver.OnGlobalLayoutListener listener = new SingleViewLayoutCompleteNotifier(onGlobalLayoutListener, view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    private static boolean isLayoutComplete(View v) {
        return (v.getWidth() != 0 && v.getHeight() != 0);
    }

    public static int getSize(Context context, int dimenId) {
        return context.getResources().getDimensionPixelSize(dimenId);
    }

    public static int getDimensionPixelSize(int dimenId) {
        return AppApplication.getGlobalContext().getResources().getDimensionPixelSize(dimenId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return ResourcesCompat.getDrawable(AppApplication.getGlobalContext().getResources(), drawableId, null);
    }

    public static boolean isFragmentInvalid(Fragment fragment) {
        return fragment == null || fragment.getActivity() == null || fragment.getContext() == null || fragment.isDetached() || fragment.isRemoving();
    }

    public static boolean isFragmentVisible(Fragment fragment) {
        return !isFragmentInvalid(fragment) && fragment.isVisible();
    }

    @UiThread
    public static boolean isFastClick() {
        return isFastClick(PERIOD_DEFAULT);
    }

    @UiThread
    public static boolean isFastClick(long period) {
        long time = System.currentTimeMillis();
        long timeDiff = time - lastClickTime;
        lastClickTime = time;
        return timeDiff < period;
    }

    public interface OnLayoutCompleteListener {
        /**
         * @param wasLayoutComplete True means all views' sizes are non-zero before call waitLayoutComplete, false means we wait layout complete and call this(
         *                          layout is done during waitLayoutComplete), at least one view is not layout-complete before call waitLayoutComplete.
         */
        void onLayoutComplete(boolean wasLayoutComplete);
    }

    private static class SingleViewLayoutCompleteNotifier implements ViewTreeObserver.OnGlobalLayoutListener {
        private final OnLayoutCompleteListener onGlobalLayoutListener;
        private View view;

        private boolean isNotified = false;

        SingleViewLayoutCompleteNotifier(OnLayoutCompleteListener onGlobalLayoutListener, View view) {
            this.onGlobalLayoutListener = onGlobalLayoutListener;
            this.view = view;
            ThreadUtil.postDelayedInMainThread(() -> notifyResult(), WAIT_LAYOUT_DELAY_MILLIS);
        }

        @Override
        public void onGlobalLayout() {
            if (view != null && isLayoutComplete(view)) {
                if (onGlobalLayoutListener != null && !isNotified()) {
                    notifyResult();
                }
            }
        }

        private void removeListener() {
            try {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } catch (Exception e) {
                L.base.e("removeOnGlobalLayoutListener", e);
            }
        }

        private void notifyResult() {
            if (isNotified()) {
                return;
            }
            onGlobalLayoutListener.onLayoutComplete(false);
            removeListener();
            isNotified = true;
            view = null;
        }

        public boolean isNotified() {
            return isNotified;
        }
    }
}
