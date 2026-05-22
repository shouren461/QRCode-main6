package com.drojian.qrcode.cameralib.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.drojian.qrcode.baselib.CodeFormat;
import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.cameralib.R;
import com.drojian.qrcode.cameralib.camera.CameraManager;
import com.drojian.qrcode.scanlib.QRScanConfig;
import com.google.zxing.DecodeHintType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * 处理解析状态
 */
public final class CaptureDecodeHandler extends Handler {

    private final CameraManager cameraManager;
    private final DecodeThread decodeThread;
    private final CaptureListener captureListener;
    private State state;

    private Bitmap barcode = null;
    private Bitmap barcodeLatest = null;


    public CaptureDecodeHandler(Looper mainLooper, Context context, CameraManager cameraManager, Collection<CodeFormat> decodeFormats, Map<DecodeHintType, ?> baseHints,
                                String characterSet, CaptureListener captureListener, QRScanConfig qrScanConfig) {
        super(mainLooper);
        this.captureListener = captureListener;
        decodeThread = new DecodeThread(context, decodeFormats, baseHints, characterSet, new ViewfinderResultPointCallback(), this, qrScanConfig);
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        try {
            Bundle bundle = message.getData();
            if (bundle != null) {
                byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                }

                byte[] compressedBitmap2 = bundle.getByteArray(DecodeThread.BARCODE_BITMAP_LATEST);
                if (compressedBitmap2 != null) {
                    barcodeLatest = BitmapFactory.decodeByteArray(compressedBitmap2, 0, compressedBitmap2.length, null);
                    barcodeLatest = barcodeLatest.copy(Bitmap.Config.ARGB_8888, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();
            if (captureListener != null) {
                captureListener.onReStart();
            }
        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            if (message.obj == null) { //测试这里Result是否会为Null
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                if (captureListener != null) {
                    captureListener.onReStart();
                }
            } else {
                if (captureListener != null) {
                    try {
                        ArrayList<ScanResultModel> scanResultModels = (ArrayList<ScanResultModel>) message.obj;
                        if (scanResultModels.isEmpty()) {
                            state = State.PREVIEW;
                            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                            captureListener.onReStart();
                        } else {
                            captureListener.onSuccess(scanResultModels, barcode);
                        }
                    } catch (Exception e) {
                        state = State.PREVIEW;
                        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                        captureListener.onReStart();
                    }
                }
            }
        } else if (message.what == R.id.decode_failed) {// We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            if (captureListener != null) {
                captureListener.onFailed();
            }
        }
    }

    // 退出循环解析
    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message message = Message.obtain(decodeThread.getHandler(), R.id.quit);
        message.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }
        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    // 开始解析
    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        }
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public Bitmap getBarcode() {
        return barcode;
    }

    public Bitmap getBarcodeLatest() {
        return barcodeLatest;
    }

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

}
