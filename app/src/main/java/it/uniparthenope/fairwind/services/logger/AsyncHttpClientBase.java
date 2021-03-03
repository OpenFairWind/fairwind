package it.uniparthenope.fairwind.services.logger;

import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.UUID;

import mjson.Json;

/**
 * Created by raffaelemontella on 11/07/2017.
 */

public abstract class AsyncHttpClientBase {
    public static final String LOG_TAG="HTTPCLIENTBASE";

    private Uploader uploader;


    private long timeStamp;
    private File file;
    private String sessionId= UUID.randomUUID().toString();

    public AsyncHttpClientBase(Uploader uploader) {
        this.uploader=uploader;

    }
    public Uploader getUploader() { return uploader; }
    public void setUploader(Uploader uploader) { this.uploader=uploader; }
    public String getSessionId() { return sessionId; }

    public File getFile() { return file; }
    public long getTimeStamp() { return timeStamp; }


    public abstract void onPost(String url, File file) throws PostException;

    public void post(String url, File file)  {
        if (file.exists()) {
            this.file = file;
            timeStamp = System.currentTimeMillis();
            uploader.put(sessionId, this);
            try {
                onPost(url, file);
            } catch (PostException ex) {
                String responseBody="{\"status\":\"fail\",\"message\":\""+ex.getMessage()+"\"}";
                failure(responseBody);
            }
        }
    }

    public void failure(String jsonString) {

        Json result=null;
        try {
            result=Json.read(jsonString);
        } catch (RuntimeException ex) {

        }
        if (result!=null) {
            Log.d(LOG_TAG, "result:" + result.toString());

            if (result.is("status", "fail")) {


            }
        }
        uploader.remove(sessionId);
    }

    public void success(String jsonString) {
        Object semaphore=new Object();
        Json result=null;
        try {
            result=Json.read(jsonString);
        } catch (RuntimeException ex) {

        }
        if (result!=null) {
            Log.d(LOG_TAG, "result:" + result.toString());

            if (result.is("status", "fail")) {
                uploader.remove(sessionId);

            } else if (result.is("status", "success")) {
                uploader.remove(sessionId,file);

            }
        }
    }
}

