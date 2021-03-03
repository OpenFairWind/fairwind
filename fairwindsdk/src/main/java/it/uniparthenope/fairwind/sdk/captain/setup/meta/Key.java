package it.uniparthenope.fairwind.sdk.captain.setup.meta;

import java.util.Map;

import mjson.Json;

/**
 * Created by raffaelemontella on 09/02/2017.
 */

public class Key {
    private String path;
    private String description;
    private String units;

    public String getPath() { return path; }
    public String getDescription() { return  description;}
    public String getUnits() { return units; }

    public Key(String path, String description, String units) {
        this.path=path;
        this.description=description;
        this.units=units;
    }

    public Key(String key,Json json) {
        byJson(key,json);
    }

    public void byJson(String path,Json json) {
        this.path=path;
        description=json.at("description","").asString().replace("\"","");
        units=json.at("units","").asString().replace("\"","");
    }

    public Json asJson() {
        Json json=Json.object();
        Json jsonKey=Json.object();
        jsonKey.set("description",description);
        if (units!=null && !units.isEmpty()) {
            jsonKey.set("units", units);
        }
        json.set(path,jsonKey);
        return json;
    }
}
