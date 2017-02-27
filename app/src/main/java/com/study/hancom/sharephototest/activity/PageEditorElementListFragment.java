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
import android.widget.NumberPicker;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.ElementListAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;

import static com.study.hancom.sharephototest.model.Album.MAX_ELEMENT_OF_PAGE_NUM;

public class PageEditorElementListFragment extends Fragment implements DataChangedListener.OnDataChangeListener {

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
                R.layout.page_editor_element_list_row, R.id.row_menuHolder, R.id.row_header_text,
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
                final NumberPicker pageNumberPicker = new NumberPicker(getActivity());
                pageNumberPicker.setMinValue(1);
                pageNumberPicker.setMaxValue(mElementListAdapter.getSectionsCount() + 1);
                pageNumberPicker.setValue(mElementListAdapter.getSelectedSection() + 1);
                createDialog(getString(R.string.dialog_title_action_single_move), getString(R.string.dialog_message_action_single_move))
                        .setView(pageNumberPicker)
                        .setPositiveButton(getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int fromIndex = mElementListAdapter.getSelectedSection();
                                int fromPosition = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                int toIndex = pageNumberPicker.getValue() - 1;
                                int toPosition = mElementListAdapter.getCountInSection(toIndex);
                                try {
                                    mElementListAdapter.reorderPicture(fromIndex, fromPosition, toIndex, toPosition);
                                    mElementListAdapter.setSelectedSection(toIndex);
                                    mElementListAdapter.setSelectedItem(-1);
                                    mElementListAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_single_move_fail), Toast.LENGTH_LONG).show();
                                }
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
            case R.id.action_single_delete:
                createDialog(getString(R.string.dialog_title_action_single_delete), getString(R.string.dialog_message_action_single_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mElementListAdapter.getSelectedSection();
                                int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                mElementListAdapter.removePicture(index, position, true);
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mElementListAdapter.getSelectedSection();
                                int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                mElementListAdapter.removePicture(index, position, false);
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
                for (int eachPosition = 0; eachPosition < dataCount; eachPosition++) {
                    mElementListAdapter.addMultipleSelectedItem(eachPosition);
                }
                mElementListAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_multiple_edit:
                return true;
            case R.id.action_multiple_move:
                if (mElementListAdapter.getMultipleSelectedItem().length > MAX_ELEMENT_OF_PAGE_NUM) {
                    Toast.makeText(getActivity(), getString(R.string.toast_action_single_move_fail), Toast.LENGTH_LONG);
                }
                return true;
            case R.id.action_multiple_delete:
                createDialog(getString(R.string.dialog_title_action_multiple_delete), getString(R.string.dialog_message_action_multiple_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int eachSelectedItemPosition : mElementListAdapter.getMultipleSelectedItem()) {
                                    int index = mElementListAdapter.getTypeFor(eachSelectedItemPosition);
                                    int position = mElementListAdapter.getPositionInSection(eachSelectedItemPosition);
                                    mElementListAdapter.removePicture(index, position, true);
                                }
                                mElementListAdapter.stopMultipleSelectMode();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer[] sortedMultipleSelectedItemArray = mElementListAdapter.getMultipleSelectedItem(true);
                                for (int i = sortedMultipleSelectedItemArray.length - 1; i > -1; i--) {
                                    int eachSelectedItemPosition = sortedMultipleSelectedItemArray[i];
                                    int index = mElementListAdapter.getTypeFor(eachSelectedItemPosition);
                                    int position = mElementListAdapter.getPositionInSection(eachSelectedItemPosition);
                                    mElementListAdapter.removePicture(index, position, false);
                                }
                                mElementListAdapter.stopMultipleSelectMode();
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
            case R.id.action_empty_set_picture:
                return true;
            case R.id.action_empty_delete:
                createDialog(getString(R.string.dialog_title_action_empty_delete), getString(R.string.dialog_message_action_empty_delete))
                        .setPositiveButton(getString(R.string.dialog_button_continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mElementListAdapter.getSelectedSection();
                                int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                mElementListAdapter.removePicture(index, position, false);
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
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
    public void onDataChanged() {
        mElementListAdapter.notifyDataSetChanged();
    }
}
