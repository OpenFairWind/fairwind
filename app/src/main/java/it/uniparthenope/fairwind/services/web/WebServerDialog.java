package it.uniparthenope.fairwind.services.web;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;
import it.uniparthenope.fairwind.sdk.util.Decompress;

/**
 * Created by raffaelemontella on 28/07/16.
 */
public class WebServerDialog extends DataListenerPreferencesDialog {


    private static final int FILE_CODE_WEBSERVER_DOCUMENTROOT=001;

    private static long downloadReference;
    private static DownloadManager downloadManager;


    @Override
    public int getLayoutId() {
        return R.layout.dialog_webserver;
    }

    @Override
    public void onInit() {
        //setTitle("Web Server Setup");
        WebServerPreferences webServerPreferences=(WebServerPreferences)dataListenerPreferences;
        final EditText editDocumentRoot=(EditText)view.findViewById(R.id.edit_documentRoot);
        EditText editPortWebSocket=(EditText)view.findViewById(R.id.edit_portWebSocket);
        EditText editPortWebService=(EditText)view.findViewById(R.id.edit_portWebService);

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
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, editDocumentRoot.getText().toString());

                startActivityForResult(i, FILE_CODE_WEBSERVER_DOCUMENTROOT);

            }
        });

        Button buttonDownloadApps=(Button)view.findViewById(R.id.button_download_apps);
        buttonDownloadApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString="http://web.uniparthenope.it/~osp/apps.zip";
                Uri uri=Uri.parse(urlString);

                Log.d(LOG_TAG,"Uri: "+uri);
                try {
                    File outputDir = getPreferenceFragment().getActivity().getCacheDir(); // context being the Activity pointer
                    File destinationFilename = File.createTempFile("app-", ".zip", outputDir);

                    Log.d(LOG_TAG,"Apps Download -> "+ uri.toString() + " -- " + uri.getLastPathSegment());

                    downloadManager = (DownloadManager)getPreferenceFragment().getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    if (request!=null) {
                        request.setDestinationInExternalPublicDir(outputDir.getAbsolutePath(), destinationFilename.getName());
                        request.setVisibleInDownloadsUi(true);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                        request.setDescription("...").setTitle("Download apps: " + destinationFilename);
                        downloadReference = downloadManager.enqueue(request);
                        Log.d(LOG_TAG,"Apps Download -> "+downloadReference);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG,e.getMessage());
                }

            }
        });

        String wwwDocumentRoot=webServerPreferences.getWwwDocumentRoot();
        if (wwwDocumentRoot==null) {
            wwwDocumentRoot="";
        }
        editDocumentRoot.setText(wwwDocumentRoot);
        editPortWebSocket.setText(String.format("%d",webServerPreferences.getPortWebSocket()));
        editPortWebService.setText(String.format("%d",webServerPreferences.getPortWebService()));
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            Log.d(LOG_TAG,"downloadReceiver: "+referenceId);

            if(downloadReference == referenceId){

                int ch;
                ParcelFileDescriptor file;
                StringBuffer strContent = new StringBuffer("");
                StringBuffer countryData = new StringBuffer("");

                //parse the JSON data and display on the screen
                try {
                    file = downloadManager.openDownloadedFile(downloadReference);
                    FileInputStream fileInputStream
                            = new ParcelFileDescriptor.AutoCloseInputStream(file);

                    View view=(View)intent.getSerializableExtra("view");
                    EditText editDocumentRoot=(EditText)view.findViewById(R.id.edit_documentRoot);
                    String wwwDocumentRootString = editDocumentRoot.getText().toString();
                    Log.d(LOG_TAG,"downloadReceiver -> " + wwwDocumentRootString);

                    Decompress decompress=new Decompress(fileInputStream,wwwDocumentRootString+File.separator);
                    decompress.unzip();

                    Toast toast = Toast.makeText(getPreferenceFragment().getActivity(),
                            "Downloading of apps just finished", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();

                } catch (FileNotFoundException e) {
                    Log.e(LOG_TAG,e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"onActivityResult -> onActivityResult");
        if (requestCode == FILE_CODE_WEBSERVER_DOCUMENTROOT && resultCode == Activity.RESULT_OK) {
            EditText editDocumentRoot=(EditText)view.findViewById(R.id.edit_documentRoot);
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            editDocumentRoot.setText(uri.getPath());
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
                            editDocumentRoot.setText(uri.getPath());
                            break;
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                editDocumentRoot.setText(uri.getPath());
            }
        }
    }

    @Override
    public void onFinish() {
        WebServerPreferences webServerPreferences=(WebServerPreferences)dataListenerPreferences;
        EditText editDocumentRoot=(EditText)view.findViewById(R.id.edit_documentRoot);
        EditText editPortWebSocket=(EditText)view.findViewById(R.id.edit_portWebSocket);
        EditText editPortWebService=(EditText)view.findViewById(R.id.edit_portWebService);

        webServerPreferences.setWwwDocumentRoot(editDocumentRoot.getText().toString());
        webServerPreferences.setPortWebSocket(Integer.parseInt(editPortWebSocket.getText().toString()));
        webServerPreferences.setPortWebService(Integer.parseInt(editPortWebService.getText().toString()));

    }
}
