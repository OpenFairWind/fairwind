package it.uniparthenope.fairwind.sdk.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;


/**
 * Created by raffaelemontella on 23/04/16.
 */
public class Utils {

    private static final String LOG_TAG = "UTILS";


    public static FairWindModel getFairWindModel() {
        FairWindModel fairWindModel=null;
        Class cls=null;
        try {
            cls = Class.forName("it.uniparthenope.fairwind.FairWindApplication");
            Log.d(LOG_TAG,"Is App");
        } catch (ClassNotFoundException exApp) {
            Log.d(LOG_TAG,exApp.getMessage());
            try {
                cls = Class.forName("it.uniparthenope.fairwind.sdk.FairWindBoatApplication");
                Log.d(LOG_TAG,"Is a BoatApp");
            } catch (ClassNotFoundException exSdk) {
                Log.d(LOG_TAG,exSdk.getMessage());
            }
        }
        if (cls!=null) {
            Object obj=null;
            Method method = null;
            try {
                method = cls.getDeclaredMethod("getFairWindModel");
                try {
                    Log.d(LOG_TAG, "pre method.invoke");
                    obj=method.invoke(cls);
                    if (obj!=null && obj instanceof FairWindModel) {
                        fairWindModel=(FairWindModel)obj;
                        Log.d(LOG_TAG,"Done!");
                    }
                    Log.d(LOG_TAG, "post method.invoke)");
                } catch (IllegalAccessException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    String msg = e.getMessage();
                    if (msg == null || msg.isEmpty()) {
                        msg = "InvocationTargetException";
                    }
                    Log.e(LOG_TAG, msg);
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                Log.d(LOG_TAG,e.getMessage());
            }
        }
        return fairWindModel;
    }



    public static String readTextFromResource(Resources resources, int resourceId) throws IOException {
        String result=null;
        InputStream inputStream = resources.openRawResource(resourceId);
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            if (inputStreamReader != null) {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line+"\n");
                }
                result=stringBuilder.toString();

            }
        }
        return result;
    }






    public static boolean isServiceRunning(Context context, String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i
                    .next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;
                break;
                //if(runningServiceInfo.foreground)
                //{
                //service run in foreground
                //}

            }
        }
        return serviceRunning;
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static SortedMap<String,Object> Json2SortedMap(String root, Json json) {
        SortedMap<String,Object> result=new TreeMap<String, Object>();

        _sortedMap(result,root,json);

        return result;
    }

    private static void _sortedMap(SortedMap<String,Object> map, String root, Json json) {
        if (json==null || json.isNull()) {
            return;
        }
        try {
            Map<String, Json> jsonMap = json.asJsonMap();
            for (String key : jsonMap.keySet()) {
                Json jsonItem=json.at(key);
                _sortedMap(map, root+"."+key, jsonItem);
            }
        } catch (UnsupportedOperationException ex1) {
            try {
                List<Json> jsonList = json.asJsonList();
                /*
                int idx=0;
                for(Json jsonItem:jsonList) {
                    _sortedMap(map, root+"."+idx, jsonItem);
                    idx++;
                }
                */
                //String s="["+jsonList.get(0).asString()+","+jsonList.get(1).asString()+"]";
                String s=json.toString();
                map.put(root, s);
            } catch (UnsupportedOperationException ex2) {
                String s = json.asString();
                Object object = null;
                try {
                    object = Double.parseDouble(s);
                } catch (NumberFormatException ex3) {
                    object = s;
                }
                if (object != null) {
                    map.put(root, object);
                }
            }
        }
    }

    public static void fixJsonDelta(Json jsonDelta) {
        List<Json> filteredUpdates=new ArrayList<Json>();
        List<Json> updates=jsonDelta.at("updates").asJsonList();
        for (Json update:updates) {
            List<Json> filteredValues=new ArrayList<Json>();
            List<Json> values=update.at("values").asJsonList();
            for (Json value:values) {
                Json aValue = value.at("value");
                if (aValue != null && aValue.isNull() == false) {
                    try {
                        Map<String, Json> map = aValue.asJsonMap();
                    } catch (UnsupportedOperationException ex) {
                        filteredValues.add(value);
                    }
                }

            }
            if (filteredValues.size()>0) {
                update.set("values", filteredValues);
                filteredUpdates.add(update);
            }
        }

        jsonDelta.set("updates", filteredUpdates);

    }

    public static SignalKModel getSubTreeByKeys(Collection<String> pathEvents) {
        SignalKModel signalKModel=(SignalKModel) Utils.getFairWindModel();
        SignalKModel temp = SignalKModelFactory.getCleanInstance();


        for (String path : pathEvents) {
            NavigableMap<String,Object> map=signalKModel.getSubMap(path);
            if (map != null) {
                boolean skipMeta=true;
                if (path.contains(SignalKConstants.dot+SignalKConstants.meta+SignalKConstants.dot)) {
                    skipMeta=false;
                }
                for(String key:map.keySet()) {
                    if ((key.contains(SignalKConstants.dot+SignalKConstants.meta+SignalKConstants.dot) && skipMeta==false) || key.contains(SignalKConstants.dot+SignalKConstants.meta+SignalKConstants.dot)==false) {
                        Object node = signalKModel.get(key);
                        if (node != null) {
                            temp.getFullData().put(key, node);
                        }
                    }
                }

            }
        }
        if (signalKModel.getFullData().isEmpty()) {
            return null;
        }
        return temp;
    }

    public static void fixSource(Json jsonDelta) {
        List<Json> updates=jsonDelta.at("updates").asJsonList();
        for(Json update:updates) {
            try {
                Json jsonSource=update.at("source");
                if (jsonSource!=null) {
                    Map<String, Json> map = jsonSource.asJsonMap();
                }
            } catch (UnsupportedOperationException ex1) {
                Json source = Json.object();
                try {
                    String sourceString = update.at("source").asString();
                    String[] parts = sourceString.split("[.]");
                    String label = parts[0];
                    String type = null;
                    source.set("label", label);
                    if (label.equals("urn:fairwind")==true) {
                        type="INTERNAL";
                    } else {
                        type = parts[1];
                        if (type.equals("NMEA0183")) {
                            String sentence = parts[2];
                            source.set("sentence", sentence);
                        }
                    }
                    source.set("type", type);
                } catch (UnsupportedOperationException ex2) {

                }
                update.set("source",source);
            }
        }

    }
}
