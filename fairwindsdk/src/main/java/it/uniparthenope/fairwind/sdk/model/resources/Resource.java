package it.uniparthenope.fairwind.sdk.model.resources;

import com.cocoahero.android.geojson.Feature;

import java.util.UUID;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

/**
 * Created by raffaelemontella on 29/09/2017.
 */

public  class Resource {

    public final static String LOG_TAG="TRACK";

    private String name;
    private String description;
    private String uuid="urn:mrn:signalk:uuid:"+UUID.randomUUID();


    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUuid() { return uuid; }


    public void setName(String name) { this.name=name; }
    public void setDescription(String description) { this.description=description; }
    public void setUuid(String uuid) { this.uuid=uuid; }


    public Json asJson() {
        Json result = Json.object();

        return result;
    }

    public void byJson(Json json) {
        if (json.at("name")!=null) {
            name = json.at("name").toString().replace("\"", "");
        }

        if (json.at("description")!=null) {
            description = json.at("description").toString().replace("\"", "");
        }


    }


    public  SignalKModel asSignalK() {
        SignalKModel signalKobject= SignalKModelFactory.getCleanInstance();

        return signalKobject;
    }


}
