package it.uniparthenope.fairwind.sdk.model.resources;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.UUID;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;

/**
 * Created by raffaelemontella on 29/09/2017.
 */

public abstract class Resources {

    public static final String LOG_TAG="RESOURCES";

    private SignalKModel signalKModel=null;

    private String basePath=null;
    private String tag=null;

    public SignalKModel getSignalKModel() { return signalKModel; }
    public void setSignalKModel(SignalKModel signalKModel) { this.signalKModel=signalKModel; }

    public abstract Resource onNewResource(String uuid, Json jsonItem);

    public Resources(SignalKModel signalKModel, String basePath, String tag) {
        this.signalKModel=signalKModel;
        this.basePath=basePath;
        this.tag=tag;
    }

    public HashMap<String,Resource> asMap() {
        Log.d(LOG_TAG,"HashMap");
        HashMap<String,Resource> map=new HashMap<String,Resource>();
        String path= basePath;
        NavigableMap<String, Object> navigableMap=signalKModel.getSubMap(path);
        if (navigableMap.size()>0) {

            SignalKModel signalKObject = SignalKModelFactory.getCleanInstance();
            signalKObject.putAll(navigableMap);

            JsonSerializer jsonSerializer = new JsonSerializer();

            try {

                Json json = jsonSerializer.writeJson(signalKObject);
                Map<String, Json> jsonMap = json.at(resources).at(tag).asJsonMap();
                for (String uuid : jsonMap.keySet()) {
                    Json jsonItem = jsonMap.get(uuid);
                    Resource resource = onNewResource(uuid,jsonItem);
                    map.put(resource.getUuid(), resource);
                }
            } catch (IOException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return map;
    }

    public ArrayList<Resource> asList() {
        Map<String,Resource> map=asMap();
        ArrayList<Resource> list=new ArrayList<>();
        for (String uuid:map.keySet()) {
            Resource resource=map.get(uuid);
            list.add(resource);
        }
        return list;
    }


}
