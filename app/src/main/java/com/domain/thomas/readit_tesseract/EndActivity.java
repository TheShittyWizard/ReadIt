package com.domain.thomas.readit_tesseract;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.content.ClipboardManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.domain.thomas.readit_tesseract.EditImageActivity.rawText;

public class EndActivity extends AppCompatActivity /*implements AdapterView.OnItemSelectedListener*/ {

    private String dirName = "ReadIt";
    private String parentDirName = Environment.DIRECTORY_DOCUMENTS;
    private String fileName = "readit1.txt";

    private final Context context = this;

    public static TextView editText;
    private Button copyText;
    private Button saveFile;
    private TextView filename;
    private Spinner mimeSelection;

    private String saveMimeType = ".txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editText = (TextView) findViewById(R.id.editText);
        copyText = (Button) findViewById(R.id.buttonCopy);
        saveFile = (Button) findViewById(R.id.buttonSave);

        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied text from readit", editText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "text copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

                View mView = getLayoutInflater().inflate(R.layout.dialog_save, null);
                filename = (TextView) mView.findViewById(R.id.filename);
                mimeSelection = (Spinner) mView.findViewById(R.id.spinnerDataType);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                        R.array.mime_type_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mimeSelection.setAdapter(adapter);

                mBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveMimeType = mimeSelection.getSelectedItem().toString();
                        fileName = filename.getText() + saveMimeType;
                        saveFile();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        //set the text to the text box
        editText.setText(rawText);
    }

    private void saveFile() {
        if (isExternalStorageWritable()) {
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

