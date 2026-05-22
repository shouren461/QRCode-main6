package com.drojian.qrcode.utillib.listener;

/**
 * @author yangfengfan@drojian.dev
 */
public interface DialogListener {
    default void onPositive() {
    }

    default void onNegative() {
    }

    default void onDisMiss() {
    }
}
