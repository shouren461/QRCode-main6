package com.drojian.qrcode.cameralib.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.cameralib.R;
import com.drojian.qrcode.scanlib.QRScanConfig;
import com.drojian.qrcode.scanlib.QRScanHelper;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * 解析
 */
final class DecodeHandler extends Handler {

    private final QRScanHelper qrScanHelper;
    private final CaptureDecodeHandler captureDecodeHandler;
    private final Context context;
    private boolean running = true;

    private int previewFormat;


    DecodeHandler(Context context, Map<DecodeHintType, Object> hints, CaptureDecodeHandler captureDecodeHandler, QRScanConfig qrScanConfig) {
        this.context = context;
        this.captureDecodeHandler = captureDecodeHandler;
        if (qrScanConfig == null) {
            qrScanConfig = new QRScanConfig();
        }
        qrScanHelper = new QRScanHelper(qrScanConfig);
    }

    @Override
    public void handleMessage(@NotNull Message message) {
        if (message == null || !running) {
            return;
        }
        try {
            Bundle bundle = message.getData();
            if (bundle != null) {
                previewFormat = bundle.getInt(DecodeThread.BARCODE_PREVIEW_FORMAT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        } else if (message.what == R.id.quit) {
            running = false;
            Looper myLooper = Looper.myLooper();
            if (myLooper != null) {
                myLooper.quit();
            }
        }
    }

    private void decode(byte[] data, int width, int height) {
        ArrayList<ScanResultModel> scanResultModels = new ArrayList<>();
        if (context != null) {
            try {
                scanResultModels = qrScanHelper.decode(context, data, width, height);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = new Bundle();
        try {
            PlanarYUVLuminanceSource source = (PlanarYUVLuminanceSource) captureDecodeHandler.getCameraManager().buildLuminanceSource(data, width, height).rotateCounterClockwise45();
            bundleThumbnail(source, bundle);
            dataToBitmap(data, previewFormat, width, height, bundle);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        handleDecodeResult(scanResultModels, bundle);
    }


    private void handleDecodeResult(ArrayList<ScanResultModel> scanResultModels, Bundle bundle) {
        if (captureDecodeHandler == null) {
            return;
        }
        if (scanResultModels != null && !scanResultModels.isEmpty()) {
            Message message = Message.obtain(captureDecodeHandler, R.id.decode_succeeded, scanResultModels);
            message.setData(bundle);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(captureDecodeHandler, R.id.decode_failed);
            message.setData(bundle);
            message.sendToTarget();
        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
    }

    private void dataToBitmap(byte[] data, int previewFormat, int width, int height, Bundle bundle) {
        try {
            if (previewFormat == ImageFormat.NV21 || previewFormat == ImageFormat.NV16 || previewFormat == ImageFormat.YUY2) {
                YuvImage yuvImage = new YuvImage(data, previewFormat, width, height, null);
                Rect rect = new Rect(0, 0, width, height);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(rect, 80, byteStream);
                byte[] bytes = byteStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                Matrix matrix = new Matrix();
                matrix.setRotate(90f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                bundle.putByteArray(DecodeThread.BARCODE_BITMAP_LATEST, out.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
