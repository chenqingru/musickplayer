package com.seed.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;


import com.seed.dialog.bean.CustomDialogBinderBean;
import com.seed.dialog.inter.ICommonDialogEventBinderListener;
import com.seed.support.R;

import java.util.List;

public class CommonDialogWrapper implements DialogInterface.OnDismissListener, LifecycleObserver {
    public static final String BUNDLE_DATA_KEY_JUMP_ACTION = "BUNDLE_DATA_KEY_JUMP_ACTION";
    public static final String BUNDLE_DATA_KEY_INVITE_FRIENDS_TYPE = "BUNDLE_DATA_KEY_INVITE_FRIENDS_TYPE";
    private Context mContext;
    private Dialog mDialog = null;
    private boolean mIsNeedShowImmediately;
    private int mShowTimes;
    private Bundle mBundle;
    /**
     * 底部虚拟按键的高度，用于Dialog位于屏幕底部时导致显示重叠问题
     * 导致该问题的原因： fullScreen 中 Window.getDecorView获取的是整个屏幕的宽高，修改设置bottom
     */
    private int mNavigationBarHeight ;
    public CommonDialogWrapper(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        this.mDialog = dialog;
        mDialog.setOnDismissListener(this);
    }

    public CommonDialogWrapper(Context context) {
        if (context == null) {
            return;
        }
        this.mContext = context;
        this.mDialog = new Dialog(context, R.style.QuitDialog);
        mDialog.setOnDismissListener(this);
    }

    public boolean isNeedShowImmediately() {
        return mIsNeedShowImmediately;
    }

    /**
     * 应该调用 DialogManager.getInstance().pushToQueue(); 不建议手动掉这个方法
     */
    public void showDialog() {
        try {
            mDialog.show();
            if (mContext instanceof LifecycleOwner) {
                ((LifecycleOwner) mContext).getLifecycle().removeObserver(this);
                ((LifecycleOwner) mContext).getLifecycle().addObserver(this);
            }
        } catch (Exception e) {
            DialogManager.getInstance().clearCurrentDialog();
        }
    }

    public void disMissDialog() {
        try {
            if (mDialog != null) {
                if (mContext instanceof LifecycleOwner) {
                    ((LifecycleOwner) mContext).getLifecycle().removeObserver(this);
                }
                mDialog.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean isDialogShowing() {
        return mDialog.isShowing();
    }

    public Dialog getRealDialog() {
        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        DialogManager.getInstance().startNextIf();
    }


    /*------------------------------------------公共方法start-----------------------------------------------------------*/

    /***
     * 设置dialog弹出的的次数,默认0不限制弹出次数
     * @param showTimes
     * @return
     */
    public CommonDialogWrapper setShowTimes(int showTimes) {
        this.mShowTimes = showTimes < 0 ? 0 : showTimes;
        return this;
    }

    public int getShowTimes() {
        return this.mShowTimes;
    }

    public String getClassName() {
        return this.mDialog.getClass().getName();
    }

    public CommonDialogWrapper isNeedShowImmediately(boolean isNeedShowImmediately) {
        this.mIsNeedShowImmediately = isNeedShowImmediately;
        return this;
    }

    public CommonDialogWrapper setCanDismissOutside(boolean isCanceledOnTouchOutside) {
        mDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        return this;
    }

    public CommonDialogWrapper setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
        return this;
    }

    public CommonDialogWrapper bindData(Bundle bundle) {
        this.mBundle = bundle;
        return this;
    }

    public CommonDialogWrapper setNavigationBarHeight(int  navigationBarHeight) {
        this.mNavigationBarHeight = navigationBarHeight;
        return this;
    }

    public CommonDialogWrapper fullScreen() {
        Window win = mDialog.getWindow();
        win.setBackgroundDrawableResource(android.R.color.transparent);//去除dialog黑边
        win.getDecorView().setPadding(0, 0, 0, mNavigationBarHeight);//dialog默认有padding，需设置为0，否则无法全屏
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        transparencyBar();
        return this;
    }

    @TargetApi(19)
    private void transparencyBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mDialog.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = mDialog.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /*------------------------------------------自定义样式start-----------------------------------------------------------*/

    public CommonDialogWrapper builderCustomDialog(int layoutResID, List<CustomDialogBinderBean> eventBinderList) {
        if (layoutResID == 0) {
            return this;
        }
        mDialog.setContentView(layoutResID);
        if (eventBinderList != null && !eventBinderList.isEmpty()) {
            for (CustomDialogBinderBean customDialogBinderBean : eventBinderList) {
                final ICommonDialogEventBinderListener listener = customDialogBinderBean.listener;
                final int resId = customDialogBinderBean.resId;
                CharSequence textString = customDialogBinderBean.textString;
                if (resId != 0) {
                    final View view = mDialog.findViewById(resId);
                    //如果有Listener就添加点击回调
                    if (listener != null && view != null) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.OnClickListener(v, CommonDialogWrapper.this, mBundle);
                            }
                        });
                    }
                    //如果是textView并且有传来的字符串则添加
                    if (view instanceof TextView && !TextUtils.isEmpty(textString)) {
                        ((TextView) view).setText(textString);
                    }

                    //如果是textView并且有传来的字符串则添加
                    /*if (view instanceof WubaDraweeView && !TextUtils.isEmpty(textString)) {
                        if (textString.toString().startsWith("http")) {
                            try {
                                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                        .setControllerListener(new CommonDialogPicLoadListener(view, listener))
                                        .setUri(textString.toString())
                                        // other setters
                                        .build();
                                ((WubaDraweeView) view).setController(controller);
                            } catch (Exception e) {
                                TLog.e(e);
                            }
                        }
                    }*/
                }
            }
        }
        return this;
    }

    /**
     * 查找view
     *
     * @param layoutResID
     * @return
     */
    public View findViewById(int layoutResID) {
        if (mDialog == null || layoutResID == 0) {
            return null;
        }
        return mDialog.findViewById(layoutResID);
    }

    /**
     * 首页红包/Back红包设置弹出tag，如果tag不为null，表示当前是首次弹窗红包或者back红包，本次back不应该弹窗啦，后台已经区分一天只能弹出一次，
     * 但为了以后server修改一天可以弹出一次以上，也是不用做修改的。保证当前正在界面显示或者在队列中的对于首页及back时有且只有一个，不会重复弹窗
     */
    private String tag ;
    public CommonDialogWrapper setTag(String tag){
        this.tag = tag ;
        return this ;
    }
    public String getTag(){
        return tag ;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        disMissDialog();
    }
}
