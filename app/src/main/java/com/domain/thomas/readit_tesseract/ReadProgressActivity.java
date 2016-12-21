package com.domain.thomas.readit_tesseract;

import android.content.pm.ActivityInfo;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.googlecode.tesseract.android.*;
import com.googlecode.tesseract.android.TessBaseAPI;

import static com.domain.thomas.readit_tesseract.MainActivity.previewImage;

public class ReadProgressActivity extends AppCompatActivity {

    private Button stopAnalysis;
    private ProgressBar ocrProgress;
    public ImageView progressImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_progress);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        stopAnalysis = (Button) findViewById(R.id.stopAnalysis);
        ocrProgress = (ProgressBar) findViewById(R.id.progressBar);
        progressImage = (ImageView) findViewById(R.id.progressImageView);


        progressImage.setImageBitmap(previewImage);

        stopAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.picOCR.stop();
                ocrProgress.setProgress(0);
                finish();
            }
        });

        Runnable r = new Runnable() {
            @Override
            public void run() {

                while (MainActivity.OCRThread) {
                    ocrProgress.setProgress(MainActivity.progress);
                }
                ocrProgress.setProgress(0);
                finish();
            }
        };

        Thread OCRProgress = new Thread(r, "OCRProgress");
        OCRProgress.start();
    }
}
