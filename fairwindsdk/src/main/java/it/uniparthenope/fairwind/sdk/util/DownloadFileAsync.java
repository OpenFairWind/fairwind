package it.uniparthenope.fairwind.sdk.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by raffaelemontella on 13/01/16.
 */
public class DownloadFileAsync extends AsyncTask<String, String, String> {
    private static final String LOG_TAG = "DOWNLOAD_FILE_ASYNC";

    private File documentRoot=null;

    public DownloadFileAsync(File documentRoot) {
        super();
        this.documentRoot=documentRoot;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        Log.d(LOG_TAG,"Pre Executed");
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {

            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();
            Log.d(LOG_TAG, "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            String localFilename=documentRoot.getAbsolutePath()+ File.separator+"temp.zip";
            OutputStream output = new FileOutputStream(localFilename);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            FileInputStream fileInputStream=new FileInputStream(localFilename);
            Decompress decompress=new Decompress(fileInputStream,documentRoot.getAbsolutePath()+File.separator);
            decompress.unzip();
        } catch (Exception e) {
            Log.d(LOG_TAG,e.getMessage());
            e.printStackTrace();
        }
        return null;

    }
    protected void onProgressUpdate(String... progress) {
        Log.d(LOG_TAG,progress[0]);
        //mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String unused) {
        //dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        Log.d(LOG_TAG,"Post Executed");
    }
}
