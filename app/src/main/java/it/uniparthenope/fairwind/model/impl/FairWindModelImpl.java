package it.uniparthenope.fairwind.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import java.io.IOException;
import java.util.Vector;

import it.uniparthenope.fairwind.FairWindApplication;
//import it.uniparthenope.fairwind.model.DataListenerInfos;
import it.uniparthenope.fairwind.model.FirstRun;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.model.Preferences;
import it.uniparthenope.fairwind.services.LookoutService;
import nz.co.fortytwo.signalk.handler.NMEA0183Producer;

import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 23/09/15.
 */
public class FairWindModelImpl extends FairWindModelBase implements FairWindModel {
    private static final String LOG_TAG = "FAIRWIND_MODEL";


    public boolean isTrueWindComputed() {
        return false;
    }



    private LookoutService lookoutService=null;
    public void setLookoutService(LookoutService lookoutService) {
       this.lookoutService = lookoutService;
    }
    public LookoutService getLookoutService() { return lookoutService; }



    private LicenseManager licenseManager;
    public  LicenseManager getLicenseManager() { return licenseManager; }
    private PreferencesImpl preferencesImpl;

    @Override
    public Preferences getPreferences() { return preferencesImpl; }

    public void sharedPreferences2Model() throws IOException {
        if (preferencesImpl!=null) {
            preferencesImpl.sharedPreferences2Model();
        }
    }

    public FairWindModelImpl()  {
        super();
        SignalKModelFactory.getInstance();
        Log.d(LOG_TAG,"boat UUID -> "+SignalKConstants.self);

        preferencesImpl =new PreferencesImpl(this);
        licenseManager=new LicenseManager(this);
        try {
            licenseManager.check();
        } catch (ValidationException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
        try {
            preferencesImpl.sharedPreferences2Model();
            Util.getConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.d(LOG_TAG,"boat UUID -> "+SignalKConstants.self);

        boolean firstrun = getPreferences().getConfigPropertyBoolean("firstrun",true);
        //firstrun=true;
        if (firstrun) { // Checks to see if we've ran the application b4
            FirstRun firstRun=new FirstRun(this);
            try {
                firstRun.execute("");

            } catch (Exception ex) {
                Log.d(LOG_TAG,ex.getMessage());
            }
        }
    }

    public Cursor querySignalK(String itemId) {
        Log.d(LOG_TAG,"querySignalK");
        String[] columns = new String[] { SignalK._ID, SignalK.ITEM};
        String value;


        MatrixCursor matrixCursor= new MatrixCursor(columns);
        if (getData()!=null) {
            if (itemId == null) {
                for (String key : getKeys()) {
                    Log.d(LOG_TAG,"querySignalK: "+key+":"+getData().get(key));
                    if (getData().get(key)!= null) {
                        value = getData().get(key).toString();
                        if (!value.isEmpty()) {
                            matrixCursor.addRow(new Object[]{key, value});
                        }
                    }
                }
            } else {
                if (getData().get(itemId) != null) {
                    value = getData().get(itemId).toString();
                    if (!value.isEmpty()) {
                        matrixCursor.addRow(new Object[]{itemId, value});
                    }
                }
            }
        }
        return matrixCursor;
    }

    /////////////////////////

    @Override
    public boolean isKeepScreenOn() {
        return true;
    }





    public AppDetails getAppDetails(Context context, int type){

        AppDetails appDetails = new AppDetails(context);
        return appDetails.selectApp(type);
    }
}
