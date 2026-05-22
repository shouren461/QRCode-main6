package com.drojian.qrcode.cameralib.decode;


import android.graphics.Bitmap;

import com.drojian.qrcode.baselib.ScanResultModel;

import java.util.ArrayList;

/**
 * 解析状态回调
 */
public interface CaptureListener {
    void onReStart();

    void onSuccess(ArrayList<ScanResultModel> scanResultModels, Bitmap bitmap);

    void onFailed();

    void cameraError();
}