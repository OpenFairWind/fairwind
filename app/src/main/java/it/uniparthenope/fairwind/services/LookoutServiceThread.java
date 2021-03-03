package it.uniparthenope.fairwind.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;

import it.uniparthenope.fairwind.FairWindApplication;
//import it.uniparthenope.fairwind.model.DataListenerInfos;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
//import it.uniparthenope.fairwind.model.DataListenerInfo;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 18/09/15.
 */
class LookoutServiceThread implements Runnable,SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "LOOKOUT_THREAD";

    private Intent intent;
    private SharedPreferences preferences;
    private long checkMillis=5000;

    private DataListeners dataListeners;

    public LookoutServiceThread(Intent intent) {
        this.intent=intent;

        // Get the preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(FairWindApplication.getInstance());
        preferences.registerOnSharedPreferenceChangeListener(this);

        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        // Get the lookout service
        LookoutService lookoutService=fairWindModel.getLookoutService();
        if (lookoutService!=null) {
            dataListeners = lookoutService.getDataListeners();
        } else {
            throw new RuntimeException("Lookout Service is null");
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Get the reference to the LookoutService
        LookoutService lookoutService=FairWindApplication.getFairWindModel().getLookoutService();

        Json jsonDataListenerPreferences=FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyJson(Constants.PREF_KEY_SERVICES_CONFIG_DATALISTENERS,null);
        if (jsonDataListenerPreferences!=null) {
            Map<String, Json> jsonDataListenerPreferencesMap = jsonDataListenerPreferences.asJsonMap();
            Collection<Json> jsonDataListenerPreferencesList = jsonDataListenerPreferencesMap.values();
            for (Json jsonDataListenerPreferencesItem : jsonDataListenerPreferencesList) {

                try {
                    String className = jsonDataListenerPreferencesItem.at("className", null).asString().replace("\"", "");
                    if (className != null) {
                        Class dataListenerPreferencesClass = Class.forName(className + "Preferences");
                        DataListenerPreferences dataListenerPreferences = (DataListenerPreferences) dataListenerPreferencesClass.newInstance();
                        dataListenerPreferences.byJson(jsonDataListenerPreferencesItem, lookoutService.getResources());
                        managePreferenceChanged(dataListenerPreferences, key);
                        Log.d(LOG_TAG, "managePreferenceChanged:" + dataListenerPreferences.getName() + ":" + dataListenerPreferences.getDesc());
                    }
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
    }



    private void managePreferenceChanged(DataListenerPreferences dataListenerPreferences, String key) {
        if (key.contains(dataListenerPreferences.getName()) && !key.contains("active")) {
            // Get the UsbSerialClient activation
            boolean active=preferences.getBoolean(dataListenerPreferences.getName()+"_active",true);

            if (active == true ) {
                // Get a valid reference
                DataListener dataListener = dataListeners.find(dataListenerPreferences.getImplClass());
                if (dataListener !=null) {
                    dataListeners.remove(dataListener);
                }
            }
        }
    }



    private DataListener manage(DataListenerPreferences dataListenerPreferences) {
        Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"...");
        //String name= dataListenerPreferences.getName();

        // Get the activation
        boolean enabled=dataListenerPreferences.isEnabled();


        // Get a valid reference
        DataListener dataListener = dataListeners.find(dataListenerPreferences.getName());


        if (enabled == true) {
            Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"-> have to be active");
            if ( dataListener != null) {
                if (dataListener.isAlive() == false) {
                    dataListeners.remove(dataListener);
                    dataListener = null;
                    Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"-> have to be active, but it is down - REMOVED");
                } else {
                    Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"-> have to be active and is ACTIVE");
                }
            }


            if (dataListener ==null) {
                Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"-> have to be active. Ready to launch!");




                try {
                    Object obj = dataListenerPreferences.getImplClass().newInstance();


                    Method method = null;
                    try {
                        method = dataListenerPreferences.getImplClass().getDeclaredMethod("newFromDataListenerPreferences", DataListenerPreferences.class);
                        if (method!=null) {
                            try {
                                dataListener = (DataListener) method.invoke(obj,dataListenerPreferences);
                                if (dataListener != null) {
                                    try {
                                        Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"-> Invoked newFromPreferences");
                                        if (dataListeners.add(dataListener)==true)
                                        {
                                            Log.d(LOG_TAG, "Manage:" + dataListenerPreferences.getName() + "-> Added");
                                        } else {
                                            Log.d(LOG_TAG, "Manage:" + dataListenerPreferences.getName() + "-> NOT Added :-/");
                                            dataListener = null;
                                        }
                                    } catch (DataListenerException dataListenerException) {
                                        //dataListenerException.printStackTrace();
                                        Log.e(LOG_TAG, dataListenerException.getMessage());
                                        dataListener = null;
                                    }
                                }
                            } catch (IllegalAccessException e) {
                                Log.e(LOG_TAG, e.getMessage());
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                Log.e(LOG_TAG, e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        Log.e(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                } catch (InstantiationException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            /*
            if (dataListener !=null) {
                Log.i(LOG_TAG,"Updating "+dataListener.getName()+"...");
                try {
                    if (dataListener.isOutput() && dataListener.mayIUpdate()) {
                        dataListener.update();
                    }
                } catch (DataListenerException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            */

        } else {
            if (enabled == false && dataListener != null && dataListener.isAlive()==true) {
                dataListeners.remove(dataListener);
                dataListener =null;
                Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"-> Removed!");
            }
        }
        Log.d(LOG_TAG,"Manage:"+ dataListenerPreferences.getName()+"...done!");
        return dataListener;
    }




    public void run() {

        boolean done=false;
        while(!done) {
            Log.i(LOG_TAG, "Alive...");



            // Get the reference to the LookoutService
            LookoutService lookoutService=FairWindApplication.getFairWindModel().getLookoutService();
            //lookoutService.updateNotification(null,null,it.uniparthenope.fairwind.sdk.R.drawable.boat_red_32x32,0);
            Json jsonDataListenerPreferences=FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyJson(Constants.PREF_KEY_SERVICES_CONFIG_DATALISTENERS,null);
            if (jsonDataListenerPreferences!=null) {
                Map<String, Json> jsonDataListenerPreferencesMap = jsonDataListenerPreferences.asJsonMap();
                Collection<Json> jsonDataListenerPreferencesList = jsonDataListenerPreferencesMap.values();
                for (Json jsonDataListenerPreferencesItem : jsonDataListenerPreferencesList) {

                    try {
                        String className = jsonDataListenerPreferencesItem.at("className", null).asString().replace("\"", "");
                        if (className != null) {
                            Class dataListenerPreferencesClass = Class.forName(className + "Preferences");
                            DataListenerPreferences dataListenerPreferences = (DataListenerPreferences) dataListenerPreferencesClass.newInstance();
                            dataListenerPreferences.byJson(jsonDataListenerPreferencesItem, lookoutService.getResources());

                            manage(dataListenerPreferences);
                            Log.d(LOG_TAG, dataListenerPreferences.getName() + ":" + dataListenerPreferences.getDesc());
                        }
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    } catch (InstantiationException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }

            // Watch the Watchdog...
            Log.d(LOG_TAG,"Checking WatchdogService");
            if (Utils.isServiceRunning(lookoutService,"it.uniparthenope.fairwind.services.WatchDogService")==false) {
                Log.d(LOG_TAG,"Restarting WatchDogService");
                // Start the mock location service
                final Context context=lookoutService.getApplicationContext();
                new Handler() {

                }.post(new Runnable() {
                    @Override
                    public void run() {
                        WatchDogService.startActionStartForeground(context);
                    }
                });
            } else {
                Log.d(LOG_TAG,"WatchDogService is Running");
            }
            //Position position=FairWindApplication.getFairWindModel().getPosition();
            //String ticker= Formatter.formatLongitude(Formatter.COORDS_STYLE_DDMM,position.getLongitude(),"n/a")+" "+Formatter.formatLatitude(Formatter.COORDS_STYLE_DDMM,position.getLatitude(),"n/a");
            //lookoutService.updateNotification(ticker,null,it.uniparthenope.fairwind.sdk.R.drawable.boat_blue_32x32,0);




            Log.i(LOG_TAG, "Sleeping...");
            try {
                Thread.sleep(checkMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(LOG_TAG, "...awake!");
        }

        dataListeners.removeAll();
    }
}
