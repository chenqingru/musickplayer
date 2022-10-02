package com.seed.dialog.inter;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.seed.dialog.CommonDialogWrapper;

public interface ICommonDialogEventBinderListener {
    /**
     * @param yourView            响应点击事件的View
     * @param commonDialogWrapper 该组件所在的dialog
     * @param bundle              dialog所绑定的数据源
     */
    void OnClickListener(View yourView, CommonDialogWrapper commonDialogWrapper, Bundle bundle);

    /**
     * 图片加载回调
     */
/*    void onFinalImageSet(View yourView, @Nullable ImageInfo imageInfo, @Nullable Animatable anim);

    void onIntermediateImageSet(View yourView, ImageInfo imageInfo);*/

    void onImageLoadFailure(View yourView);
}
