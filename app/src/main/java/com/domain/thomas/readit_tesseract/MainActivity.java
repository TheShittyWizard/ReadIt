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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //values to differentiate the Activities
    private static final int IMAGE_CAPTURE = 1;
    private static final int IMAGE_GALLERY = 2;

    public static Bitmap imageBm;

    //variables for ui elements
    private TextView editText;
    private Button loadImage;
    private Button getImage;
    private Button reset;
    public static Uri imageURI;


    @Override
    protected void onStart(){
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //textview.setText(item.getTitle());
        if(android.R.id.home == item.getItemId()){
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        }
        return true;
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
                Intent intent = new Intent(this, EditImageActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                Toast.makeText(this, "Something bad happened while getting a bitmap. (1)", Toast.LENGTH_LONG)
                        .show();
            }

        } else if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {

           imageBm = null;
            try {
                imageBm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                Intent intent = new Intent(this, EditImageActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                Toast.makeText(this, "Something bad happened while getting a bitmap. (2)", Toast.LENGTH_LONG)
                        .show();
            }
            //delete image after use
            //getContentResolver().delete(imageURI, null, null);
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
}












