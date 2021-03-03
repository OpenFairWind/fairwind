package it.uniparthenope.fairwind.services.file;


import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.NMEA0183Listener;

/**
 * Created by raffaelemontella on 18/09/15.
 */
public class NMEA0183Player extends NMEA0183Listener implements IDataListenerPreferences, Runnable {

    private static final String LOG_TAG = "NMEA0183Player";



    private boolean bPause=false;
    private String fileName="";
    private long millis=500;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String sentence;

    private ExecutorService mExecutor;

    @Override
    public boolean isLicensed() {
        return true;
    }

    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }



    public NMEA0183Player() {
    }

    public NMEA0183Player(String name, String fileName, long millis) {
        super(name);
        this.fileName=fileName;
        this.millis=millis;
        init();
     }

    public NMEA0183Player(NMEA0183PlayerPreferences prefs) {
        super(prefs.getName());
        this.fileName=prefs.getFileName();
        this.millis=prefs.getMillis();
        init();
    }

    private void init() {
        Log.d(LOG_TAG,"NMEA0183Player -> "+getName()+" "+fileName+" "+millis);
    }

    @Override
    public void onStart() throws DataListenerException {
        try {

            fileReader =new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            mExecutor=Executors.newSingleThreadExecutor();
            mExecutor.submit(this);
            play();
        } catch (IOException ioException) {
            throw new DataListenerException(ioException);
        }
    }

    @Override
    public void onStop() {
    }

    @Override
    public void send(String sSentence) throws DataListenerException {

    }

    @Override
    public boolean mayIUpdate() {
        return false;
    }

    @Override
    public void run() {
        while (!isDone()) {
            if (!bPause) {
                try {
                    sentence = bufferedReader.readLine();
                    if (sentence != null && !sentence.isEmpty()) {
                        Log.d(LOG_TAG, "process:"+sentence);
                        process(sentence);
                        Log.d(LOG_TAG,"process:done");
                        try {
                            Thread.sleep(millis);
                        } catch (InterruptedException e) {
                            Log.e(LOG_TAG,e.getMessage());
                        }

                    } else {
                        setDone();
                        try {
                            start();
                        } catch (DataListenerException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                        Log.d(LOG_TAG, "Terminated");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG,e.getMessage());
                }
            }
        }

        try {
            bufferedReader.close();
            fileReader.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void pause() {
        bPause=true;
    }

    public void play() {
        bPause=false;
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new NMEA0183Player((NMEA0183PlayerPreferences)prefs);
    }
}
