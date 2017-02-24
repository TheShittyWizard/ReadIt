package com.domain.thomas.readit_tesseract;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.domain.thomas.readit_tesseract.EditImageActivity.rawText;

public class EndActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String dirName = "ReadIt";
    String parentDirName = Environment.DIRECTORY_DOCUMENTS;
    String fileName = "readit1.txt";

    final Context context = this;

    public static TextView editText;
    private Button saveFile;
    private TextView filename;
    private Button saveButton;
    private Spinner mimeSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editText = (TextView) findViewById(R.id.editText);
        saveFile = (Button) findViewById(R.id.buttonSave);

        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_save);

                saveButton = (Button) dialog.findViewById(R.id.buttonSaveConfirm);
                filename = (TextView) dialog.findViewById(R.id.filename);
                mimeSelection = (Spinner) dialog.findViewById(R.id.spinnerDataType);

                List<String> mimeTypes = new ArrayList();

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                        R.array.mime_type_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mimeSelection.setAdapter(adapter);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileName = filename.getText().toString() + ".txt";

                        saveFile();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //set the text to the textbox
        editText.setText(rawText);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    private void saveFile() {
        if (isExternalStorageWritable()) {
            //File file = getAlbumStorageDir("ReadIt");
            String text = editText.getText().toString();

            generateNoteOnSD(parentDirName, dirName, fileName, text);

            Toast.makeText(this, "file saved as " + fileName, Toast.LENGTH_LONG).show();
        }
    }

    public void generateNoteOnSD(String parentDir, String dirName, String fileName, String text) {
        try {
            File dirFile = new File(Environment.getExternalStoragePublicDirectory(parentDir), dirName);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(dirFile, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(this, "Something bad happened while saving the file.", Toast.LENGTH_LONG);
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}

