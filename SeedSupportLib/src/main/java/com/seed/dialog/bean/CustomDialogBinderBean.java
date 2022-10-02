package com.seed.dialog.bean;


import com.seed.dialog.inter.ICommonDialogEventBinderListener;

public class CustomDialogBinderBean {
    public CustomDialogBinderBean(int resId, CharSequence textString) {
        this.resId = resId;
        this.textString = textString;
    }

    /**
     *
     * @param resId 要绑定的组件
     * @param textString 要绑定的数据，如果组件是textView,那么就把textString作为内容设置给textView。如果是wubaDreewView则把他作为图片地址传入
     * @param listener
     */
    public CustomDialogBinderBean(int resId, CharSequence textString, ICommonDialogEventBinderListener listener) {
        this.resId = resId;
        this.textString = textString;
        this.listener = listener;
    }

    public int resId;
    public CharSequence textString;
    public ICommonDialogEventBinderListener listener;
}
