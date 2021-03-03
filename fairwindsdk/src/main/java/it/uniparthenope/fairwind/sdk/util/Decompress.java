package it.uniparthenope.fairwind.sdk.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by raffaelemontella on 13/01/16.
 */
public class Decompress  extends AsyncTask<String, String, String> {
    private static final String LOG_TAG = "DECOMPRESS";

    private InputStream _is;
    private String _location;


    public Decompress(InputStream is, String location) {
        _is = is;
        _location = location;


        _dirChecker("");
    }

    public void unzip() {
        this.execute();

    }

    private void _dirChecker(String dir) {
        String path=_location+dir;
        File f = new File(path);

        if(!f.isDirectory()) {
            Log.d(LOG_TAG,"Creating: "+path);
            f.mkdirs();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try  {
            ZipInputStream zin = new ZipInputStream(_is);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v(LOG_TAG, "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    try {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }


                        fout.close();
                    } catch (FileNotFoundException e) {
                        Log.e(LOG_TAG,e.getMessage());
                    }
                }
                zin.closeEntry();
            }
            zin.close();
        } catch(Exception e) {
            Log.e(LOG_TAG, "unzip", e);
        }
        return null;
    }
}
