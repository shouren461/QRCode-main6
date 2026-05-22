package com.drojian.qrcode.utillib.listener;

/**
 * @author yangfengfan@drojian.dev
 */
public interface DialogListenerWithResult {
    default void onPositive(String result) {
    }

    default void onNegative() {
    }

    default void onDisMiss() {
    }
}
