package com.domain.thomas.readit_tesseract;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {

    private static final String TITLE = "tesseracttest";
    private static final String DESCRIPTION = "Ein mit der App tesseracttest aufgenommenes Foto";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IMAGE_CAPTURE = 1;

    private ImageView imageView;

    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.view);
        Button button = (Button) findViewById(R.id.shoot);
        Button buttonDelete = (Button) findViewById(R.id.delete);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startCamera();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap b1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    float w1 = b1.getWidth();
                    float h1 = b1.getHeight();

                    int h2 = 300;
                    int w2 = (int) (w1 / h1 * (float) h2);

                    Bitmap b2 = Bitmap.createScaledBitmap(b1, w2, h2, false);
                    imageView.setImageBitmap(b2);
                } catch (IOException e) {
                    Log.e(TAG, "setBitmap()", e);
                }
            } else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }
    }

    private void startCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        //grantUriPermission("com.android.providers.media.MediaProvider", imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }
}
