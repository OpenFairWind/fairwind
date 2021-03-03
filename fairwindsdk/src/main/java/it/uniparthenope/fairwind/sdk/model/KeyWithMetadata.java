package it.uniparthenope.fairwind.sdk.model;

import mjson.Json;

/**
 * Created by raffaelemontella on 25/01/2017.
 */

public class KeyWithMetadata {
    private String key;
    private String description;
    private String units;
    public KeyWithMetadata(String key,Json json) {
        this.key=key;
        this.description=json.at("description").asString().replace("\"","");
        if (json.at("units")!=null) {
            this.units=json.at("units").asString().replace("\"","");
        }
    }

    public String getKey() { return  key; }
    public String getDescription() { return description; }
    public String getUnits() { return units; }
}
