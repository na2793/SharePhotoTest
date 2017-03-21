package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.base.SectionedRecyclerGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumAction;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.AnimationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElementGridAdapter extends SectionedRecyclerGridAdapter<Album, ElementGridAdapter.HeaderViewHolder, ElementGridAdapter.ContentViewHolder> {

    private AlbumAction mAlbumAction = new AlbumAction();

    private boolean mEnableMultipleSelectMode = false;

    private int mSelectedSection = 0;
    private Set<Integer> mSelectedContentRawPositionSet = new HashSet<>();
    private int mSelectedContentRawPosition = -1;

    private OnContentSelectListener mOnContentSelectListener;
    private OnDataChangeListener mOnDataChangeListener;

    private AnimationUtil mAnimationUtil = new AnimationUtil();

    public ElementGridAdapter(Context context, Album data, GridLayoutManager layoutManager) {
        super(context, data, layoutManager);
    }

    public Picture getContent(int section, int position) {
        return mData.getPage(section).getPicture(position);
    }

    @Override
    public int getSectionCount() {
        return mData.getPageCount();
    }

    @Override
    public int getCountInSection(int sectionNum) {
        return mData.getPage(sectionNum).getPictureCount();
    }

    public Picture getContent(int rawPosition) {
        return mData.getPage(getSectionFor(rawPosition)).getPicture(rawPositionToPosition(rawPosition));
    }

    public int getContentCount() {
        int contentCount = 0;
        int sectionCount = getSectionCount();

        for (int i = 0; i < sectionCount; ++i) {
            contentCount += getCountInSection(i);
        }

        return contentCount;
    }

    public int getSelectedContentRawPosition() {
        if (mEnableMultipleSelectMode) {
            return -1;
        } else {
            return mSelectedContentRawPosition;
        }
    }

    public List<Integer> getMultipleSelectedContentRawPosition() {
        if (mEnableMultipleSelectMode) {
            return new ArrayList<>(mSelectedContentRawPositionSet);
        } else {
            return null;
        }
    }

    public int getSelectedContentCount() {
        if (mEnableMultipleSelectMode) {
            return mSelectedContentRawPositionSet.size();
        } else {
            if (mSelectedContentRawPosition > -1) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View headerView = LayoutInflater.from(mContext).inflate(R.layout.album_editor_element_grid_item_header, parent, false);
        return new HeaderViewHolder(headerView);
    }

    @Override
    public ContentViewHolder onCreateContentViewHolder(ViewGroup parent) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.album_editor_element_grid_item_content, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, final int section, int rawPosition) {
        /* 선택 영역 */
        int maxSectionIndex = getSectionCount() - 1;
        if (mSelectedSection > maxSectionIndex ) {
            mSelectedSection = maxSectionIndex;
        }

        /* 전체 */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSection(section);
                notifyDataSetChanged();
            }
        });

        /* 텍스트 뷰 */
        holder.textView.setText((section + 1) + " 페이지");

        /* 메뉴 홀더 */
        if (mEnableMultipleSelectMode) {
            holder.menuHolder.setVisibility(View.GONE);
            holder.view.setAlpha(1);
        } else {
            if (mSelectedSection == section) {
                holder.menuHolder.setVisibility(View.VISIBLE);
                holder.view.setAlpha(1);
            } else {
                holder.menuHolder.setVisibility(View.GONE);
                holder.view.setAlpha(0.5f);
            }
        }

        /* 메뉴 */
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAlbumAction.removePage(mData, section);
                    mSelectedContentRawPosition = -1;
                    mOnDataChangeListener.onDataChanged();
                } catch (LayoutNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBindContentViewHolder(ContentViewHolder holder, final int section, final int position, final int rawPosition) {
        /* 전체 & 체크 박스 */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSection(section);
                selectContent(rawPosition);
                notifyDataSetChanged();
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startMultipleSelectMode();
                selectContent(rawPosition);
                notifyDataSetChanged();

                return false;
            }
        });

        if (mEnableMultipleSelectMode) {
            mAnimationUtil.startWobbleAnimation(holder.view);
            holder.view.setAlpha(1);

            holder.checkBox.setVisibility(View.VISIBLE);
            if (mSelectedContentRawPositionSet.contains(rawPosition)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            mAnimationUtil.stopWobbleAll();
            holder.view.setRotation(0);

            if (mSelectedSection == section) {
                holder.view.setAlpha(1);
            } else {
                holder.view.setAlpha(0.5f);
            }

            if (mSelectedContentRawPosition == rawPosition) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.checkBox.setChecked(false);
            }
        }

        /* 텍스트 뷰 */
        String elementNum = Integer.toString(rawPositionToPosition(rawPosition) + 1);
        holder.textView.setText(elementNum);

        /* 이미지 뷰 */
        Picture picture = mData.getPage(section).getPicture(position);
        if (picture != null) {
            Glide.with(mContext).load(picture.getPath()).centerCrop().into(holder.imageView);
        } else {
            Glide.with(mContext).load(R.drawable.place_holder).into(holder.imageView);
        }
    }

    public void selectSection(int sectionIndex) {
        if (sectionIndex < 0) {
            mSelectedSection = -1;
        } else {
            mSelectedSection = sectionIndex;
        }
    }

    public int getSelectedSection() {
        return mSelectedSection;
    }

    public boolean selectContent(int rawPosition) {
        if (mEnableMultipleSelectMode) {
            if (mSelectedContentRawPositionSet.remove(rawPosition)) {
                if (mOnContentSelectListener != null) {
                    mOnContentSelectListener.onSelect(getSectionFor(rawPosition), rawPositionToPosition(rawPosition));
                }
                return false;
            } else {
                mSelectedContentRawPositionSet.add(rawPosition);
                if (mOnContentSelectListener != null) {
                    mOnContentSelectListener.onSelect(getSectionFor(rawPosition), rawPositionToPosition(rawPosition));
                }
                return true;
            }
        } else {
            if (rawPosition < 0) {
                mSelectedContentRawPosition = -1;
                if (mOnContentSelectListener != null) {
                    mOnContentSelectListener.onCancel();
                }
                return false;
            } else {
                if (mSelectedContentRawPosition != rawPosition) {
                    mSelectedContentRawPosition = rawPosition;
                    if (mOnContentSelectListener != null) {
                        mOnContentSelectListener.onSelect(getSectionFor(rawPosition), rawPositionToPosition(rawPosition));
                    }
                    return true;
                } else {
                    mSelectedContentRawPosition = -1;
                    if (mOnContentSelectListener != null) {
                        mOnContentSelectListener.onCancel();
                    }
                    return false;
                }
            }
        }
    }

    public void setMultipleSelectMode(boolean enable) {
        if (enable) {
            startMultipleSelectMode();
        } else {
            stopMultipleSelectMode();
        }
    }

    public boolean isMultipleSelectModeEnabled() {
        return mEnableMultipleSelectMode;
    }

    public void startMultipleSelectMode() {
        mEnableMultipleSelectMode = true;
    }

    public void stopMultipleSelectMode() {
        mEnableMultipleSelectMode = false;
        mSelectedContentRawPositionSet.clear();
        selectContent(-1);
    }

    public void setOnContentSelectListener(OnContentSelectListener listener) {
        mOnContentSelectListener = listener;
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        mOnDataChangeListener = listener;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        LinearLayout menuHolder;
        Button buttonPreview;
        Button buttonChangeLayout;
        Button buttonDelete;

        HeaderViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = (TextView) itemView.findViewById(R.id.header_text);
            menuHolder = (LinearLayout) itemView.findViewById(R.id.header_menu_holder);
            buttonPreview = (Button) itemView.findViewById(R.id.header_menu_button_preview);
            buttonChangeLayout = (Button) itemView.findViewById(R.id.header_menu_button_change_layout);
            buttonDelete = (Button) itemView.findViewById(R.id.header_menu_button_delete);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;
        CheckBox checkBox;
        TextView textView;

        ContentViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.item_content_image);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_content_checkbox);
            textView = (TextView) itemView.findViewById(R.id.item_content_text);
        }
    }

    /* 리스너 인터페이스 */
    public interface OnContentSelectListener {
        void onSelect(int section, int position);

        void onCancel();
    }

    public interface OnDataChangeListener {
        void onDataChanged();
    }
}
