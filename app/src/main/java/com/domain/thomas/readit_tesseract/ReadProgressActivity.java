package com.domain.thomas.readit_tesseract;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.googlecode.tesseract.android.*;
import com.googlecode.tesseract.android.TessBaseAPI;

public class ReadProgressActivity extends AppCompatActivity {

    private Button stopAnalysis;
    private ProgressBar ocrProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_progress);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        stopAnalysis = (Button) findViewById(R.id.stopAnalysis);
        ocrProgress = (ProgressBar) findViewById(R.id.progressBar);



        stopAnalysis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.picOCR.stop();
                finish();
            }
        });
    }
}
