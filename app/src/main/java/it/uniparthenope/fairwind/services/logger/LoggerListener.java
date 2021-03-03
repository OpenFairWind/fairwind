package it.uniparthenope.fairwind.services.logger;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.Utils;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;

/**
 * Created by raffaelemontella on 09/12/15.
 */
public class LoggerListener extends DataListener implements IDataListenerPreferences, Runnable {
    private static final String LOG_TAG = "LOGGER_LISTENER";



    private int timeout=100;
    private String boardLogPath="";
    private boolean upload=false;
    private long millis=5000;
    private long updateMillis=15000;
    private long cutMillis=600000;
    private long fullMillis=300000;
    private long lastFullMillis=System.currentTimeMillis();
    private long lastCutMillis=lastFullMillis;
    private long lastUpdateMillis=lastFullMillis;
    private String currentFilename="";

    private HashSet<String> pathEvents=new HashSet<String>();

    private ExecutorService mExecutor;

    private Uploader uploader;

    public LoggerListener() {
        init();
    }

    // The constructor
    public LoggerListener(String name, boolean upload, String boardLogPath, long cutMillis, long fullMillis) {
        super(name);
        this.upload=upload;
        this.boardLogPath=boardLogPath;
        this.cutMillis=cutMillis;
        this.fullMillis=fullMillis;
        init();

    }

    public LoggerListener(LoggerListenerPreferences prefs) {
        super(prefs.getName());
        upload=prefs.getUpload();
        boardLogPath=prefs.getBoardLogPath();
        cutMillis=prefs.getCutMillis();
        fullMillis=prefs.getFullMillis();
        init();

    }

    private void init() {
        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        Context context=FairWindApplication.getInstance().getApplicationContext();
        SecureFilePacker secureFilePacker=fairWindModel.getLicenseManager();
        //String apiUrl=fairWindModel.getApiUrl();
        String apiUrl="http://api.fairwind.cloud:5050";

        String userId=fairWindModel.getLicenseManager().getUserId();
        String deviceId=fairWindModel.getLicenseManager().getDeviceId();

        Log.d(LOG_TAG,"apiUrl:"+apiUrl);
        Log.d(LOG_TAG,"userId:"+userId);
        Log.d(LOG_TAG,"deviceId:"+deviceId);
        Log.d(LOG_TAG,"upload:"+upload);

        // Check if userId and deviceId are consistent
        if (upload==true && userId!=null && userId.isEmpty()==false && deviceId!=null && deviceId.isEmpty()==false) {
            DBHelper dbHelper=new SQLiteDBHelper(context);
            String uploadPath=boardLogPath+File.separator+"uploads";
            int maxClients=Math.round(AsyncHttpClient.getNumberOfCores()*1.6f);
            uploader = new Uploader("it.uniparthenope.fairwind.services.logger.AndroidAsyncHttpClient", dbHelper, secureFilePacker, apiUrl, userId, deviceId,uploadPath,maxClients);
            uploader.start();
        }
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void onStart() throws DataListenerException {
        mExecutor= Executors.newSingleThreadExecutor();
        mExecutor.submit(this);
    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }

    @Override
    public boolean mayIUpdate() {
        return true;
    }

    @Override
    public  boolean isOutput() {
        return true;
    }

    @Override
    public  boolean isInput() {
        return false;
    }


    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {
        Log.d(LOG_TAG,"onUpdate:"+pathEvent.getPath());
        long millis=System.currentTimeMillis();

        if ((millis-lastCutMillis>cutMillis) || currentFilename==null || currentFilename.isEmpty()) {

            if (uploader != null && currentFilename != null && currentFilename.isEmpty() == false) {
                uploader.moveToUpload();
            }
            lastCutMillis = millis;

            boolean added = false;
            while (added==false) {
                Calendar c = Calendar.getInstance();

                SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
                dfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
                String formattedDate = dfDate.format(c.getTime());

                SimpleDateFormat dfTime = new SimpleDateFormat("hhmmss");
                dfTime.setTimeZone(TimeZone.getTimeZone("GMT"));
                String formattedTime = dfTime.format(c.getTime());
                currentFilename = boardLogPath + File.separator + formattedDate + "Z" + formattedTime + ".signalk.json";

                Log.d(LOG_TAG, "New current file:" + currentFilename);
                if (uploader != null) {
                    added=uploader.add(currentFilename);
                }
            }
        }

        SignalKModel temp=null;

        if ( (millis-lastFullMillis>fullMillis) || lastCutMillis==millis ) {
            temp= SignalKModelFactory.getInstance();
            if (temp!=null) {
                lastFullMillis = millis;
                pathEvents.clear();
            }

        } else  {
            pathEvents.add(pathEvent.getPath().substring(0,pathEvent.getPath().lastIndexOf(".")));
            if (millis-lastUpdateMillis>updateMillis) {
                temp = Utils.getSubTreeByKeys(pathEvents);
                if (temp!=null) {
                    lastUpdateMillis = millis;
                    pathEvents.clear();
                }
            }
        }

        if (temp!=null) {
            try {
                Log.d(LOG_TAG,"Writing: "+currentFilename);
                File file = new File(currentFilename);
                FileWriter fileWriter = new FileWriter(file, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                write(printWriter,temp);
                printWriter.flush();
                printWriter.close();
                //fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
                //Log.e(LOG_TAG, ex.getMessage());
            }
        }
    }

    private void write(PrintWriter printWriter, SignalKModel temp) throws IOException {
        Log.d(LOG_TAG,"Write");
        JsonSerializer jsonSerializer=new JsonSerializer();
        FullToDeltaConverter fullToDeltaConverter=new FullToDeltaConverter();
        String jsonString;


        Json jsonFull = jsonSerializer.writeJson(temp);
        List<Json> jsonDeltas = fullToDeltaConverter.handle(jsonFull);
        if (jsonDeltas != null && !jsonDeltas.isEmpty()) {
            for (Json jsonDelta : jsonDeltas) {
                if (jsonDelta.at("updates").asJsonList().size()>0) {
                    try {
                        Utils.fixSource(jsonDelta);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    jsonString = jsonDelta.toString();
                    Log.d(LOG_TAG, "L:delta " + jsonString);
                    printWriter.println(jsonString);
                }
            }
        }
    }


    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new LoggerListener((LoggerListenerPreferences)prefs);
    }

    @Override
    public void run() {
        while (!isDone()) {
            Log.d(LOG_TAG,"Run");
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG,e.getMessage());
            }
        }
    }


}
