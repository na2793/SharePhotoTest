package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.ElementListAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.listener.AlbumDataChangeInterface;
import com.study.hancom.sharephototest.listener.AlbumDataChangedListener;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;

public class PageEditorElementListFragment extends Fragment implements AlbumDataChangeInterface {

    private static final int MENU_MODE_MAIN = 1;
    private static final int MENU_MODE_SINGLE_SELECT = 2;
    private static final int MENU_MODE_MULTIPLE_SELECT = 3;
    private static final int MENU_MODE_EMPTY_PICTURE = 4;

    private Menu mMenu;
    private int mMenuMode = MENU_MODE_MAIN;
    private MenuInflater mMenuInflater;

    private ListView mElementListView;
    private ElementListAdapter mElementListAdapter;

    private Album mAlbum;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("temp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.page_editor_element_list, container, false);

        /* 리스트뷰에 어댑터 붙이기 */
        mElementListView = (ListView) view.findViewById(R.id.page_list_view);
        mElementListAdapter = new ElementListAdapter(getActivity(), mAlbum,
                R.layout.page_editor_element_list_row, R.id.row_header,
                R.id.row_itemHolder, SectionableAdapter.MODE_VARY_WIDTHS);
        mElementListAdapter.setOnMultipleItemSelectModeListener(new ElementListAdapter.OnMultipleItemSelectModeListener() {
            @Override
            public void onStart() {
                onChangeActionBar(MENU_MODE_MULTIPLE_SELECT);
            }

            @Override
            public void onSelect() {
                getActivity().setTitle(String.format(getResources().getString(R.string.title_page_editor_multiple_select), mElementListAdapter.getSelectedItemCount(), mElementListAdapter.getDataCount()));
            }

            @Override
            public void onStop() {
                onChangeActionBar(MENU_MODE_MAIN);
            }
        });
        mElementListAdapter.setOnItemSelectListener(new ElementListAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(Object item) {
                if (item != null) {
                    onChangeActionBar(MENU_MODE_SINGLE_SELECT);
                } else {
                    onChangeActionBar(MENU_MODE_EMPTY_PICTURE);
                }
            }

            @Override
            public void onItemSelectCancel() {
                onChangeActionBar(MENU_MODE_MAIN);
            }
        });
        mElementListView.setAdapter(mElementListAdapter);
        mElementListView.setDividerHeight(0);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        mMenuInflater = inflater;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (mMenuMode) {
                    case MENU_MODE_MAIN:
                        getActivity().finish();
                        break;
                    case MENU_MODE_EMPTY_PICTURE:
                        // pass ; MENU_MODE_SINGLE_SELECT와 동일
                    case MENU_MODE_SINGLE_SELECT:
                        mElementListAdapter.setSelectedItem(-1);
                        mElementListAdapter.notifyDataSetChanged();
                        break;
                    case MENU_MODE_MULTIPLE_SELECT:
                        mElementListAdapter.stopMultipleSelectMode();
                        mElementListAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            case R.id.action_confirm:
                getActivity().finish();
                return true;
            case R.id.action_single_edit:
                return true;
            case R.id.action_single_move:
                return true;
            case R.id.action_single_delete:
                createDialog(getString(R.string.dialog_title_action_single_delete), getString(R.string.dialog_message_action_single_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mElementListAdapter.getSelectedSection();
                                int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                onPictureRemove(index, position, true);
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mElementListAdapter.getSelectedSection();
                                int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                onPictureRemove(index, position, false);
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            case R.id.action_multiple_select_all:
                int dataCount = mElementListAdapter.getDataCount();
                for (int i = 0; i < dataCount; i++) {
                    mElementListAdapter.setSelectedItem(i);
                }
                mElementListAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_multiple_edit:
                return true;
            case R.id.action_multiple_move:
                return true;
            case R.id.action_multiple_delete:
                return true;
            case R.id.action_empty_set_picture:
                return true;
            case R.id.action_empty_delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onChangeActionBar(int mode) {
        mMenuMode = mode;
        mMenu.clear();
        switch (mMenuMode) {
            case MENU_MODE_MAIN:
                getActivity().setTitle(R.string.title_page_editor_main);
                mMenuInflater.inflate(R.menu.page_editor_main, mMenu);
                break;
            case MENU_MODE_SINGLE_SELECT:
                getActivity().setTitle(R.string.title_page_editor_single_select);
                mMenuInflater.inflate(R.menu.page_editor_select_single_picture, mMenu);
                break;
            case MENU_MODE_MULTIPLE_SELECT:
                getActivity().setTitle(String.format(getResources().getString(R.string.title_page_editor_multiple_select), mElementListAdapter.getSelectedItemCount(), mElementListAdapter.getDataCount()));
                mMenuInflater.inflate(R.menu.page_editor_select_multiple_picture, mMenu);
                break;
            case MENU_MODE_EMPTY_PICTURE:
                getActivity().setTitle(R.string.title_page_editor_empty_picture);
                mMenuInflater.inflate(R.menu.page_editor_select_empty_picture, mMenu);
                break;
        }
    }

    private AlertDialog.Builder createDialog(String title, String message) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
    }

    @Override
    public void onPageAdd(Page page) {
        onPageAdd(mAlbum.getPageCount(), page);
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageAdd(int index, Page page) {
        mAlbum.addPage(index, page);
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageRemove(int index) {
        mAlbum.removePage(index);
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageReorder(int fromIndex, int toIndex) {
        mAlbum.reorderPage(fromIndex, toIndex);
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPictureAdd(int index, Picture picture) {
        onPictureAdd(index, mAlbum.getPage(index).getPictureCount(), picture);
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPictureAdd(int index, int position, Picture picture) {
        mAlbum.getPage(index).addPicture(position, picture);
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPictureRemove(int index, int position, boolean nullable) {
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
                mElementListAdapter.setSelectedSection(-1);
            }
            page.removePicture(position);
            mElementListAdapter.setSelectedItem(-1);
        }
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPictureReorder(int index, int fromPosition, int toPosition) {
        mAlbum.getPage(index).reorderPicture(fromPosition, toPosition);
        mElementListAdapter.notifyDataSetChanged();
    }
}
