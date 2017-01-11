package com.domain.thomas.readit_tesseract;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import static com.domain.thomas.readit_tesseract.EditImageActivity.previewImage;

public class ReadProgressActivity extends AppCompatActivity {

    private Button stopAnalysis;
    private ProgressBar ocrProgress;
    public ImageView progressImage;

    @Override
    public void onBackPressed() {
        EditImageActivity.picOCR.stop();
        ocrProgress.setProgress(0);
        EditImageActivity.stopRecognition = true;
        finish();
    }

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
                EditImageActivity.picOCR.stop();
                ocrProgress.setProgress(0);
                EditImageActivity.stopRecognition = true;
                finish();
            }
        });

        Runnable r = new Runnable() {
            @Override
            public void run() {

                while (EditImageActivity.OCRThread) {
                    ocrProgress.setProgress(EditImageActivity.progress);
                }
                ocrProgress.setProgress(0);
                EditImageActivity.stopRecognition = false;
                startEnd();
                finish();
            }
        };

        Thread OCRProgress = new Thread(r, "OCRProgress");
        OCRProgress.start();
    }
    public void startEnd(){
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }
}
