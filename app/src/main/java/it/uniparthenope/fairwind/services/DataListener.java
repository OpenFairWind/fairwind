package it.uniparthenope.fairwind.services;

import android.util.Log;

import com.google.common.eventbus.Subscribe;

import java.util.NavigableMap;
import java.util.UUID;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.handler.CourseHandler;
import it.uniparthenope.fairwind.handler.SaveHandler;
import it.uniparthenope.fairwind.handler.TrackHandler;
import it.uniparthenope.fairwind.model.UpdateListener;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.AlarmHandler;
import nz.co.fortytwo.signalk.handler.DeclinationHandler;
import nz.co.fortytwo.signalk.handler.DeltaToMapConverter;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.handler.TrueWindHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.model.impl.SignalKModelImpl;
import nz.co.fortytwo.signalk.util.AlarmManager;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import static nz.co.fortytwo.signalk.util.SignalKConstants.alarm;
import static nz.co.fortytwo.signalk.util.SignalKConstants.normal;
import static nz.co.fortytwo.signalk.util.SignalKConstants.warn;

/**
 * Created by raffaelemontella on 21/08/15.
 */
public abstract class DataListener implements UpdateListener {
    private static final String LOG_TAG = "DATA_LISTENER";



    //protected static final FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();//(FairWindModelImpl)FairWindModelImpl.getInstance();

    private String name;
    public String getName() {
        return name;
    }

    protected String type="Base Data Listener";
    public String getType() {return type;}



    public abstract long getTimeout();

    public boolean isInput() {
        return FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyBoolean(name+"_isinput",true);
    }

    public boolean isOutput() {
        return FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyBoolean(name+"_isoutput",true);
    }

    public boolean isLicensed() {
        return FairWindApplication.getFairWindModel().getLicenseManager().hasBeenChecked();
    }


    public abstract void onStart() throws DataListenerException;
    public abstract void onStop();
    public abstract void onUpdate(PathEvent pathEvent) throws UpdateException;
    public abstract boolean onIsAlive();
    public abstract boolean mayIUpdate();


    public void start() throws DataListenerException {

        if (!isAlive()) {
            Log.d(LOG_TAG, "start");
            done=false;
            onStart();
            // Register to all events
            FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
            fairWindModel.getEventBus().register(this);
        }



    }

    private boolean done=false;
    protected void setDone() {
        done=true;
    }
    protected boolean isDone() { return done; }

    public void stop() {
        if (isAlive()) {
            Log.d(LOG_TAG,"stop");
            onStop();
            done=true;
            // Unregister to all events
            FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
            fairWindModel.getEventBus().unregister(this);
        }
    }

    public boolean isAlive() {
        boolean result=false;
        result=onIsAlive();
        return result;
    }
    /*
    The constructor
     */


    public DataListener() {
    }

    public DataListener(DataListenerPreferences prefs) {
        this.name=prefs.getName();
        init();
    }

    public DataListener(String name) {
        this.name=name;
        init();
    }

    private void init() {



        // Create a sentence factory

    }

    //private SignalKModel signalKModelPre=SignalKModelFactory.getCleanInstance();

    public void process(SignalKModel signalKObject) {

        Log.d(LOG_TAG, "process");




        FairWindApplication.getFairWindModel().getLookoutService().getDataListeners().process(this,signalKObject);


    }



    @Subscribe
    public void onEvent(PathEvent pathEvent) {
        Log.d(LOG_TAG,"onEvent:"+pathEvent.getPath());
        Log.d(LOG_TAG, "Is to update " + getName() + "? "+isOutput()+" "+mayIUpdate()+" "+isLicensed());
        if (isOutput()  && mayIUpdate()  && isLicensed()) {
            try {
                Log.d(LOG_TAG, "updating -> " + getName());
                onUpdate(pathEvent);
            } catch (UpdateException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

}
