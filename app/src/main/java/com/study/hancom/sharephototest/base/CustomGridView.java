package com.study.hancom.sharephototest.base;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

public class CustomGridView extends GridView {

    protected Point mTouchPoint = new Point(0, 0);
    protected int mSelectedItemPosition = -1;

    private OnItemDragListener mOnItemDragListener;
    private OnItemDropListener mOnItemDropListener;
    private OnItemCancelListener mOnItemCancelListener;

    public CustomGridView(Context context) {
        this(context, null);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        int x = (int) e.getX();
        int y = (int) e.getY();

        int rawX = (int) e.getRawX();
        int rawY = (int) e.getRawY();

        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mTouchPoint.set(rawX, rawY);
                mSelectedItemPosition = pointToPosition(x, y);
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        int x = (int) e.getX();
        int y = (int) e.getY();

        int rawX = (int) e.getRawX();
        int rawY = (int) e.getRawY();

        switch (e.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (mOnItemDragListener != null) {
                    return mOnItemDragListener.onItemDrag(this, x, y, rawX, rawY);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mOnItemDropListener != null) {
                    int dropPosition = getItemPosition(x, y);
                    return mOnItemDropListener.onItemDrop(this, mSelectedItemPosition, dropPosition, rawX, rawY);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mOnItemCancelListener != null) {
                    mOnItemCancelListener.onItemCancel(this, x, y);
                }
                break;

            default:
                break;
        }

        return super.onTouchEvent(e);
    }

    public int getItemPosition(int x, int y) {
        return pointToPosition(x, y);
    }

    public void setOnItemDragListener(OnItemDragListener listener)
    {
        mOnItemDragListener = listener;
    }

    public void setOnItemDropListener(OnItemDropListener listener)
    {
        mOnItemDropListener = listener;
    }

    public void setOnItemCancelListener(OnItemCancelListener listener)
    {
        mOnItemCancelListener = listener;
    }

    /* 리스너 인터페이스 */
    public interface OnItemDragListener
    {
        boolean onItemDrag(View view, int x, int y, int rawX, int rawY);
    }

    public interface OnItemDropListener
    {
        boolean onItemDrop(View view, int fromPosition, int toPosition, int toRawX, int toRawY);
    }

    public interface OnItemCancelListener
    {
        void onItemCancel(View view, int x, int y);
    }
}