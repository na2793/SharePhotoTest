package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumDataChangeInterface;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.AnimationUtil;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.study.hancom.sharephototest.model.Album.MAX_ELEMENT_OF_PAGE_NUM;

public class ElementListAdapter extends SectionableAdapter implements AlbumDataChangeInterface {

    private Album mAlbum;

    private int mSelectedSection;
    private int mSelectedItemPosition;

    private boolean mIsMultipleItemSelectMode;
    private Set<Integer> mMultipleSelectedItemPositionSet;

    private OnItemTouchListener mOnItemTouchListener;
    private OnItemSelectListener mOnItemSelectListener;
    private OnMultipleItemSelectModeListener mOnMultipleItemSelectModeListener;

    private AnimationUtil mAnimationUtil;

    private static ImageLoader mImageLoader = ImageLoader.getInstance();

    public ElementListAdapter(Context context, Album album, int rowLayoutID, int headerMenuHolderID, int headerTextID, int itemHolderID, int resizeMode) {
        super(context, rowLayoutID, headerMenuHolderID, headerTextID, itemHolderID, resizeMode);
        mAlbum = album;
        mSelectedSection = 0;
        mSelectedItemPosition = -1;
        mIsMultipleItemSelectMode = false;
        mMultipleSelectedItemPositionSet = new HashSet<>();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        mOnItemTouchListener = new OnItemTouchListener() {
            @Override
            public void onItemTouch(int sectionNum) {
                setSelectedSection(sectionNum);
            }
        };
        mAnimationUtil = new AnimationUtil();
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
    public int getDataCount() {
        int total = 0;
        int pageNum = mAlbum.getPageCount();
        for (int i = 0; i < pageNum; ++i) {
            total += mAlbum.getPage(i).getPictureCount();
        }
        return total;
    }

    @Override
    public int getSectionsCount() {
        return mAlbum.getPageCount();
    }

    @Override
    public int getCountInSection(int index) {
        if (index >= getSectionsCount()) {
            return -1;
        }
        return mAlbum.getPage(index).getPictureCount();
    }

    @Override
    public int getTypeFor(int position) {
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
    public String getHeaderForSection(int section) {
        return context.getResources().getString(R.string.album_editor_section_header, section + 1);
    }

    @Override
    protected void bindView(final View convertView, final int position) {
        TextView textView = (TextView) convertView.findViewById(R.id.item_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_image);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.item_checkbox);

        /* 편집 효과 처리 */
        if (mIsMultipleItemSelectMode) {
            mAnimationUtil.startWobbleAnimation(convertView);
        } else {
            convertView.setRotation(0);
        }

        /* 텍스트뷰 처리 */
        String elementNum = Integer.toString(getPositionInSection(position) + 1);
        textView.setText(elementNum);

        /* 이미지뷰 처리 */
        Picture picture = (Picture) getItem(position);
        if (picture != null) {
            mImageLoader.displayImage("file://" + picture.getPath(), imageView, ImageUtil.options);
        } else {
            mImageLoader.displayImage(ImageUtil.drawableResourceToURI(R.drawable.place_holder), imageView, ImageUtil.options);
        }

        /* 체크박스 처리 */
        if (mIsMultipleItemSelectMode) {
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

        /* 이벤트 처리 */
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMultipleItemSelectMode) {
                    if (!removeMultipleSelectedItem(position)) {
                        addMultipleSelectedItem(position);
                    }
                    mOnMultipleItemSelectModeListener.onSelect();
                } else {
                    if (mSelectedItemPosition != position) {
                        mOnItemTouchListener.onItemTouch(getTypeFor(position));
                        setSelectedItem(position);
                    } else {
                        setSelectedItem(-1);
                    }
                }
                notifyDataSetChanged();
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startMultipleSelectMode(position);
                mSelectedItemPosition = -1;
                mOnItemTouchListener.onItemTouch(getTypeFor(position));
                notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    protected void customizeRow(final int row, View rowView) {
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemTouchListener.onItemTouch(getSectionByIndex(row));
                if (!mIsMultipleItemSelectMode) {
                    setSelectedItem(-1);
                }
                notifyDataSetChanged();
            }
        });

        LinearLayout menuHolder = (LinearLayout) rowView.findViewById(headerMenuHolderID);
        if (mIsMultipleItemSelectMode) {
            menuHolder.setVisibility(View.GONE);
            rowView.setAlpha(1);
        } else {
            if (mSelectedSection == getSectionByIndex(row)) {
                rowView.setAlpha(1);
            } else {
                menuHolder.setVisibility(View.GONE);
                rowView.setAlpha(0.5f);
            }
        }

        /* 페이지 컨텍스트 메뉴 버튼 처리 */
        Button buttonPreview = (Button) rowView.findViewById(R.id.button_preview);
        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Button buttonChangeLayout = (Button) rowView.findViewById(R.id.button_change_layout);
        buttonChangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Button buttonDelete = (Button) rowView.findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePage(getSelectedSection());
                setSelectedSection(-1);
            }
        });
    }

    public void startMultipleSelectMode() {
        this.startMultipleSelectMode(-1);
    }

    public void startMultipleSelectMode(int position) {
        mIsMultipleItemSelectMode = true;
        if (position > -1) {
            addMultipleSelectedItem(position);
        }
        if (mOnMultipleItemSelectModeListener != null) {
            mOnMultipleItemSelectModeListener.onStart();
        }
    }

    public void stopMultipleSelectMode() {
        mAnimationUtil.stopWobbleAll();
        mIsMultipleItemSelectMode = false;
        clearMultipleSelectedItem();
        if (mOnMultipleItemSelectModeListener != null) {
            mOnMultipleItemSelectModeListener.onStop();
        }
    }

    public void setSelectedItem(int position) {
        mSelectedItemPosition = position;
        if (mOnItemSelectListener != null) {
            if (position > -1) {
                mOnItemSelectListener.onItemSelect(getItem(position));
            } else {
                mOnItemSelectListener.onItemSelectCancel();
            }
        }
    }

    public void addMultipleSelectedItem(int position) {
        if (position > -1) {
            mMultipleSelectedItemPositionSet.add(position);
        }
    }

    public boolean removeMultipleSelectedItem(int position) {
        return mMultipleSelectedItemPositionSet.remove(position);
    }

    public void clearMultipleSelectedItem() {
        mMultipleSelectedItemPositionSet.clear();
    }

    public int getSelectedItemCount() {
        if (mIsMultipleItemSelectMode) {
            return mMultipleSelectedItemPositionSet.size();
        } else {
            if (mSelectedItemPosition > -1) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public int getSelectedItem() {
        return mSelectedItemPosition;
    }

    public Integer[] getMultipleSelectedItem() {
        return getMultipleSelectedItem(false);
    }

    public Integer[] getMultipleSelectedItem(boolean isSort) {
        Integer[] multipleSelectedItemArray = mMultipleSelectedItemPositionSet.toArray(new Integer[mMultipleSelectedItemPositionSet.size()]);
        if (isSort) {
            Arrays.sort(multipleSelectedItemArray);
        }
        return multipleSelectedItemArray;
    }

    public void setSelectedSection(int index) {
        mSelectedSection = index;
    }

    public int getSelectedSection() {
        return mSelectedSection;
    }

    public int getPositionInSection(int position) {
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

    public int getSectionByIndex(int index) {
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

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        mOnItemSelectListener = listener;
    }

    public void setOnMultipleItemSelectModeListener(OnMultipleItemSelectModeListener listener) {
        mOnMultipleItemSelectModeListener = listener;
    }

    @Override
    public void addPage(Page page) {
        addPage(mAlbum.getPageCount(), page);
    }

    @Override
    public void addPage(int index, Page page) {
        mAlbum.addPage(index, page);
        DataChangedListener.notifyChanged();
    }

    @Override
    public void removePage(int index) {
        mAlbum.removePage(index);
        DataChangedListener.notifyChanged();
    }

    @Override
    public void reorderPage(int fromIndex, int toIndex) {
        mAlbum.reorderPage(fromIndex, toIndex);
        DataChangedListener.notifyChanged();
    }

    @Override
    public void addPicture(int index, Picture picture) {
        addPicture(index, mAlbum.getPage(index).getPictureCount(), picture);
    }

    @Override
    public void addPicture(int index, int position, Picture picture) {
        mAlbum.getPage(index).addPicture(position, picture);
        DataChangedListener.notifyChanged();
    }

    public void removePicture(int index, int position, boolean nullable) {
        if (nullable) {
            mAlbum.getPage(index).removePicture(position);
            mAlbum.getPage(index).addPicture(position, null);
        } else {
            Page page = mAlbum.getPage(index);
            int pictureCount = page.getPictureCount();
            if (pictureCount > 1) {
                try {
                    page.setLayout(pictureCount - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mAlbum.removePage(index);
                setSelectedSection(-1);
            }
            page.removePicture(position);
        }
        setSelectedItem(-1);
        DataChangedListener.notifyChanged();
    }

    @Override
    public void reorderPicture(int fromIndex, int fromPosition, int toIndex, int toPosition) throws Exception {
        if (fromIndex == toIndex) {
            mAlbum.getPage(fromIndex).reorderPicture(fromPosition, toPosition);
        } else {
            if (toIndex >= getSectionsCount()) {
                mAlbum.addPage(new Page(1));
                if (toPosition < 0) {
                    toPosition = 0;
                }
            } else if (MAX_ELEMENT_OF_PAGE_NUM < getCountInSection(toIndex) + 1) {
                //** 사용 가능한 요소 갯수를 넘음
                throw new Exception();
            }

            Picture target;
            if (getCountInSection(fromIndex) > 1) {
                target = mAlbum.getPage(fromIndex).removePicture(fromPosition);
            } else {
                target = mAlbum.getPage(fromIndex).removePicture(fromPosition);
                mAlbum.getPage(fromIndex).addPicture(fromPosition, null);
            }
            mAlbum.getPage(toIndex).addPicture(toPosition, target);
        }
        DataChangedListener.notifyChanged();
    }

    /* 리스너 인터페이스 */
    public interface OnItemTouchListener {
        void onItemTouch(int sectionNum);
    }

    public interface OnItemSelectListener {
        void onItemSelect(Object item);

        void onItemSelectCancel();
    }

    public interface OnMultipleItemSelectModeListener {
        void onStart();

        void onSelect();

        void onStop();
    }
}
