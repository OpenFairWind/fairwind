package it.uniparthenope.fairwind.services.file;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.Serializable;
import java.util.ArrayList;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 29/07/16.
 */
public class NMEA0183PlayerDialog extends DataListenerPreferencesDialog {

    public static final String LOG_TAG="jjj";

    public static final int FILE_CODE_NMEAPLAYERDATASOURCE_FILENAME=001;

    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183player;
    }



    @Override
    public void onInit() {
        NMEA0183PlayerPreferences nmea0183PlayerPreferences=(NMEA0183PlayerPreferences)dataListenerPreferences;
        final EditText editFileNmae=(EditText) view.findViewById(R.id.edit_fileName);
        EditText editMillis=(EditText) view.findViewById(R.id.edit_millis);

        editFileNmae.setText(nmea0183PlayerPreferences.getFileName());
        editMillis.setText(String.format("%d",nmea0183PlayerPreferences.getMillis()));

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
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, editFileNmae.getText().toString());

                startActivityForResult(i, FILE_CODE_NMEAPLAYERDATASOURCE_FILENAME);

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"onActivityResult -> onActivityResult");
        if (requestCode == FILE_CODE_NMEAPLAYERDATASOURCE_FILENAME && resultCode == Activity.RESULT_OK) {
            EditText editFileNmae=(EditText) view.findViewById(R.id.edit_fileName);
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            editFileNmae.setText(uri.getPath());
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
                            editFileNmae.setText(uri.getPath());
                            break;
                        }
                    }
                }

            } else {
                Uri uri = data.getData();

                editFileNmae.setText(uri.getPath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    public void onFinish() {
        NMEA0183PlayerPreferences nmea0183PlayerPreferences=(NMEA0183PlayerPreferences)dataListenerPreferences;
        EditText editFileNmae=(EditText)view.findViewById(R.id.edit_fileName);
        EditText editMillis=(EditText)view.findViewById(R.id.edit_millis);

        nmea0183PlayerPreferences.setFileName(editFileNmae.getText().toString());
        nmea0183PlayerPreferences.setMillis(Integer.parseInt(editMillis.getText().toString()));

    }
}
