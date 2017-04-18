package com.study.hancom.sharephototest.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.AlbumGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumManager;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.EpubMaker;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.ArrayList;
import java.util.List;

public class AlbumOverviewActivity extends AppCompatActivity {
    static final String STATE_ALBUM = "album";
    static final String STATE_ALBUM_GRID_ADAPTER_ALL_PINNED_POSITION = "albumGridAdapterAllPinnedPosition";

    private Album mAlbum;

    private AutoFitRecyclerGridView mAlbumGridView;
    private AlbumGridAdapter mAlbumGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_overview_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mAlbum = savedInstanceState.getParcelable(STATE_ALBUM);
            mAlbumGridAdapter = new AlbumGridAdapter(this, mAlbum);
            ArrayList<Integer> albumGridAdapterAllPinnedPosition = savedInstanceState.getIntegerArrayList(STATE_ALBUM_GRID_ADAPTER_ALL_PINNED_POSITION);
            for (int eachPosition : albumGridAdapterAllPinnedPosition) {
                mAlbumGridAdapter.addPinnedPosition(eachPosition);
            }
        } else {
            Bundle bundle = getIntent().getExtras();
            List<String> picturePathList = bundle.getStringArrayList("selectedPicturePathList");

            List<Picture> pictureList = new ArrayList<>();
            for (String eachPicturePath : picturePathList) {
                Picture picture = new Picture(eachPicturePath);
                pictureList.add(picture);
            }

            try {
                mAlbum = AlbumManager.createAlbum(pictureList);
            } catch (LayoutNotFoundException e) {
                //** String 임시
                Toast.makeText(this, "ERROR : 페이지를 구성하는데 필요한 필수 파일을 찾지 못했습니다. (../SharePhoto/layout)", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                finish();
            }

            mAlbumGridAdapter = new AlbumGridAdapter(this, mAlbum);
        }

        /* 어댑터 붙이기 */
        mAlbumGridView = (AutoFitRecyclerGridView) findViewById(R.id.album_overview_grid);
        mAlbumGridView.setAdapter(mAlbumGridAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_overview_main, menu);
        setTitle(R.string.title_album_overview_main);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_album_edit:
                Intent intentEditor = new Intent(this, AlbumEditorActivity.class);
                intentEditor.putExtra("album", mAlbum);
                startActivity(intentEditor);
                return true;

            case R.id.action_album_relayout:
                try {
                    List<Integer> oldPinnedPositionList = mAlbumGridAdapter.getPinnedPositionAll();
                    List<Integer> newPinnedPositionList = AlbumManager.relayoutAlbum(mAlbum, oldPinnedPositionList);
                    if (oldPinnedPositionList != null && newPinnedPositionList != null) {
                        for (int eachPosition : oldPinnedPositionList) {
                            mAlbumGridAdapter.removePinnedPosition(eachPosition);
                        }
                        for (int eachPosition : newPinnedPositionList) {
                            mAlbumGridAdapter.addPinnedPosition(eachPosition);
                        }
                    }
                } catch (LayoutNotFoundException e) {
                    //** String 임시
                    Toast.makeText(this, "ERROR : 앨범 재구성을 실패했습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                mAlbumGridAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_album_confirm:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View promptView = layoutInflater.inflate(R.layout.album_editor_save, null);

                final EditText titleEdit = (EditText) promptView.findViewById(R.id.save_epub_editText_title);
                final EditText authorEdit = (EditText) promptView.findViewById(R.id.save_epub_editText_author);
                final EditText publisherEdit = (EditText) promptView.findViewById(R.id.save_epub_editText_publisher);

                createDialog(getString(R.string.dialog_title_action_create_epub), getString(R.string.dialog_message_action_create_epub))
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.dialog_button_save), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String title = titleEdit.getText().toString();
                                String author = authorEdit.getText().toString();
                                String publisher = publisherEdit.getText().toString();
                                if (title.length() != 0 && author.length() != 0&& publisher.length() != 0) {
                                    new createEpubTask().execute(title, author, publisher);
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.toast_action_save_epub_empty_data), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })

                        .create()
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private AlertDialog.Builder createDialog(String title, String message) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
    }


    private class createEpubTask extends AsyncTask<String, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(AlbumOverviewActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("저장중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            new EpubMaker(mAlbum, getApplicationContext()).createFile(arg0[0], arg0[1], arg0[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_ALBUM, mAlbum);
        outState.putIntegerArrayList(STATE_ALBUM_GRID_ADAPTER_ALL_PINNED_POSITION, mAlbumGridAdapter.getPinnedPositionAll());
        super.onSaveInstanceState(outState);
    }
}