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

import java.util.HashSet;
import java.util.Set;

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

    public ElementListAdapter(Context context, Album album, int rowLayoutID, int headerID, int itemHolderID, int resizeMode) {
        super(context, rowLayoutID, headerID, itemHolderID, resizeMode);
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
        return context.getResources().getString(R.string.page_editor_section_header, section + 1);
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
            mImageLoader.displayImage(picture.getPath(), imageView, ImageUtil.options);
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
                    if (!mMultipleSelectedItemPositionSet.contains(position)) {
                        mMultipleSelectedItemPositionSet.add(position);
                    } else {
                        mMultipleSelectedItemPositionSet.remove(position);
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
    public View getView(final int index, View convertView, final ViewGroup parent) {
        convertView = super.getView(index, convertView, parent);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemTouchListener.onItemTouch(getSectionByIndex(index));
                if (!mIsMultipleItemSelectMode) {
                    setSelectedItem(-1);
                }
                notifyDataSetChanged();
            }
        });

        /* 페이지 컨텍스트 메뉴 버튼 처리 */
        Button buttonPreview = (Button) convertView.findViewById(R.id.button_preview);
        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Button buttonChangeLayout = (Button) convertView.findViewById(R.id.button_change_layout);
        buttonChangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Button buttonDelete = (Button) convertView.findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePage(getSelectedSection());
                setSelectedSection(-1);
            }
        });

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.row_menu);
        if (mIsMultipleItemSelectMode) {
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

    public void startMultipleSelectMode() {
        this.startMultipleSelectMode(-1);
    }

    public void startMultipleSelectMode(int position) {
        mIsMultipleItemSelectMode = true;
        if (position > -1) {
            mMultipleSelectedItemPositionSet.add(position);
        }
        if (mOnMultipleItemSelectModeListener != null) {
            mOnMultipleItemSelectModeListener.onStart();
        }
    }

    public void stopMultipleSelectMode() {
        mAnimationUtil.stopWobbleAll();
        mIsMultipleItemSelectMode = false;
        mMultipleSelectedItemPositionSet.clear();
        if (mOnMultipleItemSelectModeListener != null) {
            mOnMultipleItemSelectModeListener.onStop();
        }
    }

    public void setSelectedItem(int position) {
        if (mIsMultipleItemSelectMode) {
            if (position > -1) {
                mMultipleSelectedItemPositionSet.add(position);
            }
        } else {
            mSelectedItemPosition = position;
            if (mOnItemSelectListener != null) {
                if (position > -1) {
                    mOnItemSelectListener.onItemSelect(getItem(position));
                } else {
                    mOnItemSelectListener.onItemSelectCancel();
                }
            }
        }
    }

    public int getSelectedItem() {
        return mSelectedItemPosition;
    }

    public Integer[] getMultipleSelectedItem() {
        return mMultipleSelectedItemPositionSet.toArray(new Integer[mMultipleSelectedItemPositionSet.size()]);
    }

    public void setSelectedSection(int index) {
        mSelectedSection = index;
    }

    public int getSelectedSection() {
        return mSelectedSection;
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
    public void reorderPicture(int index, int fromPosition, int toPosition) {
        mAlbum.getPage(index).reorderPicture(fromPosition, toPosition);
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
