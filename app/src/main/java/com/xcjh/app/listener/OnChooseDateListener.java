package com.xcjh.app.listener;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author：luck
 * @date：2020-01-13 17:58
 * @describe：长按事件
 */
public interface OnChooseDateListener {
    void onDismiss();
    void onSure(String time);

}
