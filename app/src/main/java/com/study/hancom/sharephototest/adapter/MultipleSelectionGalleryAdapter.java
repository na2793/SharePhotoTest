package com.study.hancom.sharephototest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.GalleryFullSizePictureActivity;
import com.study.hancom.sharephototest.adapter.base.GalleryAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultipleSelectionGalleryAdapter extends GalleryAdapter {
    private static final int REQUEST_CODE = 1;

    private Set<Integer> mSelectedPositionSet = new HashSet<>();
    
    private OnMultipleItemSelectListener mOnMultipleItemSelectListener;

    public MultipleSelectionGalleryAdapter(Context context, ArrayList<String> picturePaths) {
        super(context, picturePaths);
    }

    @Override
    protected void bindView(GalleryAdapter.ViewHolder holder, final int position) {
        if (mSelectedPositionSet.contains(position)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSelectedPositionSet.remove(position)) {
                    mSelectedPositionSet.add(position);
                }

                mOnMultipleItemSelectListener.onSelect();
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GalleryFullSizePictureActivity.class);
                intent.putStringArrayListExtra("picturePathList", mPicturePathList);
                intent.putIntegerArrayListExtra("selectedPicturePositionList", new ArrayList<>(mSelectedPositionSet));
                intent.putExtra("currentPictureIndex", position);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    public void addSelectedPosition(int position) {
        mSelectedPositionSet.add(position);
    }

    public void deselectAll() {
        mSelectedPositionSet.clear();
    }

    public int getSelectedPositionCount() {
        return mSelectedPositionSet.size();
    }

    public ArrayList<Integer> getAllSelectedPosition() {
        return new ArrayList<>(mSelectedPositionSet);
    }

    public void setOnMultipleItemSelectListener(OnMultipleItemSelectListener listener) {
        mOnMultipleItemSelectListener = listener;
    }

    public interface OnMultipleItemSelectListener {
        void onSelect();
    }
}
