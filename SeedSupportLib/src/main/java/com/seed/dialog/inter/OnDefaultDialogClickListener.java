package com.seed.dialog.inter;

import android.os.Bundle;

import com.seed.dialog.CommonDialogWrapper;

public interface OnDefaultDialogClickListener {
    void OnLeftButtonClickListener(CommonDialogWrapper commonDialogWrapper, Bundle bundle);

    void OnRightButtonClickListener(CommonDialogWrapper commonDialogWrapper, Bundle bundle);
}
