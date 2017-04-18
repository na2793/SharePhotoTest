package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.RecentProjectGridAdapter;
import com.study.hancom.sharephototest.adapter.base.RecyclerClickableItemAdapter;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ArrayList<String> mRecentProjectPath = new ArrayList<>();
    private RecentProjectGridAdapter mRecentProjectGridAdapter;

    private AutoFitRecyclerGridView mRecentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newProjectBtn = (Button) findViewById(R.id.new_project);
        Button loadProjectBtn = (Button) findViewById(R.id.load_project);

        newProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryMultipleSelectionActivity.class);
                startActivity(intent);
            }
        });

        loadProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/epub+zip");
                startActivityForResult(intent, 1);
            }
        });

        mRecentProject = (AutoFitRecyclerGridView) findViewById(R.id.recent_project_grid_view);
        mRecentProjectGridAdapter = new RecentProjectGridAdapter(getApplicationContext(), mRecentProjectPath);
        mRecentProjectGridAdapter.setOnOnItemClickListener(new RecyclerClickableItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), position + " ", Toast.LENGTH_LONG).show();
            }
        });

        mRecentProject.setAdapter(mRecentProjectGridAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String filePath = data.getData().getPath();

            mRecentProjectPath.remove(filePath);
            mRecentProjectPath.add(0, filePath);

            mRecentProjectGridAdapter.notifyDataSetChanged();
        }
    }
}
