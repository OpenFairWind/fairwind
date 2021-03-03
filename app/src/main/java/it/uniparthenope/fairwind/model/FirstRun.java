package it.uniparthenope.fairwind.model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.FileTools;

/**
 * Created by raffaelemontella on 21/09/16.
 */



public class FirstRun extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = "FIRSTRUN";
    private FairWindModelImpl fairWindModel;
    String extStorageDirectory=null;

    public FirstRun(FairWindModelImpl fairWindModel) {
        this.fairWindModel=fairWindModel;
        File externalFilesDir=FairWindApplication.getInstance().getExternalFilesDir(null);
        extStorageDirectory=externalFilesDir.getAbsolutePath()+File.separator;
        new File(extStorageDirectory).mkdirs();

    }




    private void webServer() {
        Log.d(LOG_TAG,"webServer");

        /*
        String destAppsRoot=destRoot +  File.separator +"apps"+ File.separator ;
        File appsDocumentRoot = new File(destAppsRoot);

        if (!appsDocumentRoot.exists()) {
            appsDocumentRoot.mkdirs();
            Log.d(LOG_TAG,"Document root created");
        }

        String[] apps={"Kindle_App_Example","ikommunicate-Test","simplegauges","instrumentpanel","sailgauge"};
        for (String app:apps) {
            try {
                Log.d(LOG_TAG, "Installing: "+app);
                FileTools.unzipFromAssets(application, "apps/"+app+".zip", destAppsRoot);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        */
    }

    @Override
    protected String doInBackground(String... params) {
        Application application=FairWindApplication.getInstance();
        FileTools.assetsCopyDirectory(application,"nmea",extStorageDirectory);
        FileTools.assetsCopyDirectory(application,"www",extStorageDirectory);
        FileTools.assetsCopyDirectory(application,"logs",extStorageDirectory);
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        fairWindModel.getPreferences().setConfigPropertyBoolean("firstrun", false);
        Toast.makeText(FairWindApplication.getInstance(),"First initialization done!",Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "First initialization done!");
    }


}

