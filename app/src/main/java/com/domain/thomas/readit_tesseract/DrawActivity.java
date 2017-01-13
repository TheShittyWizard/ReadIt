package com.domain.thomas.readit_tesseract;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

public class DrawActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);

        setContentView(R.layout.activity_edit_image);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(MainActivity.imageURI, "image/*");
        intent.putExtra("crop", "true");
        // true to return a Bitmap, false to directly save the cropped iamge
        intent.putExtra("return-data", false);
        //save output image in uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,RESULT_OK);

        finish();
    }
}
