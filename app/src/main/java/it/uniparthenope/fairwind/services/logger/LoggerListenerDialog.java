package it.uniparthenope.fairwind.services.logger;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 03/03/2017.
 */

public class LoggerListenerDialog extends DataListenerPreferencesDialog {
    public static final String LOG_TAG="LOGGER_DIALOG";

    private static final int FILE_CODE_LOGGER_BOARDLOGPATH=001;




    @Override
    public int getLayoutId() {
        return R.layout.dialog_logger;
    }

    @Override
    public void onInit() {
        //setTitle("Web Server Setup");
        LoggerListenerPreferences loggerListenerPreferences =(LoggerListenerPreferences)dataListenerPreferences;
        final CheckBox checkBoxUpload=(CheckBox)view.findViewById(R.id.checkbox_upload);
        final EditText editBoardLogPath=(EditText)view.findViewById(R.id.edit_boardlogpath);
        final EditText editCutSecs=(EditText)view.findViewById(R.id.edit_cutsecs);
        final EditText editFullSecs=(EditText)view.findViewById(R.id.edit_fullsecs);


        Button buttonBrowse=(Button)view.findViewById(R.id.button_browse);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This always works
                Intent i = new Intent(getPreferenceFragment().getActivity(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, editBoardLogPath.getText().toString());

                startActivityForResult(i, FILE_CODE_LOGGER_BOARDLOGPATH);

            }
        });


        boolean upload=loggerListenerPreferences.getUpload();
        checkBoxUpload.setChecked(upload);
        /*
        checkBoxUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkBoxUpload.setChecked(b);
            }
        });
        */
        String boardLogPath= loggerListenerPreferences.getBoardLogPath();
        if (boardLogPath==null) {
            boardLogPath="";
        }
        editBoardLogPath.setText(boardLogPath);
        editCutSecs.setText(String.format("%d", loggerListenerPreferences.getCutMillis()/1000));
        editFullSecs.setText(String.format("%d", loggerListenerPreferences.getFullMillis()/1000));
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"onActivityResult -> onActivityResult");
        if (requestCode == FILE_CODE_LOGGER_BOARDLOGPATH && resultCode == Activity.RESULT_OK) {
            EditText editBoardLogPath=(EditText)view.findViewById(R.id.edit_boardlogpath);
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            editBoardLogPath.setText(uri.getPath());
                            break;
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            editBoardLogPath.setText(uri.getPath());
                            break;
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                editBoardLogPath.setText(uri.getPath());
            }
        }
    }

    @Override
    public void onFinish() {
        LoggerListenerPreferences loggerListenerPreferences =(LoggerListenerPreferences) dataListenerPreferences;

        CheckBox checkBoxUpload=(CheckBox)view.findViewById(R.id.checkbox_upload);
        EditText editBoardLogPath=(EditText)view.findViewById(R.id.edit_boardlogpath);
        EditText editCutSecs=(EditText)view.findViewById(R.id.edit_cutsecs);
        EditText editFullSecs=(EditText)view.findViewById(R.id.edit_fullsecs);


        boolean upload=checkBoxUpload.isChecked();
        loggerListenerPreferences.setUpload(upload);
        loggerListenerPreferences.setBoardLogPath(editBoardLogPath.getText().toString());
        loggerListenerPreferences.setCutMillis(Long.parseLong(editCutSecs.getText().toString())*1000);
        loggerListenerPreferences.setFullMillis(Long.parseLong(editFullSecs.getText().toString())*1000);

    }
}
