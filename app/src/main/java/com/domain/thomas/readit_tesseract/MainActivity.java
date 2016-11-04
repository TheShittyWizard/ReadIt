package com.domain.thomas.readit_tesseract;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {

    private static final String TITLE = "tesseracttest";
    private static final String DESCRIPTION = "Ein mit der App tesseracttest aufgenommenes Foto";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IMAGE_CAPTURE = 1;
    private static final int IMG_RESULT = 2;

    private ImageView imageView;
    private TextView edittextView;
    private Button LoadImage;
    private Button GetImage;

    private Uri imageURI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imageView = (ImageView) findViewById(R.id.view);
        LoadImage = (Button) findViewById(R.id.buttonGetPicture);
        GetImage = (Button) findViewById(R.id.buttonTakePicture);
        edittextView = (TextView) findViewById(R.id.editText);

        LoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, IMG_RESULT);
            }
        });
        GetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_RESULT && resultCode == RESULT_OK
                && null != data) {

            imageURI = data.getData();

            Bitmap b1 = null;
            try {
                b1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
            } catch (IOException e) {
                Toast.makeText(this, "Something bad happened while getting a bitmap. (1)", Toast.LENGTH_LONG)
                        .show();
            }

            final Bitmap bt = b1;

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    final TessBaseAPI bildOcr = new TessBaseAPI();
                    bildOcr.init("storage/emulated/0/","deu");
                    bildOcr.setImage(bt);
                    Log.d(TAG, "Bild gesetzt");

                    final String readText = bildOcr.getUTF8Text();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edittextView.setText(readText);
                        }
                    });

                    Log.d(TAG, "Text gelesen");
                    bildOcr.end();
                    Log.d(TAG, "OCR beendet");
                }
            };
            Thread t = new Thread(r);
            t.start();

            Bitmap b2 = bmDownscale(b1, 480);
            imageView.setImageBitmap(b2);

        } else if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap b1 = null;
            try {
                b1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
            } catch (IOException e) {
                Toast.makeText(this, "Something bad happened while getting a bitmap. (2)", Toast.LENGTH_LONG)
                        .show();
            }

            final Bitmap bt = b1;

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    final TessBaseAPI bildOcr = new TessBaseAPI();
                    bildOcr.init("storage/emulated/0/","deu");
                    bildOcr.setImage(bt);
                    Log.d(TAG, "Bild gesetzt");

                    final String readText = bildOcr.getUTF8Text();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edittextView.setText(readText);
                        }
                    });

                    Log.d(TAG, "Text gelesen");
                    bildOcr.end();
                    Log.d(TAG, "OCR beendet");
                }
            };
            Thread t = new Thread(r);
            t.start();

            Bitmap b2 = bmDownscale(b1, 480);
            imageView.setImageBitmap(b2);
        }
    }

    private void startCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    public Bitmap bmDownscale(Bitmap bm, int hResTarget) {
        float w1 = bm.getWidth();
        float h1 = bm.getHeight();

        int w2 = (int) (w1 / h1 * (float) hResTarget);

        return Bitmap.createScaledBitmap(bm, w2, hResTarget, false);
    }
}
