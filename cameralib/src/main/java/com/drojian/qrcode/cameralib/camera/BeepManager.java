package com.drojian.qrcode.cameralib.camera;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.drojian.qrcode.cameralib.R;

/**
 * 提示音和震动
 */
public final class BeepManager {
    private static final long VIBRATE_DURATION = 150L; //震动时长
    private final Context context;
    private SoundPool soundPool;
    private boolean playBeep;
    private boolean vibrate;
    private int soundId;
    private boolean isBeepLoadComplete = false;

    public BeepManager(Context context, boolean isBeep) {
        this(context, isBeep, true);
    }

    public BeepManager(Context context, boolean isBeep, boolean isVibrate) {
        this.context = context;
        updatePrefs(isBeep, isVibrate);
    }

    public synchronized void updatePrefs(boolean isBeep, boolean isVibrate) {
        playBeep = isBeep;
        vibrate = isVibrate;
        if (playBeep && soundPool == null) {
            try {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
                soundPool = new SoundPool.Builder()
                        .setMaxStreams(1)
                        .setAudioAttributes(attributes)
                        .build();

                soundId = soundPool.load(context, R.raw.beep, 1);
                soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                    if (status == 0) {
                        isBeepLoadComplete = true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void playBeepSoundAndVibrate() {
        if (playBeep && soundPool != null && isBeepLoadComplete) {
            soundPool.play(soundId, 1, 1, 1, 0, 1);
        } else if (playBeep) {
            try {
                SoundPool soundPool;
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
                soundPool = new SoundPool.Builder()
                        .setMaxStreams(1)
                        .setAudioAttributes(attributes).build();

                final int voiceId = soundPool.load(context, R.raw.beep, 1);
                soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
                    if (status == 0) {
                        soundPool1.play(voiceId, 1, 1, 1, 0, 1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 震动
        if (vibrate) {
            try {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VIBRATE_DURATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void close() {
        if (soundPool != null) {
            try {
                soundPool.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            soundPool = null;
        }
    }

}
