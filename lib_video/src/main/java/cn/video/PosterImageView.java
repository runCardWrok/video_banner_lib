package cn.video;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class PosterImageView extends ImageView {
    public static final String TAG = "PosterImageView";

    private ImgGestureDetectorListener listener;

    public PosterImageView(Context context) {
        super(context);
    }

    public PosterImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PosterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void setListener(ImgGestureDetectorListener listener) {
        this.listener = listener;
    }

    /**
     * 双击
     */
    protected GestureDetector gestureDetector = new GestureDetector(getContext().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (listener != null) {
                Log.d(TAG, "doublClick [" + this.hashCode() + "] ");
                listener.onDoubleTap();
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed [" + this.hashCode() + "] ");

            if (listener != null) {
                listener.onSingleTapConfirmed();
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (listener != null) {
                listener.onLongPress();
            }

            super.onLongPress(e);
        }
    });

}
