package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;

import java.util.HashSet;
import java.util.Set;

public class PageEditorAdapter extends SectionableAdapter {

    private Album mAlbum;

    private int mSelectedSection;
    private int mSelectedItemPosition;

    private boolean mIsMultipleItemSelectionMode;
    private Set<Integer> mMultipleSelectedItemPositionSet;

    private OnItemTouchListener mOnItemTouchListener;

    private static ImageLoader mImageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions mImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.place_holder)
            .showImageForEmptyUri(R.drawable.place_holder)
            .showImageOnFail(R.drawable.place_holder)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public PageEditorAdapter(Context context, Album album, int rowLayoutID, int headerID, int itemHolderID, int resizeMode) {
        super(context, rowLayoutID, headerID, itemHolderID, resizeMode);
        mAlbum = album;
        mSelectedSection = 0;
        mSelectedItemPosition = -1;
        mIsMultipleItemSelectionMode = false;
        mMultipleSelectedItemPositionSet = new HashSet<>();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        mOnItemTouchListener = new OnItemTouchListener() {
            @Override
            public boolean onItemTouch(int sectionNum) {
                mSelectedSection = sectionNum;

                return false;
            }
        };
    }

    @Override
    public Object getItem(int position) {
        int pageNum = mAlbum.getPageCount();
        for (int i = 0; i < pageNum; ++i) {
            Page eachPage = mAlbum.getPage(i);
            int pictureNum = eachPage.getPictureCount();
            if (position < pictureNum) {
                return eachPage.getPicture(position);
            }
            position -= pictureNum;
        }
        // This will never happen.
        return null;
    }

    @Override
    protected int getDataCount() {
        int total = 0;
        int pageNum = mAlbum.getPageCount();
        for (int i = 0; i < pageNum; ++i) {
            total += mAlbum.getPage(i).getPictureCount();
        }
        return total;
    }

    @Override
    protected int getSectionsCount() {
        return mAlbum.getPageCount();
    }

    @Override
    protected int getCountInSection(int index) {
        return mAlbum.getPage(index).getPictureCount();
    }

    @Override
    protected int getTypeFor(int position) {
        int runningTotal = 0;
        int pageNum = mAlbum.getPageCount();
        for (int i = 0; i < pageNum; ++i) {
            int eachItemCount = mAlbum.getPage(i).getPictureCount();
            if (position < runningTotal + eachItemCount)
                return i;
            runningTotal += eachItemCount;
        }
        // This will never happen.
        return -1;
    }

    @Override
    protected String getHeaderForSection(int section) {
        return Integer.toString(section + 1) + " 페이지";
    }

    @Override
    protected void bindView(final View convertView, final int position) {
        TextView textView = (TextView) convertView.findViewById(R.id.item_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_image);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.item_checkbox);

        /* 체크박스 처리 */
        if (mIsMultipleItemSelectionMode) {
            checkBox.setVisibility(View.VISIBLE);
            if (mMultipleSelectedItemPositionSet.contains(position)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        } else {
            if (position == mSelectedItemPosition) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            } else {
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
            }
        }

        /* 텍스트뷰 처리 */
        String elementNum = Integer.toString(getPositionInSection(position) + 1);
        textView.setText(elementNum);

        /* 이미지뷰 처리 */
        mImageLoader.displayImage(((Picture) getItem(position)).getPath(), imageView, mImageOptions);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMultipleItemSelectionMode) {
                    mMultipleSelectedItemPositionSet.add(position);
                } else {
                    if (mSelectedItemPosition != position) {
                        mSelectedItemPosition = position;
                        mOnItemTouchListener.onItemTouch(getTypeFor(position));
                    } else {
                        mSelectedItemPosition = -1;
                    }
                }
                notifyDataSetChanged();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startMultipleSelectionMode(position);
                mOnItemTouchListener.onItemTouch(getTypeFor(position));
                notifyDataSetChanged();

                return false;
            }
        });
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        convertView = super.getView(index, convertView, parent);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemTouchListener.onItemTouch(getSectionByIndex(index));
                notifyDataSetChanged();
            }
        });

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.row_menu);
        if (mIsMultipleItemSelectionMode) {
            linearLayout.setVisibility(View.GONE);
            convertView.setAlpha(1);
        } else {
            if (mSelectedSection == getSectionByIndex(index)) {
                if (isSectionHeader(index)) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                convertView.setAlpha(1);
            } else {
                linearLayout.setVisibility(View.GONE);
                convertView.setAlpha(0.5f);
            }
        }

        return convertView;
    }

    public void startMultipleSelectionMode() {
        this.startMultipleSelectionMode(-1);
    }

    public void startMultipleSelectionMode(int position) {
        mIsMultipleItemSelectionMode = true;
        if (position != -1) {
            mMultipleSelectedItemPositionSet.add(position);
        }
    }

    public void stopMultipleSelectionMode() {
        mIsMultipleItemSelectionMode = false;
        mMultipleSelectedItemPositionSet.clear();
    }

    private int getPositionInSection(int position) {
        int runningTotal = 0;
        int pageCount = mAlbum.getPageCount();
        for (int i = 0; i < pageCount; ++i) {
            int eachItemCount = mAlbum.getPage(i).getPictureCount();
            if (position < runningTotal + eachItemCount) {
                return position - runningTotal;
            }
            runningTotal += eachItemCount;
        }
        // This will never happen.
        return -1;
    }

    private int getSectionByIndex(int index) {
        int runningTotal = 0;
        int pageCount = mAlbum.getPageCount();
        int colCount = getColCount();

        for (int i = 0; i < pageCount; ++i) {
            int eachItemCount = mAlbum.getPage(i).getPictureCount();
            int eachRowCount = (int) Math.ceil((double) eachItemCount / (double) colCount);
            if (index < runningTotal + eachRowCount) {
                return i;
            }
            runningTotal += eachRowCount;
        }
        // This will never happen.
        return -1;
    }

    private boolean isSectionHeader(int index) {
        int runningTotal = 0;
        int pageCount = mAlbum.getPageCount();
        int colCount = getColCount();

        for (int i = 0; i < pageCount; ++i) {
            int eachItemCount = mAlbum.getPage(i).getPictureCount();
            int eachRowCount = (int) Math.ceil((double) eachItemCount / (double) colCount);
            if (eachRowCount == runningTotal + eachRowCount - index) {
                return true;
            } else if (index < 0) {
                return false;
            }
            runningTotal += eachRowCount;
        }
        // This will never happen.
        return false;
    }

    /* 리스너 인터페이스 */
    public interface OnItemTouchListener
    {
        boolean onItemTouch(int sectionNum);
    }
}
