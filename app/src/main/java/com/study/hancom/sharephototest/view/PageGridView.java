package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.study.hancom.sharephototest.view.base.CustomGridView;

public class PageGridView extends CustomGridView {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    private Point mTouchPointOffset = new Point(0, 0);
    private ImageView mFloatingItemView;

    private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return drawFloatingItemView();
        }
    };
    private OnItemDragListener mOnItemDragListener = new OnItemDragListener() {
        @Override
        public boolean onItemDrag(View view, int x, int y, int rawX, int rawY) {
            return moveFloatingItemView(rawX, rawY);
        }
    };
    private OnItemDropListener mOnItemDropListener = new OnItemDropListener() {
        @Override
        public boolean onItemDrop(View view, int fromPosition, int toPosition, int toRawX, int toRawY) {
            return dropFloatingItemView();
        }
    };
    private OnItemCancelListener mOnItemCancelListener = new OnItemCancelListener() {
        @Override
        public void onItemCancel(View view, int x, int y) {
            removeFloatingItemView();
        }
    };

    public PageGridView(Context context) {
        this(context, null);
    }
    public PageGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public PageGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context)
    {
        Log.v("tag", "init");
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.format = PixelFormat.TRANSLUCENT;

        setOnItemLongClickListener(mOnItemLongClickListener);
        setOnItemDragListener(mOnItemDragListener);
        setOnItemDropListener(mOnItemDropListener);
        setOnItemCancelListener(mOnItemCancelListener);
    }

    public boolean drawFloatingItemView()
    {
        if (mSelectedItemPosition < 0) {
            return false;
        }

        View item = getChildAt(mSelectedItemPosition - getFirstVisiblePosition()); //  getFirstVisiblePosition()이 없으면 스크롤 시 문제 생김
        item.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

        ImageView tempFloatingItemView = new ImageView(getContext());
        tempFloatingItemView.setBackgroundColor(Color.parseColor("#FFE400"));
        tempFloatingItemView.setImageBitmap(bitmap);

        mTouchPointOffset.x = (int) (item.getWidth() * 0.5);
        mTouchPointOffset.y = (int) (item.getHeight() * 0.5);

        mWindowParams.x = mTouchPoint.x - mTouchPointOffset.x;
        mWindowParams.y = mTouchPoint.y - mTouchPointOffset.y;
        mWindowManager.addView(tempFloatingItemView, mWindowParams);
        mFloatingItemView = tempFloatingItemView;

        return true;
    }

    public boolean moveFloatingItemView(int rawX, int rawY)
    {
        if (mFloatingItemView == null) {
            return false;
        }

        mWindowParams.x = rawX - mTouchPointOffset.x;
        mWindowParams.y = rawY - mTouchPointOffset.y;
        mWindowManager.updateViewLayout(mFloatingItemView, mWindowParams);

        return true;
    }

    private boolean dropFloatingItemView()
    {
        if (mFloatingItemView == null) {
            return false;
        }

        removeFloatingItemView();

        return true;
    }

    public boolean removeFloatingItemView()
    {
        if (mFloatingItemView == null) {
            return false;
        }

        mWindowManager.removeView(mFloatingItemView);
        mFloatingItemView = null;

        return true;
    }
}
