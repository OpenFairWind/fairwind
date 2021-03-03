package it.uniparthenope.fairwind.sdk.maps.source.kmllayer;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferencesDialog;

/**
 * Created by raffaelemontella on 11/08/16.
 */
public class KmlLayerDialog extends MapPreferencesDialog {
    public static final int FILE_CODE_FILENAME=001;



    @Override
    public int getLayoutId() {
        return R.layout.dialog_kmllayer;
    }

    @Override
    public void onInit() {

        KmlLayerPreferences kmlLayerPreferences =(KmlLayerPreferences)mapPreferences;
        final EditText editTextUrl=(EditText)view.findViewById(R.id.editText_url);
        editTextUrl.setText(kmlLayerPreferences.getUrl());
        Button button_browse=(Button)view.findViewById(R.id.button_browse);
        button_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                String filename=editTextUrl.getText().toString();
                if (filename.startsWith("file://")) {
                    i.putExtra(FilePickerActivity.EXTRA_START_PATH, filename.replace("file://",""));
                }
                startActivityForResult(i, FILE_CODE_FILENAME);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"onActivityResult -> onActivityResult");
        if (requestCode == FILE_CODE_FILENAME && resultCode == Activity.RESULT_OK) {
            EditText editFileNmae=(EditText)view.findViewById(R.id.editText_url);
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            editFileNmae.setText("file://"+uri.getPath());
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

                editFileNmae.setText("file://"+uri.getPath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFinish() {
        KmlLayerPreferences kmlLayerPreferences =(KmlLayerPreferences)mapPreferences;
        EditText editTextUrl=(EditText)view.findViewById(R.id.editText_url);
        kmlLayerPreferences.setUrl(editTextUrl.getText().toString());
    }
}
