package com.drojian.qrcode.cameralib.camera;

import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_MACRO;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * 相机自动对焦
 */
final class CameraFocusManager implements Camera.AutoFocusCallback {

    private static final String TAG = "CameraFocusManager";
    private static long AUTO_FOCUS_INTERVAL_MS = 1500L;
    private static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        FOCUS_MODES_CALLING_AF = new ArrayList<>(2);
        FOCUS_MODES_CALLING_AF.add(FOCUS_MODE_AUTO);
        FOCUS_MODES_CALLING_AF.add(FOCUS_MODE_MACRO);
    }

    private boolean stopped;
    private boolean focusing;
    private boolean useAutoFocus;
    private final Camera camera;
    private AsyncTask<?, ?, ?> outstandingTask;
    private int isSwitch = 2;

    CameraFocusManager(Camera camera) {
        this.camera = camera;
        String currentFocusMode = "";
        try {
            currentFocusMode = camera.getParameters().getFocusMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
        start();
    }

    @Override
    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        try {
            focusing = false;
            if (isSwitch >= 2) {
                setFocusMode(camera.getParameters());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSwitch >= 2 && !success) {
            isSwitch = 0;
        } else {
            isSwitch++;
        }
        autoFocusAgainLater();
    }

    private synchronized void autoFocusAgainLater() {
        if (!stopped && outstandingTask == null) {
            AutoFocusTask newTask = new AutoFocusTask();
            try {
                newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                outstandingTask = newTask;
            } catch (RejectedExecutionException ree) {
                Log.w(TAG, "Could not request auto focus", ree);
            }
        }
    }

    synchronized void start() {
        try {
            if (!useAutoFocus) {
                resetFocusMode(camera.getParameters());
                useAutoFocus = FOCUS_MODES_CALLING_AF.contains(camera.getParameters().getFocusMode());
            }
            if (focusing) {
                try {
                    camera.cancelAutoFocus();
                    focusing = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (useAutoFocus) {
                outstandingTask = null;
                if (!stopped && !focusing) {
                    try {
                        camera.autoFocus(this);
                        focusing = true;
                    } catch (RuntimeException re) {
                        re.printStackTrace();
                        autoFocusAgainLater();
                    }
                }
            } else {
                onAutoFocus(false, camera);
            }
        } catch (Exception e) {
            autoFocusAgainLater();
        }
    }

    private synchronized void cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
                outstandingTask.cancel(true);
            }
            outstandingTask = null;
        }
    }

    synchronized void stop() {
        try {
            stopped = true;
            resetFocusMode(camera.getParameters());
            if (useAutoFocus) {
                cancelOutstandingTask();
                try {
                    camera.cancelAutoFocus();
                } catch (RuntimeException re) {
                }
            }
        } catch (Exception e) {
        }
    }

    public void onShotAutoFocus() {
        if (focusing) {
            try {
                camera.cancelAutoFocus();
                focusing = false;
                resetFocusMode(camera.getParameters());
            } catch (Exception e) {
            }
        }
        if (!stopped && !focusing) {
            try {
                camera.autoFocus(this);
                focusing = true;
            } catch (RuntimeException re) {
            }
        }
    }

    private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... voids) {
            try {
                Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
            } catch (InterruptedException e) {
                // continue
            }
            try {
                if (camera != null) {
                    resetFocusMode(camera.getParameters());
                }
            } catch (Exception e) {
            }
            start();
            return null;
        }
    }


    public void resetFocusMode(Camera.Parameters parameters) {
        if (!stopped) {
            try {
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.contains(FOCUS_MODE_AUTO)) {
                    boolean supportAutoFocus = true;
                    List<Camera.Area> singletonList = Collections.singletonList(new Camera.Area(new Rect(-350, -350, 350, 350), 1000));

                    if (parameters.getMaxNumFocusAreas() != 0) {
                        String focusMode = parameters.getFocusMode();
                        boolean noFocusMode = (!focusMode.equals(FOCUS_MODE_AUTO) && !focusMode.equals(FOCUS_MODE_MACRO) &&
                                !focusMode.equals(FOCUS_MODE_CONTINUOUS_PICTURE) && !focusMode.equals(FOCUS_MODE_CONTINUOUS_VIDEO));
                        if (focusMode == null || noFocusMode) {
                            supportAutoFocus = false;
                        }
                        if (supportAutoFocus) {
                            parameters.setFocusMode(FOCUS_MODE_AUTO);
                            parameters.setFocusAreas(singletonList);
                            if (parameters.getMaxNumMeteringAreas() <= 0) {
                                return;
                            }
                            parameters.setMeteringAreas(singletonList);
                        }
                    }
                    if (parameters.getMaxNumMeteringAreas() > 0) {
                        parameters.setFocusMode(FOCUS_MODE_AUTO);
                        parameters.setFocusAreas(singletonList);
                        parameters.setMeteringAreas(singletonList);
                    }
                }
                camera.cancelAutoFocus();
                camera.setParameters(parameters);
            } catch (Exception e) {
            }
        }
    }

    private void setFocusMode(Camera.Parameters parameters) {
        if (!stopped) {
            camera.setParameters(parameters);
            try {
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes != null) {
                    if (supportedFocusModes.contains(FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (supportedFocusModes.contains(FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(FOCUS_MODE_CONTINUOUS_VIDEO);
                    } else if (supportedFocusModes.contains(FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(FOCUS_MODE_AUTO);
                    } else if (supportedFocusModes.contains(FOCUS_MODE_MACRO)) {
                        parameters.setFocusMode(FOCUS_MODE_MACRO);
                    }
                }
                camera.cancelAutoFocus();
                camera.setParameters(parameters);
            } catch (Exception e) {
            }
        }
    }

}
