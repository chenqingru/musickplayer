package com.union.musicplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * name:chenqingru
 * data:
 * des:
 */
public class GlideUtils {
    private static final CenterCrop CENTER_CROP = new CenterCrop();

    private static RequestManager getRequestManager(Activity activity) {
        return Glide.with(activity);
    }

    private static RequestManager getRequestManager(Fragment fragment) {
        return Glide.with(fragment);
    }

    private static RequestManager getRequestManager(View view) {
        return Glide.with(view);
    }

    public static void bindImageView(Activity activity, int resId, ImageView iv) {
        if (isValidContextForGlide(activity)) {
            return;
        }
        bindImageView(activity, () -> GlideUtils.getRequestManager(activity), resId, iv);
    }

    public static void bindImageView(Context context, int resId, ImageView iv) {
        bindImageView(context, () -> GlideUtils.getRequestManager(iv), resId, iv);
    }

    private static void bindImageView(Context context, RequestManagerGetter getter, int resId, ImageView iv) {
        bindImageView(context, getter, resId, iv, false);
    }

    private static void bindImageView(Context context, RequestManagerGetter getter, int resId, ImageView iv, boolean diskCache) {
        if (!isValidContextForGlide(context)) {
            return;
        }

        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            RequestManager requestManager = getter.createRequestManager();
            requestManager.load(resId)
                    .apply(RequestOptions.placeholderOf(resId))
                    .apply(RequestOptions.skipMemoryCacheOf(false))
                    .apply(RequestOptions.diskCacheStrategyOf(diskCache ? DiskCacheStrategy.AUTOMATIC : DiskCacheStrategy.NONE))
                    .into(iv);
        }, iv);
    }

    public static void bindImageView(Context context, File file, ImageView iv) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        Glide.with(iv).load(file)
                .apply(RequestOptions.skipMemoryCacheOf(false))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.signatureOf(new ObjectKey(file.lastModified())))
                .into(iv);
    }

    @NotNull
    private static RequestOptions getRequestOptionsHooked(ImageView iv) {
        int width = iv.getMeasuredWidth();
        int height = iv.getMeasuredHeight();
        if (width == 0 || height == 0) {
            L.base.e("getRequestOptionsHooked image view's measured width is " + iv.getMeasuredWidth() + ",measured height is " + iv.getMeasuredHeight());

        }
        return RequestOptions.overrideOf(width, height);
    }

    /**
     * Use the one with width and height instead.
     * This one has OOM risk for url may stands for a very big image.
     *
     * @param context          Context
     * @param url              Url
     * @param iv               ImageView
     * @param placeholderResId Default resource Id.
     */
    @Deprecated
    public static void bindImageViewWithDefault(Context context, String url, ImageView iv, int placeholderResId) {
        bindImageViewWithResIds(context, url, iv, placeholderResId, 0);
    }

    /**
     * Use the one with width and height instead.
     * This one has OOM risk for url may stands for a very big image.
     *
     * @param context          Context
     * @param url              Url
     * @param iv               ImageView
     * @param placeholderResId placeholder resource Id.
     * @param errorResId       Error resource Id.
     */
    private static void bindImageViewWithResIds(Context context, String url, ImageView iv, int placeholderResId, int errorResId) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            int width = iv.getWidth();
            int height = iv.getHeight();
            RequestOptions requestOptions = RequestOptions.overrideOf(width, height);
            if (errorResId != 0) {
                requestOptions = requestOptions.error(errorResId);
            }
            if (placeholderResId != 0) {
                requestOptions = requestOptions.placeholder(placeholderResId);
            }
            Glide.with(iv).load(url)
                    .apply(requestOptions)
                    .into(iv);
        }, iv);
    }

    public static void bindImageViewWithRoundCorners(Context context, String url, ImageView iv, int radius) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            Glide.with(iv).load(url)
                    .apply(getRequestOptionsHooked(iv))
                    .apply(new RequestOptions().transforms(CENTER_CROP, new RoundedCorners(radius)))
                    .into(iv);
        }, iv);
    }

    /**
     * @param context Context
     * @param url     Url
     * @param iv      ImageView
     * @param radius  Radius for round corner.
     * @param holdId  placeholder resource Id, default icon.
     */
    public static void bindImageViewWithRoundCorners(Context context, String url, ImageView iv, int radius, int holdId) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            Glide.with(iv).load(url)
                    .apply(createPlaceHolder(holdId))
                    .apply(getRequestOptionsHooked(iv))
                    .apply(RequestOptions.noAnimation())
                    .apply(new RequestOptions().transforms(CENTER_CROP, new RoundedCorners(radius)))
                    .into(iv);
        }, iv);
    }

    public static void bindImageViewWithRoundUseContext(Context context, String url, ImageView iv, int radius, int holdId) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            Glide.with(context).load(url)
                    .apply(createPlaceHolder(holdId))
                    .apply(getRequestOptionsHooked(iv))
                    .apply(RequestOptions.noAnimation())
                    .apply(new RequestOptions().transforms(CENTER_CROP, new RoundedCorners(radius)))
                    .into(iv);
        }, iv);
    }

    private static RequestOptions createPlaceHolder(int holdId) {
        return RequestOptions.placeholderOf(holdId);
    }

    // 表盘的缩略图很大，所以列表显示时，Glide自己裁剪的需要缓存一份
    public static void bindImageViewWithRoundCorners(Context context, int resId, ImageView iv, int radius, boolean diskCache) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            Glide.with(iv).load(resId)
                    .apply(RequestOptions.skipMemoryCacheOf(false))
                    .apply(RequestOptions.diskCacheStrategyOf(diskCache ? DiskCacheStrategy.AUTOMATIC : DiskCacheStrategy.NONE))
                    .apply(getRequestOptionsHooked(iv))
                    .apply(new RequestOptions().transforms(CENTER_CROP, new RoundedCorners(radius)))
                    .override(iv.getWidth(), iv.getHeight())
                    .into(iv);
        }, iv);
    }

    public static boolean isValidContextForGlide(final Context context) {
        return context != null;
    }

    public static void bindImageView(Context context, String url, ImageView iv, int placeholderId) {
        if (!isValidContextForGlide(context)) {
            return;
        }
        UiUtils.waitLayoutComplete(wasLayoutComplete -> {
            // we have to check only if the process is async.
            // wasLayoutComplete true : sync, wasLayoutComplete false : async.
            if (!isValidContextForGlide(context)) {
                return;
            }
            Glide.with(context).load(url)
                    .apply(createPlaceHolder(placeholderId))
                    .apply(getRequestOptionsHooked(iv))
                    .apply(RequestOptions.noAnimation())
                    .into(iv);
        }, iv);
    }

    public static RequestOptions getDefaultRequestOptions() {
        return RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE);
    }

    private interface RequestManagerGetter {
        RequestManager createRequestManager();
    }
}
