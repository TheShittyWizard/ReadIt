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
import android.widget.TextView;

import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {

    private static final String TITLE = "tesseracttest";
    private static final String DESCRIPTION = "Ein mit der App tesseracttest aufgenommenes Foto";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private TextView textView;

    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.view);
        textView = (TextView) findViewById(R.id.ReadText);
        Button button = (Button) findViewById(R.id.shoot);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startCamera();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {

                    final Bitmap b1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    float w1 = b1.getWidth();
                    float h1 = b1.getHeight();

                    int h2 = 300;
                    int w2 = (int) (w1 / h1 * (float) h2);

                    Bitmap b2 = Bitmap.createScaledBitmap(b1, w2, h2, false);
                    imageView.setImageBitmap(b2);

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            TessBaseAPI bildOcr = new TessBaseAPI();
                            bildOcr.init("storage/emulated/0/","deu");
                            bildOcr.setImage(b1);
                            textView.setText( bildOcr.getUTF8Text());
                            bildOcr.end();
                        }
                    };
                    Thread t = new Thread(r);
                    t.start();

                    int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                    Log.d(TAG, rowsDeleted + " rows deleted");

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
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }
}
