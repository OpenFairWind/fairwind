package it.uniparthenope.fairwind.services.logger;


import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by raffaelemontella on 10/11/2017.
 */

public class AndroidUploadTask  extends UploadTaskBase {

    public static final String LOG_TAG="ANDROID UPLOAD TASK";

    public AndroidUploadTask(String httpClientClassName, String uploadUrl, Uploader uploader) {
        super(httpClientClassName, uploadUrl, uploader);
    }

    public void execute(File[] files) {
        UploadTask uploadTask=new UploadTask();
        uploadTask.execute(files);
    }

    private class UploadTask extends AsyncTask<File[], Integer, Long> {

        @Override
        protected Long doInBackground(File[]... files) {
            Log.d(LOG_TAG,"UPLOADTASK :"+files[0].length);
            for (File file : files[0]) {
                if (isUploading(file.getAbsolutePath()) == false) {
                    Log.d(LOG_TAG, "UPLOADTASK :" + getCurrentClients() + "/" + getAvailableClients());
                    if (getAvailableClients() - getCurrentClients() > 0) {
                        try {

                            Class<?> clazz = Class.forName(getHttpClientClassName());
                            Constructor<?> constructor = clazz.getConstructor(Uploader.class);
                            AsyncHttpClientBase client = (AsyncHttpClientBase) constructor.newInstance(getUploader());
                            client.post(getUploadUrl(), file);
                            Log.d(LOG_TAG,"UPLOADTASK :"+getCurrentClients()+"/"+getAvailableClients()+" | "+file.getAbsolutePath());


                        } catch (ClassNotFoundException ex1) {
                            throw new RuntimeException(ex1);
                        } catch (NoSuchMethodException ex2) {
                            throw new RuntimeException(ex2);
                        } catch (IllegalAccessException ex3) {
                            throw new RuntimeException(ex3);
                        } catch (InvocationTargetException ex4) {
                            throw new RuntimeException(ex4);
                        } catch (InstantiationException ex5) {
                            throw new RuntimeException(ex5);
                        }


                    } else {
                        Log.d(LOG_TAG, "No available clients");
                        break;
                    }
                }
            }
            return null;
        }
    }
}


