package com.domain.thomas.readit_tesseract;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {

    //values to differentiate the Activities
    private static final int IMAGE_CAPTURE = 1;
    private static final int IMAGE_GALLERY = 2;

    public static int progress;
    public static Bitmap imageBm;
    public static Bitmap previewImage;
    public static boolean stopRecognition;
    public static Boolean OCRThread = false;

    public static final TessBaseAPI picOCR = new TessBaseAPI(new TessBaseAPI.ProgressNotifier() {
        @Override
        //gets the percentage value of the progress
        public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
            progress = progressValues.getPercent();
        }
    });

    //variables for ui elements
    private TextView editText;
    private Button loadImage;
    private Button getImage;
    private Button reset;
    private Uri imageURI;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //assign the ui elements to the variables
        loadImage = (Button) findViewById(R.id.buttonGetPicture);
        getImage = (Button) findViewById(R.id.buttonTakePicture);
        editText = (TextView) findViewById(R.id.editText);
        reset = (Button) findViewById(R.id.buttonReset);

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set intent to pick a picture from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, IMAGE_GALLERY);
            }
        });

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY && resultCode == RESULT_OK
                && null != data) {

            //get the URI of the image from Data
            imageURI = data.getData();

            //get the bitmap of the image and
            imageBm = null;
            try {
                imageBm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                startOCR(imageBm);
            } catch (IOException e) {
                Toast.makeText(this, "Something bad happened while getting a bitmap. (1)", Toast.LENGTH_LONG)
                        .show();
            }

        } else if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {

           imageBm = null;
            try {
                imageBm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                startOCR(imageBm);
            } catch (IOException e) {
                Toast.makeText(this, "Something bad happened while getting a bitmap. (2)", Toast.LENGTH_LONG)
                        .show();
            }
            //delete image after use
            getContentResolver().delete(imageURI, null, null);
        }
    }

    private void startCamera() {

        //create a uri for the new picture
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //set intent to make an image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //add the uri to the taken image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    public void startOCR(final Bitmap bm) {
        final String TAG = MainActivity.class.getSimpleName();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //show that a OCRThread is active
                OCRThread = true;

                //set parameters for the recognition
                picOCR.init("storage/emulated/0/", "deu");
                picOCR.setImage(bm);

                //start the recognition
                final String readText = picOCR.getHOCRText(0);

                //filter the actual text out of readText
                final String rawText = parseHOCRText(readText);

                //set the text to the textbox
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText.setText(rawText);
                    }
                });

                //end recognition
                picOCR.end();

                //thread now inactive
                OCRThread = false;
            }
        };

        //reset recognition progress and start recognition
        progress = 0;
        Thread OCR = new Thread(r, "OCR");
        OCR.start();

        //get size of the display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //scale image to display size
        previewImage = bmDownscale(bm, size.x * 3 / 4);

        //start the ProgressActivity
        Intent intent = new Intent(this, ReadProgressActivity.class);
        startActivity(intent);
    }

    public Bitmap bmDownscale(Bitmap bm, int hResTarget) {
        float w1 = bm.getWidth();
        float h1 = bm.getHeight();

        int w2 = (int) (w1 / h1 * (float) hResTarget);

        return Bitmap.createScaledBitmap(bm, w2, hResTarget, false);
    }

    public String parseHOCRText(final String text) {
        //don't filter text if recognition was stopped
        if(stopRecognition == false) {
            String rawText = text.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " "); //replace html tags with spaces
            rawText = rawText.replaceFirst("   ", ""); //remove access spaces at the start

            return rawText;
        }
        return text;
    }
}












