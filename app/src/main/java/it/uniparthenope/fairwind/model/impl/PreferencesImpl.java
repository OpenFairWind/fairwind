package it.uniparthenope.fairwind.model.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.preferences.meta.MetaPreferencesContent;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.MetaPreferences;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Zone;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.Preferences;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 23/04/16.
 */
public class PreferencesImpl implements Preferences{
    private static final String LOG_TAG = "PREFERENCES_IMPL";

    public static final String KEY="C3BOH725R2D2BB8";

    private FairWindModelImpl fairWindModel;

    public PreferencesImpl(FairWindModelImpl fairWindModel) {
        this.fairWindModel=fairWindModel;
    }

    public void sharedPreferences2Model() throws IOException {

        String boatUuid=getConfigProperty(Constants.PREF_KEY_GENERAL_BOAT_UUID);
        String boatName=getConfigProperty(Constants.PREF_KEY_GENERAL_BOAT_NAME);
        String boatFlag=getConfigProperty(Constants.PREF_KEY_GENERAL_BOAT_FLAG);
        String boatPort=getConfigProperty(Constants.PREF_KEY_GENERAL_BOAT_PORT);
        String boatMmsi=getConfigProperty(Constants.PREF_KEY_GENERAL_BOAT_MMSI);
        String boatUrl=getConfigProperty(Constants.PREF_KEY_GENERAL_BOAT_URL);

        Log.d(LOG_TAG,"boat UUID -> "+boatUuid);

        Util.setSelf(boatUuid);

        fairWindModel.setBoatUuid(boatUuid);
        fairWindModel.setBoatName(boatName);
        fairWindModel.setBoatFlag(boatFlag);
        fairWindModel.setBoatPort(boatPort);
        fairWindModel.setBoatMmsi(boatMmsi);
        fairWindModel.setBoatUrl(boatUrl);

        put(ConfigConstants.VERSION, "0.1");
        put(ConfigConstants.UUID,boatUuid);
        put(ConfigConstants.MMSI,boatMmsi);
        put(ConfigConstants.NAME,boatName);
        put(ConfigConstants.PORT,boatPort);
        put(ConfigConstants.FLAG,boatFlag);
        put(ConfigConstants.STREAM_URL,boatUrl);

        String filesDir= FairWindApplication.getInstance().getFilesDir().getAbsolutePath();

        String sdcardDir= Environment.getExternalStorageDirectory().getAbsolutePath();
        put(ConfigConstants.USBDRIVE, sdcardDir);

        String mapsPath=getConfigProperty(Constants.PREF_KEY_MAPS_PATH);
        if (mapsPath!=null && mapsPath.isEmpty()) {
            mapsPath=sdcardDir+ File.separator+"TiledLayerDialog"+File.separator;
            if (!(new File(mapsPath)).exists()) { (new File(mapsPath)).mkdirs(); }
            setConfigProperty(Constants.PREF_KEY_MAPS_PATH,mapsPath);
        }

        String cfgDir=filesDir+ File.separator+"conf"+File.separator;
        if (!(new File(cfgDir)).exists()) { (new File(cfgDir)).mkdirs(); }
        put(ConfigConstants.CFG_DIR, cfgDir);

        put(ConfigConstants.CFG_FILE, "signalk.cfg");

        String storageRoot=filesDir+ File.separator+"storage"+File.separator;
        if (!(new File(storageRoot)).exists()) { (new File(storageRoot)).mkdirs(); }
        put(ConfigConstants.STORAGE_ROOT, storageRoot);

        String staticDir=filesDir+ File.separator+"signalk-static";
        if (!(new File(staticDir)).exists()) { (new File(staticDir)).mkdirs(); }
        put(ConfigConstants.STATIC_DIR, staticDir);

        String mapDir=filesDir+ File.separator+"mapcache";
        if (!(new File(mapDir)).exists()) { (new File(mapDir)).mkdirs(); }
        put(ConfigConstants.MAP_DIR, mapDir);



/*
        put(ConfigConstants.DEMO, false);

        put(ConfigConstants.WEBSOCKET_PORT, getConfigPropertyInt(Constants.PREF_KEY_WEBSERVER_PORTWS));
        put(ConfigConstants.REST_PORT, getConfigPropertyInt(Constants.PREF_KEY_WEBSERVER_PORTREST));

        put(
                ConfigConstants.SERIAL_PORTS,
                "[\"/dev/ttyUSB0\",\"/dev/ttyUSB1\",\"/dev/ttyUSB2\",\"/dev/ttyACM0\",\"/dev/ttyACM1\",\"/dev/ttyACM2\"]");

        put(ConfigConstants.SERIAL_PORT_BAUD, 38400);

        put(ConfigConstants.TCP_PORT, null);
        put(ConfigConstants.UDP_PORT, null);
        put(ConfigConstants.TCP_NMEA_PORT, getConfigPropertyInt(Constants.PREF_KEY_NMEA0183TCPIPSERVER_PORT));
        put(ConfigConstants.UDP_NMEA_PORT, null);
        put(ConfigConstants.STOMP_PORT, null);
        put(ConfigConstants.MQTT_PORT, null);
        put(ConfigConstants.CLOCK_source, "system");
        put(ConfigConstants.HAWTIO_PORT, null);
        put(ConfigConstants.HAWTIO_AUTHENTICATE, false);
        put(ConfigConstants.HAWTIO_CONTEXT, null);
        put(ConfigConstants.HAWTIO_WAR,null);
        put(ConfigConstants.HAWTIO_START, false);
        */

        /*
        put(ConfigConstants.ALLOW_INSTALL, false);
        put(ConfigConstants.ALLOW_UPGRADE, false);
        put(ConfigConstants.GENERATE_NMEA0183, true);
        put(ConfigConstants.START_MQTT, false);
        put(ConfigConstants.START_STOMP, false);
        String tcpipClientDataSourceUri=getConfigProperty(Constants.PREF_KEY_NMEA0183TCPIPCLIENTDATASOURCE_HOST)+":"+
                getConfigPropertyInt(Constants.PREF_KEY_NMEA0183TCPIPCLIENTDATASOURCE_PORT);
        put(ConfigConstants.CLIENT_TCP,  tcpipClientDataSourceUri);
        put(ConfigConstants.CLIENT_MQTT, null);
        put(ConfigConstants.CLIENT_STOMP, null);
        */
        /***/
        // Add metadata
        setMeta();

        SignalKModelFactory.saveConfig(fairWindModel);
    }

    private void put(String key, int val) {
        fairWindModel.getFullData().put(key,val);
    }

    private void put(String key, String val) {
        fairWindModel.getFullData().put(key,val);
    }

    private void put(String key, boolean val) {
        fairWindModel.getFullData().put(key,val);
    }

    private void put(String key, double val) {
        fairWindModel.getFullData().put(key,val);
    }

    public void setMeta() {
        try {
            String defaultString= Utils.readTextFromResource(FairWindApplication.getInstance().getResources(), R.raw.default_metapreferences);
            Json defaultJson=Json.read(defaultString);
            Json jsonAlarmPreferences= getConfigPropertyJson(Constants.PREF_KEY_META_CONFIG_META,defaultJson);
            //Json json=defaultJson;
            Map<String,Json> jsonAlarmPreferencesMap=jsonAlarmPreferences.asJsonMap();
            Collection<Json> jsonAlarmPreferencesList= jsonAlarmPreferencesMap.values();
            for(Json jsonAlarmPreferencesItem:jsonAlarmPreferencesList) {
                MetaPreferences metaPreferences=new MetaPreferences(jsonAlarmPreferencesItem);
                if (metaPreferences.getPath().isEmpty()==false) {
                    String path = Util.fixSelfKey(metaPreferences.getPath().replace("/", ".").substring(1));
                    // Remove any .meta ending
                    if (path.endsWith(".meta")) {
                        path=path.substring(0,path.lastIndexOf("."));
                    }

                    // Work with path
                    //...

                    // Add .meta to path
                    path+=".meta";

                    // clear(path)
                    Log.d(LOG_TAG, metaPreferences.getPath() + ":" + metaPreferences.getDisplayName());
                    fairWindModel.getData().put(path + ".displayName", metaPreferences.getDisplayName());
                    fairWindModel.getData().put(path + ".shortName", metaPreferences.getShortName());
                    fairWindModel.getData().put(path + ".warnMethod", metaPreferences.getWarnMethod().toString().toLowerCase());
                    fairWindModel.getData().put(path + ".warnMessage", metaPreferences.getWarnMessage());
                    fairWindModel.getData().put(path + ".alarmMethod", metaPreferences.getAlarmMethod().toString().toLowerCase());
                    fairWindModel.getData().put(path + ".alarmMessage", metaPreferences.getAlarmMessage());
                    Json jsonZones=Json.array();
                    for (Zone zone : metaPreferences.getZones()) {
                        jsonZones.add(zone.asJson());
                    }

                    fairWindModel.getData().put(path + ".zones",jsonZones);

                    String[] parts=path.split("[.]");
                    String notificationPath=parts[0]+"."+parts[1]+"."+SignalKConstants.notifications;
                    for(int i=2;i<parts.length-1;i++) {
                        notificationPath+="."+parts[i];
                    }
                    FairWindEvent event=new FairWindEvent(notificationPath,notificationPath,0, PathEvent.EventType.ADD,FairWindApplication.getInstance());
                    fairWindModel.register(event);
                }

            }
        } catch (IOException e) {
            Log.d(LOG_TAG,e.getMessage());
        }
    }

    public void model2SharedPreferences() {
        setConfigProperty("boat_name",fairWindModel.getBoatName());
        setConfigProperty("boat_flag",fairWindModel.getBoatFlag());
        setConfigProperty("boat_port",fairWindModel.getBoatPort());
        setConfigProperty("boat_uuid",fairWindModel.getBoatUuid());
        setConfigProperty("boat_mmsi",fairWindModel.getBoatMmsi());
        setConfigProperty("boat_url",fairWindModel.getBoatUrl());
    }

    @Override
    public void setConfigProperty(int keyStringId, String value) {
        setConfigProperty( FairWindApplication.getInstance().getResources().getString(keyStringId),value);
    }

    @Override
    public void setConfigProperty(String key, String value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( FairWindApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    @Override
    public void setConfigPropertyInt(String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( FairWindApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    @Override
    public void setConfigPropertyBoolean(String key, boolean value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( FairWindApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    @Override
    public void setConfigPropertyJson(String key, Json json) {
        setConfigProperty(key,json.toString());
    }

    @Override
    public String getConfigProperty(String keyString) {
        Context context=FairWindApplication.getInstance();
        String defString=keyString.replace("pref_key_","pref_default_");
        String defValue=null;
        int defId= context.getResources().getIdentifier(defString,"string", context.getPackageName());
        if (defId!=0) {
            defValue = FairWindApplication.getInstance().getResources().getString(defId);
        }
        return getConfigProperty(keyString,defValue);
    }

    @Override
    public String getConfigProperty(String keyString, String defValue) {
        Context context=FairWindApplication.getInstance();
        String result=null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context);
        if (sharedPreferences!=null) {
            result=sharedPreferences.getString(keyString,defValue);
        }
        return result;
    }

    @Override
    public Integer getConfigPropertyInt(String keyString) {
        Context context=FairWindApplication.getInstance();
        String defString=keyString.replace("pref_key_","pref_default_");
        int defId= context.getResources().getIdentifier(defString,"integer", context.getPackageName());
        Integer defValue=null;
        if (defId!=0) {
            defValue = context.getResources().getInteger(defId);
        }
        return getConfigPropertyInt(keyString,defValue);
    }


    @Override
    public Integer getConfigPropertyInt(String keyString, Integer defValue) {
        Integer result=null;
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences( FairWindApplication.getInstance());
        if (sharedPreferences != null) {
            try {
                String valueString=sharedPreferences.getString(keyString, defValue.toString());
                if (valueString!=null) {
                    try {
                        result = Integer.parseInt(sharedPreferences.getString(keyString, defValue.toString()));
                    } catch (ClassCastException e) {
                        Log.e(LOG_TAG, keyString + ":" + valueString + " -> " + e.getMessage());
                    }
                }
            } catch (ClassCastException e) {
                result = sharedPreferences.getInt(keyString,defValue);
            }

        }
        return result;
    }


    @Override
    public Boolean getConfigPropertyBoolean(String keyString) {
        Boolean result=null;
        Context context=FairWindApplication.getInstance();
        String defString=keyString.replace("pref_key_","pref_default_");
        Boolean defValue=null;
        int defId= context.getResources().getIdentifier(defString,"bool",context.getPackageName());
        if (defId!=0) {
            defValue = FairWindApplication.getInstance().getResources().getBoolean(defId);
        }
        result=getConfigPropertyBoolean(keyString,defValue);

        return result;
    }

    @Override
    public Boolean getConfigPropertyBoolean(String keyString, Boolean defValue) {
        Boolean result=null;
        Context context=FairWindApplication.getInstance();
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences( context);
        if (sharedPreferences != null) {
            try {
                String valueString = sharedPreferences.getString(keyString, defValue.toString());
                if (valueString != null) {
                    result = Boolean.parseBoolean(valueString);
                }
            } catch (ClassCastException e) {
                result = sharedPreferences.getBoolean(keyString,defValue);
            }
        }
        return result;
    }

    @Override
    public Json getConfigPropertyJson(String keyString, Json defValue) {
        Json json=defValue;
        String jsonString=getConfigProperty(keyString,null);
        if (jsonString!=null && !jsonString.isEmpty()) {
            json=Json.read(jsonString);
            if (json==null) {
                json=defValue;
            }
        }
        return json;
    }

    @Override
    public Json getConfigPropertyJson(String keyString) {
        return getConfigPropertyJson(keyString,null);
    }

    public int getUnit(int code) {
        int result=0;
        if (code== FairWindModel.UNIT_BOAT_SPEED) {
            result= Formatter.UNIT_SPEED_KNT;
        } else
        if (code==FairWindModel.UNIT_DEPTH) {
            result= Formatter.UNIT_DEPTH_M;
        } else
        if (code==FairWindModel.UNIT_WATERTEMP) {
            result= Formatter.UNIT_TEMP_C;
        } else
        if (code==FairWindModel.UNIT_AIRTEMP) {
            result= Formatter.UNIT_TEMP_C;
        } else
        if (code==FairWindModel.UNIT_AIRPRESSURE) {
            result= Formatter.UNIT_PRESSURE_MB;
        } else
        if (code==FairWindModel.UNIT_WINDCHILL) {
            result= Formatter.UNIT_TEMP_C;
        } else
        if (code==FairWindModel.UNIT_WIND_SPEED) {
            result= Formatter.UNIT_SPEED_KNT;
        }
        else
        if (code==FairWindModel.COORDS_STYLE) {
            result= Formatter.COORDS_STYLE_DDMMSS;
        }
        return result;
    }
}
