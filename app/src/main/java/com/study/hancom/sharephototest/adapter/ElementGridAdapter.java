package com.study.hancom.sharephototest.adapter;

import android.content.Context;
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
import com.study.hancom.sharephototest.adapter.base.SectionedRecyclerGridAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Picture;

import java.util.ArrayList;
import java.util.List;

public class ElementGridAdapter extends SectionedRecyclerGridAdapter<Album, ElementGridAdapter.HeaderViewHolder, ElementGridAdapter.ContentViewHolder> {
    public static final int SELECT_MODE_SINGLE = 1;
    public static final int SELECT_MODE_MULTIPLE = 2;

    private int mSelectMode = SELECT_MODE_SINGLE;

    private int mSelectedSectionIndex = 0;
    private List<Integer> mSelectedRawPositionList = new ArrayList<>();

    private OnHeaderClickListener mOnHeaderClickListener;

    public ElementGridAdapter(Context context, Album data, GridLayoutManager layoutManager) {
        super(context, data, layoutManager);
        clearSelected();
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

    public int getSelectedItemRawPosition() {
        return mSelectedRawPositionList.get(0);
    }

    public ArrayList<Integer> getSelectedItemRawPositions() {
        return new ArrayList<>(mSelectedRawPositionList);
    }

    public int getSelectedItemCount() {
        if (mSelectMode == SELECT_MODE_MULTIPLE) {
            return mSelectedRawPositionList.size();
        } else {
            if (mSelectedRawPositionList.get(0) > -1) {
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
        if (mSelectMode == SELECT_MODE_MULTIPLE) {
            holder.menuHolder.setVisibility(View.GONE);
            holder.view.setAlpha(1);
        } else {
            if (mSelectedSectionIndex == section) {
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
        if (mSelectMode == SELECT_MODE_MULTIPLE) {
            holder.view.setAlpha(1);
        } else {
            if (mSelectedSectionIndex == section) {
                holder.view.setAlpha(1);
            } else {
                holder.view.setAlpha(0.5f);
            }
        }

        /* 체크박스 */
        if (mSelectMode == SELECT_MODE_MULTIPLE) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (mSelectedRawPositionList.contains(rawPosition)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            if (mSelectedRawPositionList.get(0) == rawPosition) {
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

    public int getSelectedSection() {
        return mSelectedSectionIndex;
    }

    public void setSelectedSection(int sectionIndex) {
        if (sectionIndex < 0) {
            mSelectedSectionIndex = -1;
        } else {
            mSelectedSectionIndex = sectionIndex;
        }
    }

    public void setSelectedItemPosition(Integer rawPosition) {
        mSelectedRawPositionList.set(0, rawPosition);
    }

    public void addSelectedItemPosition(Integer rawPosition) {
        mSelectedRawPositionList.add(rawPosition);
    }

    public boolean removeSelectedItemPosition(Integer rawPosition) {
        return mSelectedRawPositionList.remove(rawPosition);
    }

    public void clearSelected() {
        mSelectedRawPositionList.clear();
        mSelectedRawPositionList.add(-1);
    }

    public int getSelectMode() {
        return mSelectMode;
    }

    public void setSelectMode(int mode) {
        mSelectMode = mode;
        clearSelected();
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
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
}
