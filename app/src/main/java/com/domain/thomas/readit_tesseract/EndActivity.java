package com.domain.thomas.readit_tesseract;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static com.domain.thomas.readit_tesseract.EditImageActivity.rawText;

public class EndActivity extends AppCompatActivity{

    public static TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editText = (TextView) findViewById(R.id.editText);

        //set the text to the textbox
        editText.setText(rawText);
    }
}
