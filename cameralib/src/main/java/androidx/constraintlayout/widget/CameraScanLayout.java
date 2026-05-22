package androidx.constraintlayout.widget;

import static android.view.ScaleGestureDetector.OnScaleGestureListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 相机扫描布局
 */
public class CameraScanLayout extends ConstraintLayout implements OnScaleGestureListener {

    public static final int MAX_SCALE_VALUE = 2;
    public static final int MIN_SCALE_VALUE = 0;
    private final ScaleGestureDetector scaleGestureDetector;
    private OnScaleChangeListener scaleChangeListener;
    private final GestureDetector gestureDetector;
    private float mPreScale = 0;
    private float mScale = 0;


    public CameraScanLayout(Context context) {
        this(context, null);
    }

    public CameraScanLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraScanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // 双击恢复默认缩放
                if (scaleChangeListener != null) {
                    scaleChangeListener.onScaleChanged(0);
                }
                return super.onDoubleTap(e);
            }
        });
    }

    public void setScaleChangeListener(OnScaleChangeListener listener) {
        this.scaleChangeListener = listener;
    }

    public void resetScale() {
        mScale = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (scaleChangeListener != null) {
                scaleChangeListener.onActionUp();
            }
        }
        gestureDetector.onTouchEvent(event);
        return scaleGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float curScale = detector.getScaleFactor();
        mScale += curScale - mPreScale;
        if (mScale > MAX_SCALE_VALUE) {
            mScale = MAX_SCALE_VALUE;
        }
        if (mScale < MIN_SCALE_VALUE) {
            mScale = MIN_SCALE_VALUE;
        }
        mPreScale = curScale;
        if (scaleChangeListener != null) {
            scaleChangeListener.onScaleChanged(mScale);
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mPreScale = 1.0f;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    public interface OnScaleChangeListener {
        void onScaleChanged(float curScale);

        void onActionUp();
    }

}
