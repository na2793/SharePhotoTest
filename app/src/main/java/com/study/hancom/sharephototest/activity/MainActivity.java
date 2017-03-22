package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newProjectBtn = (Button)findViewById(R.id.new_project);
        Button loadProjectBtn = (Button)findViewById(R.id.load_project);

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
                Toast.makeText(getApplicationContext(), "준비중", Toast.LENGTH_LONG).show();
            }
        });
    }
}
