package com.drojian.qrcode.utillib.listener;

/**
 * @author yangfengfan@drojian.dev
 */
public interface TwoStateListener {
    default void onPositive() {
    }

    default void onNegative() {
    }
}
