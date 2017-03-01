package com.domain.thomas.readit_tesseract;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.googlecode.tesseract.android.TessBaseAPI;

import static com.domain.thomas.readit_tesseract.MainActivity.imageBm;

public class EditImageActivity extends AppCompatActivity {

    private Button fullRecognition;
    private Button manualDefinition;
    private Button automaticDefinition;
    public ImageView editImage;
    public SurfaceView draw;
    public Spinner langSelection;

    public static Boolean OCRThread = false;
    public static Bitmap previewImage;
    public static int progress;
    public static boolean stopRecognition;
    public static String rawText;

    private String lang = "deu";

    public static final TessBaseAPI picOCR = new TessBaseAPI(new TessBaseAPI.ProgressNotifier() {
        @Override
        //gets the percentage value of the progress
        public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
            progress = progressValues.getPercent();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_image);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fullRecognition = (Button) findViewById(R.id.fullRecognition);
        manualDefinition = (Button) findViewById(R.id.manualDefinition);
        automaticDefinition = (Button) findViewById(R.id.automaticDefinition);
        editImage = (ImageView) findViewById(R.id.editImage);
        langSelection = (Spinner) findViewById(R.id.spinnerLanguage);

        //get size of the display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //scale image to display size
        previewImage = bmDownscale(imageBm, size.x * 3 / 4);

        editImage.setImageBitmap(previewImage);

        fullRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lang = langSelection.getSelectedItem().toString();

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //show that a OCRThread is active
                        OCRThread = true;

                        //set parameters for the recognition
                        picOCR.init("storage/emulated/0/", lang);
                        picOCR.setImage(imageBm);

                        //start the recognition
                        final String readText = picOCR.getHOCRText(0);

                        //filter the actual text out of readText
                        rawText = parseHOCRText(readText);

                        //end recognition
                        picOCR.end();

                        //thread now inactive
                        OCRThread = false;
                    }
                };

                if (OCRThread == false) {
                    //reset recognition progress and start recognition
                    progress = 0;
                    Thread OCR = new Thread(r, "OCR");
                    OCR.start();

                    startProgress();
                }
            }
        });
        manualDefinition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                startDraw();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSelection.setAdapter(adapter);
    }

    public Bitmap bmDownscale(Bitmap bm, int hResTarget) {
        float w1 = bm.getWidth();
        float h1 = bm.getHeight();

        int w2 = (int) (w1 / h1 * (float) hResTarget);

        return Bitmap.createScaledBitmap(bm, w2, hResTarget, false);
    }

    public String parseHOCRText(final String text) {
        //don't filter text if recognition was stopped
        if (stopRecognition == false) {
            String rawText = text.replaceAll("(?s)(\\<.*?\\>)", ""); //delete all html tags
            rawText = rawText.replaceAll("(?s)(     \n)", ""); //delete double newlines
            rawText = rawText.replaceFirst("(?s)(  \n   \n    \n)", "");
            rawText = rawText.replaceAll("     ", ""); //delete unnecessary spaces
            rawText = rawText.replaceAll("    \n" + "   \n" + "  \n", "");

            return rawText;
        }
        return text;
    }

    public void startProgress() {
        //start the ProgressActivity
        Intent intent = new Intent(this, ReadProgressActivity.class);
        startActivity(intent);
    }

    public void startDraw() {
        Intent intent = new Intent(this, OpenGLES20Activity.class);
        startActivity(intent);
    }
}
