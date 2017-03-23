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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultipleSelectionGalleryAdapter extends com.study.hancom.sharephototest.adapter.base.GalleryAdapter {
    private static final int REQUEST_CODE = 1;

    private Set<Integer> mSelectedPositionSet = new HashSet<>();
    
    private OnMultipleItemSelectListener mOnMultipleItemSelectListener;

    public MultipleSelectionGalleryAdapter(Context context, ArrayList<String> picturePaths) {
        super(context, picturePaths);
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

    public Integer[] getAllSelectedPosition() {
        return mSelectedPositionSet.toArray(new Integer[mSelectedPositionSet.size()]);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ImageView imageView = (ImageView) view.findViewById(R.id.gallery_image);
        Button button = (Button) view.findViewById(R.id.show_clicked_Image);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.gallery_selected_image);

        if (mSelectedPositionSet.contains(position)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSelectedPositionSet.remove(position)) {
                    mSelectedPositionSet.add(position);
                }

                mOnMultipleItemSelectListener.onSelect();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GalleryFullSizePictureActivity.class);
                intent.putStringArrayListExtra("picturePathList", mPicturePathList);
                intent.putIntegerArrayListExtra("selectedPicturePositionList", new ArrayList<>(mSelectedPositionSet));
                intent.putExtra("currentPictureIndex", position);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }

    public void setOnMultipleItemSelectListener(OnMultipleItemSelectListener listener) {
        mOnMultipleItemSelectListener = listener;
    }

    public interface OnMultipleItemSelectListener {
        void onSelect();
    }
}
