package com.drojian.qrcode.cameralib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Message;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.drojian.qrcode.cameralib.camera.CameraManager;
import com.drojian.qrcode.cameralib.camera.CameraConfigureCallback;
import com.drojian.qrcode.cameralib.decode.CaptureDecodeHandler;
import com.drojian.qrcode.cameralib.decode.CaptureListener;
import com.drojian.qrcode.cameralib.view.CameraView;
import com.drojian.qrcode.cameralib.view.UseSize;
import com.drojian.qrcode.scanlib.QRScanConfig;

/**
 * @author yangfengfan 2020-11-30
 */
public class CameraHelper {
    private final String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};

    private final Activity activity;
    private CameraManager cameraManager;
    @Nullable
    private final CameraView cameraView;
    private final CaptureListener captureListener;
    private CaptureDecodeHandler decodeHandler;
    private final QRScanConfig qrScanConfig;

    private Long initCameraExceptionTimestamp = null;
    private Boolean initCameraExceptionHasCameraPermission = null;
    private int retryOpenCameraTimes = 0; // 出错时重新打开相机次数

    private Thread delayInitCameraThread = null;
    private Thread delayPauseCameraThread = null;
    private boolean isCameraThreadProcessing = false;
    private int lastCameraOperateState = -1;
    private final int CAMERA_OPERATE_INIT = 11;
    private final int CAMERA_OPERATE_PAUSE = 12;
    private Boolean isCallShowingBoolean = null;


    public CameraHelper(Activity activity, @Nullable CameraView cameraView, CaptureListener captureListener, QRScanConfig qrScanConfig) {
        this.activity = activity;
        this.captureListener = captureListener;
        this.cameraManager = new CameraManager(activity, cameraConfigureCallback);
        this.cameraView = cameraView;
        this.qrScanConfig = qrScanConfig;
    }

    public CaptureDecodeHandler getDecodeHandler() {
        return decodeHandler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void startCamera(Boolean isShowing) {
        isCallShowingBoolean = isShowing;
        lastCameraOperateState = CAMERA_OPERATE_INIT;
        if (isCameraThreadProcessing) {
            return;
        }

        if (isCallShowingBoolean == Boolean.TRUE) {
            delayInitCameraThread = new Thread(() -> {
                try {
                    if (delayPauseCameraThread != null) {
                        delayPauseCameraThread.join();
                    }
                    cameraManager = new CameraManager(activity, cameraConfigureCallback);
                    if (cameraView != null && cameraView.getHolder() != null) {
                        initCamera(cameraView.getHolder());
                    }
                    isCameraThreadProcessing = false;
                    finishedCameraThread(CAMERA_OPERATE_INIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "delay_init_camera_thread");

            try {
                isCameraThreadProcessing = true;
                delayInitCameraThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopCamera(Boolean isShowing) {
        isCallShowingBoolean = isShowing;
        lastCameraOperateState = CAMERA_OPERATE_PAUSE;
        if (isCameraThreadProcessing) {
            return;
        }

        delayPauseCameraThread = new Thread(() -> {
            try {
                if (delayInitCameraThread != null) {
                    delayInitCameraThread.join();
                }
                if (decodeHandler != null) {
                    decodeHandler.quitSynchronously();
                    decodeHandler = null;
                }
                if (isCallShowingBoolean != Boolean.TRUE) {
                    CameraManager var1 = cameraManager;
                    if (var1 != null) {
                        var1.closeDriver();
                    }
                    cameraManager = null;
                }
                isCameraThreadProcessing = false;
                finishedCameraThread(CAMERA_OPERATE_PAUSE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "delay_pause_camera_thread");
        try {
            isCameraThreadProcessing = true;
            delayPauseCameraThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化相机
     */
    private boolean initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null || cameraManager == null || cameraManager.isOpen()) {
            return false;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (decodeHandler == null && activity != null) {
                decodeHandler = new CaptureDecodeHandler(activity.getMainLooper(), activity, cameraManager, null, null, null, captureListener, qrScanConfig);
            }
            Message message = Message.obtain(decodeHandler, R.id.decode_succeeded, null);
            decodeHandler.sendMessage(message);
        } catch (Exception e) {
            //先判断是否有相机权限
            if (initCameraExceptionHasCameraPermission == null) {
                try {
                    if (activity != null && isHavePermissions(activity, PERMISSIONS_CAMERA)) {
                        initCameraExceptionHasCameraPermission = true;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            //没有相机权限不操作
            if (initCameraExceptionHasCameraPermission == null || !initCameraExceptionHasCameraPermission) {
                return false;
            }

            //记录发生异常时间 并 上传埋点
            if (initCameraExceptionTimestamp == null) {
                initCameraExceptionTimestamp = System.currentTimeMillis();
            }

            //1.排除切换到其他页面情况 2.在发生异常3s时间最多重试5次，3s后仍发生异常则展示失败弹窗
            if (lastCameraOperateState == CAMERA_OPERATE_INIT) {
                try {
                    if (System.currentTimeMillis() - initCameraExceptionTimestamp < 3000 && retryOpenCameraTimes <= 5) {
                        retryOpenCameraTimes++;
                        if (!initCamera(surfaceHolder)) {
                            retryOpenCameraTimes = 0;
                            if (captureListener != null) {
                                captureListener.cameraError();
                            }
                        }
                    } else {
                        retryOpenCameraTimes = 0;
                        if (captureListener != null) {
                            captureListener.cameraError();
                        }
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }

        return true;
    }

    private static boolean isHavePermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void finishedCameraThread(int currentFinishedState) {
        if (currentFinishedState != CAMERA_OPERATE_INIT && lastCameraOperateState == CAMERA_OPERATE_INIT) {
            startCamera(isCallShowingBoolean);
        } else if (currentFinishedState != CAMERA_OPERATE_PAUSE && lastCameraOperateState == CAMERA_OPERATE_PAUSE) {
            stopCamera(isCallShowingBoolean);
        }
    }


    private final CameraConfigureCallback cameraConfigureCallback = new CameraConfigureCallback() {
        @Override
        public void onConfigure(Point bestPreviewSize) {
            if (cameraView != null) {
                cameraView.post(() -> cameraView.setCameraPreviewSize(new UseSize(bestPreviewSize.x, bestPreviewSize.y)));
            }
        }
    };
}
