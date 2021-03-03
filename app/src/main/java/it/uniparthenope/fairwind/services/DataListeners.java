package it.uniparthenope.fairwind.services;

//import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.captain.AlarmDialog;
import it.uniparthenope.fairwind.handler.AlarmsHandler;
import it.uniparthenope.fairwind.handler.AnchorHandler;
import it.uniparthenope.fairwind.handler.CourseHandler;
import it.uniparthenope.fairwind.handler.SaveHandler;
import it.uniparthenope.fairwind.handler.TrackHandler;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.AlarmHandler;
import nz.co.fortytwo.signalk.handler.DeclinationHandler;
import nz.co.fortytwo.signalk.handler.TrueWindHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 22/08/15.
 */
public class DataListeners {
    private static final String LOG_TAG = "DATALISTENERS";

    public static final UUID licenseAlertUuid=UUID.fromString("6b42d618-e270-11e6-bf01-fe55135034f3");

    private TrueWindHandler trueWindHandler;
    private AlarmsHandler alarmsHandler;
    private DeclinationHandler declinationHandler;
    private SaveHandler saveHandler;
    private TrackHandler trackHandler;
    private CourseHandler courseHandler;
    private AnchorHandler anchorHandler;

    private  Vector<DataListener> dataListeners;
    public Vector<DataListener> get() { return dataListeners; }

    public DataListeners() {
        trueWindHandler=new TrueWindHandler();
        alarmsHandler=new AlarmsHandler();
        declinationHandler=new DeclinationHandler();

        courseHandler=new CourseHandler();

        saveHandler=new SaveHandler();
        trackHandler=new TrackHandler();

        anchorHandler=new AnchorHandler();

        dataListeners =new Vector<DataListener>();
    }

    public boolean remove(DataListener dataListener) {
        if (dataListener ==null) return false;
        dataListener.stop();
        return dataListeners.remove(dataListener);
    }

    public void removeAll() {
        for(DataListener dataListener : dataListeners) {
            dataListeners.remove(dataListener);
        }
    }

    public boolean add(DataListener dataListener) throws DataListenerException {

        if (dataListener != null) {
            Log.d(LOG_TAG,"Adding -> "+dataListener.getName());
            StarterTask starterTask=new StarterTask(dataListener);
            if (starterTask.get()) {
                return dataListeners.add(dataListener);
            }

        }
        return false;
    }

    class StarterTask extends Thread {
        private boolean result=false;
        private boolean done=false;
        private DataListener dataListener;


        public StarterTask(DataListener dataListener) {
            this.dataListener=dataListener;

        }

        public boolean get() {
            Log.d(LOG_TAG,"get");
            long t0= SystemClock.currentThreadTimeMillis();
            this.start();
            while (!done && (SystemClock.currentThreadTimeMillis()-t0)<dataListener.getTimeout()) {
                try {
                    Thread.sleep(dataListener.getTimeout()/10);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG,e.getMessage());
                }
            }
            Log.d(LOG_TAG,"get ->" +result);
            return result;
        }


        public void run() {
            try {
                try {
                    dataListener.start();
                    result = true;
                } catch (SecurityException se) {
                    Log.e(LOG_TAG,se.getMessage());
                }
            } catch (DataListenerException dle) {
                Log.e(LOG_TAG,dle.getMessage());
            }
            done=true;
        }
    }

    public DataListener find(Class<?> cls) {
        // Get a valid reference
        DataListener result = null;
        for (DataListener dataListener : dataListeners) {
            if (cls.isInstance(dataListener)) {
                result = dataListener;
                break;
            }
        }
        return result;
    }

    public DataListener find(String name) {
        // Get a valid reference
        DataListener result = null;
        for (DataListener dataListener : dataListeners) {
            if (dataListener.getName().equals(name)) {
                result = dataListener;
                break;
            }
        }
        return result;
    }

    synchronized public void process(DataListener dataListener,SignalKModel signalKObject) {
        if (dataListener.isLicensed()==false) {

            AlarmDialog.showAlert(FairWindApplication.getInstance(),licenseAlertUuid,30000,7500,
                    "License Error",
                    "Please register your free license on http://fariwind.cloud in order to use "+
                            dataListener.getName() +" ("+dataListener.getType()+") data listener.",null,null);

        }
        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();

        if (signalKObject != null && !signalKObject.getData().isEmpty() && dataListener.isInput() && dataListener.isLicensed()) {



            Log.d(LOG_TAG,"Put All SignalK new data");
            fairWindModel.putAll(signalKObject.getData());

            if (fairWindModel.isTrueWindComputed()) {
                Log.d(LOG_TAG,"Handle TrueWind");
                trueWindHandler.handle(fairWindModel);
            }

            Log.d(LOG_TAG,"Handle declination");
            declinationHandler.handle(fairWindModel);

            Log.d(LOG_TAG,"Handler course");
            courseHandler.handle(fairWindModel);

            Log.d(LOG_TAG,"Handle track");
            trackHandler.handle(signalKObject);

            Log.d(LOG_TAG,"Handle anchor");
            anchorHandler.handle(fairWindModel);

            Log.d(LOG_TAG,"Handle alarm");
            alarmsHandler.handle(fairWindModel);

            Log.d(LOG_TAG,"Handle save");
            saveHandler.handle(fairWindModel);


        }




        Log.d(LOG_TAG, "process -> size: " + fairWindModel.getData().size());
    }


}
