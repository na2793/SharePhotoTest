package com.study.hancom.sharephototest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.AlbumEditorNewLayoutSelectionActivity;
import com.study.hancom.sharephototest.activity.AlbumFullSizeWebViewActivity;
import com.study.hancom.sharephototest.adapter.base.SectionedRecyclerGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumManager;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.WobbleAnimator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ElementGridAdapter extends SectionedRecyclerGridAdapter<Album, ElementGridAdapter.HeaderViewHolder, ElementGridAdapter.ContentViewHolder> {
    private boolean mEnableMultipleSelectMode = false;

    private int mSelectedSection = 0;
    private Set<Integer> mSelectedContentRawPositionSet = new HashSet<>();
    private int mSelectedContentRawPosition = -1;

    private OnHeaderClickListener mOnHeaderClickListener;
    private OnContentClickListener mOnContentClickListener;

    private WobbleAnimator mWobbleAnimator = new WobbleAnimator();

    public ElementGridAdapter(Context context, Album data, GridLayoutManager layoutManager) {
        super(context, data, layoutManager);
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

    public Picture getContent(int section, int position) {
        return mData.getPage(section).getPicture(position);
    }

    public int getSelectedContentRawPosition() {
        return mSelectedContentRawPosition;
    }

    public ArrayList<Integer> getMultipleSelectedContentRawPosition() {
        return new ArrayList<>(mSelectedContentRawPositionSet);
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
        final HeaderViewHolder holder = new HeaderViewHolder(headerView);
        return holder;
    }

    @Override
    public ContentViewHolder onCreateContentViewHolder(ViewGroup parent) {
        final View contentView = LayoutInflater.from(mContext).inflate(R.layout.album_editor_element_grid_item_content, parent, false);
        return new ContentViewHolder(contentView);
    }

    @Override
    public void onBindHeaderViewHolder(final HeaderViewHolder holder, final int section, final int rawPosition) {
        /* 전체 */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onClick(section, rawPosition, v);
                }
            }
        });

        /* 메뉴 */
        holder.buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onClick(section, rawPosition, v);
                }
            }
        });
        holder.buttonChangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onClick(section, rawPosition, v);
                }
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onClick(section, rawPosition, v);
                }
            }
        });
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

        /* 텍스트 뷰 */
        holder.textView.setText((section + 1) + " 페이지");    //** 리소스로 뺄 것
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onClick(section, rawPosition, v);
                }
            }
        });
    }

    @Override
    public void onBindContentViewHolder(final ContentViewHolder holder, final int section, final int position, final int rawPosition) {
        /* 전체 */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnContentClickListener != null) {
                    mOnContentClickListener.onClick(section, position, rawPosition, v);
                }
            }
        });

        if (mEnableMultipleSelectMode) {
            holder.view.setAlpha(1);
        } else {
            if (mSelectedSection == section) {
                holder.view.setAlpha(1);
            } else {
                holder.view.setAlpha(0.5f);
            }
        }

        /* 체크박스 */
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnContentClickListener != null) {
                    mOnContentClickListener.onClick(section, position, rawPosition, v);
                }
            }
        });

        if (mEnableMultipleSelectMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (mSelectedContentRawPositionSet.contains(rawPosition)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            if (mSelectedContentRawPosition == rawPosition) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.checkBox.setChecked(false);
            }
        }

        /* 텍스트 뷰 */
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnContentClickListener != null) {
                    mOnContentClickListener.onClick(section, position, rawPosition, v);
                }
            }
        });
        String elementNum = Integer.toString(rawPositionToPosition(rawPosition) + 1);
        holder.textView.setText(elementNum);

        /* 이미지 뷰 */
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnContentClickListener != null) {
                    mOnContentClickListener.onClick(section, position, rawPosition, v);
                }
            }
        });
        Picture picture = mData.getPage(section).getPicture(position);
        if (picture != null) {
            Glide.with(mContext).load(picture.getPath()).centerCrop().into(holder.imageView);
        } else {
            Glide.with(mContext).load(R.drawable.place_holder).into(holder.imageView);
        }
    }

    public int getSelectedSection() {
        return mSelectedSection;
    }

    public void setSelectedSection(int sectionIndex) {
        if (sectionIndex < 0) {
            mSelectedSection = -1;
        } else {
            mSelectedSection = sectionIndex;
        }
    }

    public void setSelectedContentPosition(int rawPosition) {
        if (rawPosition < 0) {
            mSelectedContentRawPosition = -1;
        } else {
            mSelectedContentRawPosition = rawPosition;
        }
    }

    public void addMultipleSelectedContentPosition(int rawPosition) {
        mSelectedContentRawPositionSet.add(rawPosition);
    }

    public boolean removeMultipleSelectedContentPosition(int rawPosition) {
        return mSelectedContentRawPositionSet.remove(rawPosition);
    }

    public boolean isMultipleSelectModeEnabled() {
        return mEnableMultipleSelectMode;
    }

    public void setMultipleSelectMode(boolean enable) {
        if (enable) {
            startMultipleSelectMode();
        } else {
            stopMultipleSelectMode();
        }
    }

    public void startMultipleSelectMode() {
        mEnableMultipleSelectMode = true;
    }

    public void startMultipleSelectMode(int rawPosition) {
        mEnableMultipleSelectMode = true;
        mSelectedContentRawPositionSet.add(rawPosition);
    }

    public void stopMultipleSelectMode() {
        mEnableMultipleSelectMode = false;
        mSelectedContentRawPositionSet.clear();
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    public void setOnContentClickListener(OnContentClickListener listener) {
        mOnContentClickListener = listener;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
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

    public class ContentViewHolder extends RecyclerView.ViewHolder {
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
    public interface OnHeaderClickListener {
        void onClick(int section, int rawPosition, View v);
    }

    public interface OnContentClickListener {
        void onClick(int section, int position, int rawPosition, View v);
    }
}
