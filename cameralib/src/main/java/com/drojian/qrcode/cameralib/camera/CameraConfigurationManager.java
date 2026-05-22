package com.drojian.qrcode.cameralib.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import com.drojian.qrcode.cameralib.view.CameraView;

/**
 * A class which deals with reading, parsing, and setting the camera parameters which are used to configure the camera hardware.
 */
final class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";

    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;
    private Point bestPreviewSize;
    @Nullable
    private final CameraConfigureCallback cameraConfigureCallback;

    CameraConfigurationManager(Context context, @Nullable CameraConfigureCallback cameraConfigureCallback) {
        this.context = context;
        this.cameraConfigureCallback = cameraConfigureCallback;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(OpenCamera camera) {
        Camera.Parameters parameters = camera.getCamera().getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        screenResolution = theScreenResolution;
        cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(context, parameters, screenResolution);
        bestPreviewSize = cameraResolution;
        if (cameraConfigureCallback != null) {
            cameraConfigureCallback.onConfigure(bestPreviewSize);
        }
    }

    void setDesiredCameraParameters(OpenCamera camera, boolean safeMode) {
        Camera theCamera = camera.getCamera();
        Camera.Parameters parameters = theCamera.getParameters();

        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }

        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }

        CameraConfigurationUtils.setFocus(parameters, true, true, safeMode);

        parameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);

        theCamera.setDisplayOrientation(90);
        theCamera.setParameters(parameters);

        Camera.Parameters afterParameters = theCamera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (bestPreviewSize.x != afterSize.width || bestPreviewSize.y != afterSize.height)) {
            Log.w(TAG, "Camera said it supported preview size " + bestPreviewSize.x + 'x' + bestPreviewSize.y +
                    ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            bestPreviewSize.x = afterSize.width;
            bestPreviewSize.y = afterSize.height;
        }
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    boolean getTorchState(Camera camera) {
        try {
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                if (parameters != null) {
                    String flashMode = parameters.getFlashMode();
                    return Camera.Parameters.FLASH_MODE_ON.equals(flashMode) ||
                            Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void setTorch(Camera camera, boolean newSetting) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            doSetTorch(parameters, newSetting, false);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSetTorch(Camera.Parameters parameters, boolean isTorchOn, boolean isSafeMode) {
        CameraConfigurationUtils.setTorch(parameters, isTorchOn);
        if (!isSafeMode) {
            CameraConfigurationUtils.setBestExposure(parameters, isTorchOn);
        }
    }

}
