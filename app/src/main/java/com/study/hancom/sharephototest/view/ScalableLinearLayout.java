package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;

public class ScalableLinearLayout extends LinearLayout {
    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private Mode mMode = Mode.NONE;
    private float mScaleFactor = 1.f;
    private float mLastScaleFactor = 0f;

    private float mStartX = 0f;
    private float mStartY = 0f;

    private float mDistanceX = 0f;
    private float mDistanceY = 0f;
    private float mPrevDistanceX = 0f;
    private float mPrevDistanceY = 0f;

    private OnTouchListener mOnTouchListener;
    private ScaleGestureDetector mScaleGestureDetector;

    public ScalableLinearLayout(Context context) {
        this(context, null);
    }

    public ScalableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScalableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        mOnTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (mScaleFactor > MIN_ZOOM) {
                            mMode = Mode.DRAG;
                            mStartX = motionEvent.getX() - mPrevDistanceX;
                            mStartY = motionEvent.getY() - mPrevDistanceY;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mMode == Mode.DRAG) {
                            mDistanceX = motionEvent.getX() - mStartX;
                            mDistanceY = motionEvent.getY() - mStartY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mMode = Mode.ZOOM;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mMode = Mode.DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                        mMode = Mode.NONE;
                        mPrevDistanceX = mDistanceX;
                        mPrevDistanceY = mDistanceY;
                        break;
                }
                mScaleGestureDetector.onTouchEvent(motionEvent);

                if ((mMode == Mode.DRAG && mScaleFactor >= MIN_ZOOM) || mMode == Mode.ZOOM) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float maxDx = (child().getWidth() - (child().getWidth() / mScaleFactor)) / 2 * mScaleFactor;
                    float maxDy = (child().getHeight() - (child().getHeight() / mScaleFactor)) / 2 * mScaleFactor;
                    mDistanceX = Math.min(Math.max(mDistanceX, -maxDx), maxDx);
                    mDistanceY = Math.min(Math.max(mDistanceY, -maxDy), maxDy);
                    applyScaleAndTranslation();
                }

                return true;
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mOnTouchListener.onTouch(this, ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mOnTouchListener.onTouch(this, ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScaleFactor, mScaleFactor);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private View child() {
        return getChildAt(0);
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(mScaleFactor);
        child().setScaleY(mScaleFactor);
        child().setTranslationX(mDistanceX);
        child().setTranslationY(mDistanceY);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = mScaleGestureDetector.getScaleFactor();
            if (mLastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(mLastScaleFactor))) {
                mScaleFactor *= scaleFactor;
                mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
                mLastScaleFactor = scaleFactor;
            } else {
                mLastScaleFactor = 0;
            }
            return true;
        }
    }
}
