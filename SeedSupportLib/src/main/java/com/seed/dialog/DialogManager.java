package com.seed.dialog;

import android.text.TextUtils;
import android.util.Log;


import java.util.HashMap;
import java.util.LinkedList;

/***
 * create by ljs on on 18/11/21
 */
public class DialogManager {

    private static final String TAG = "DialogManager";
    private LinkedList<CommonDialogWrapper> mDialogQueue = new LinkedList<CommonDialogWrapper>();//dialog的队列
    private HashMap<String, Integer> mDialogTimesQueue = new HashMap<String, Integer>();
    private CommonDialogWrapper mCurrentShowDialog;
    /**
     * 屏蔽弹窗标记
     */
    private boolean shieldPopupWindow = false;

    private DialogManager() {

    }

    public static DialogManager getInstance() {
        return Holder.sInstance;
    }


    /**
     * 每次弹窗调用PushQueue方法
     *
     * @param dialogBase
     */
    public void pushToQueue(CommonDialogWrapper dialogBase) {
        //添加到队列中
        if (dialogBase != null) {
            Log.e(TAG, "add..");
            if (dialogBase.isNeedShowImmediately()) {
                mDialogQueue.addFirst(dialogBase);
            } else if (dialogBase.getShowTimes() != 0) {
                int times = mDialogTimesQueue.containsKey(dialogBase.getClassName()) ?
                        mDialogTimesQueue.get(dialogBase.getClassName()) : 0;
                if (times < dialogBase.getShowTimes()) {
                    mDialogTimesQueue.put(dialogBase.getClassName(), ++times);
                    mDialogQueue.add(dialogBase);
                }
            } else {
                mDialogQueue.add(dialogBase);
                //只有当前队列数量为1时才能进行下一步操作
            }

            if (mCurrentShowDialog == null || !mCurrentShowDialog.isDialogShowing()) {
                startNextIf();
            }
        }
    }

    /**
    *
     * 判断传入的tag是否存在队列中
     * @return
    */
    public boolean isTagInQueue(String tag){

        if (TextUtils.isEmpty(tag)){
            return false ;
        }

        if (mDialogQueue != null && !mDialogQueue.isEmpty()) {
            for (int i = 0; i < mDialogQueue.size(); i++) {
                if (mDialogQueue.get(i) != null && TextUtils.equals(tag , mDialogQueue.get(i).getTag())){
                    return true ;
                }
            }
        }
        return false ;
    }

    /**
     * 当前正在显示弹框是否为传入的tag
     * @param tag
     * @return
     */
    public boolean isCurrentDialogEqualTag(String tag){

        if (TextUtils.isEmpty(tag)){
            return false ;
        }

        if ( mCurrentShowDialog != null && mCurrentShowDialog.isDialogShowing() ){
            return TextUtils.equals(tag , mCurrentShowDialog.getTag());
        }
        return false ;
    }

    /**
     * 显示下一个弹窗任务
     */
    public void startNextIf() {
        if (shieldPopupWindow) {
            return;
        }
        if (mDialogQueue != null && !mDialogQueue.isEmpty()) {
            mCurrentShowDialog = mDialogQueue.removeFirst();
            if (mCurrentShowDialog != null) {
                mCurrentShowDialog.showDialog();
            } else {
                Log.e(TAG, "任务队列为空...");
            }
        }
    }



    /**
     * 清除对象
     */
    public void clear() {
        mDialogQueue.clear();
        if (mCurrentShowDialog != null) {
            mCurrentShowDialog.disMissDialog();
            mCurrentShowDialog = null;
        }
    }

    /**
     * 清除某个在队列里但是有问题的dialog
     */
    public void clearCurrentDialog() {
        try {
            mDialogQueue.poll();
            if (mCurrentShowDialog != null) {
                mCurrentShowDialog.disMissDialog();
                mCurrentShowDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void removeDialogFromQueue(CommonDialogWrapper commonDialogWrapper) {
        if (mDialogQueue != null && commonDialogWrapper != null) {
            mDialogQueue.remove(commonDialogWrapper);
        }
    }

    /**
     * 屏蔽弹窗
     * <p>
     * 注意：正在显示的将不会再显示
     * 在合适的时机请取消屏蔽弹窗
     *
     */
    public void shieldPopupWindow() {
        shieldPopupWindow = true;
        if (mCurrentShowDialog != null) {
            mCurrentShowDialog.disMissDialog();
        }
    }

    /**
     * 取消屏蔽弹窗
     * <p>
     * 注意：正在显示的将不会再显示
     */
    public void cancelShieldPopupWindow() {
        shieldPopupWindow = false;
        startNextIf();
    }

    private static class Holder {
        public static DialogManager sInstance = new DialogManager(); // This will be lazily initialised
    }
}
