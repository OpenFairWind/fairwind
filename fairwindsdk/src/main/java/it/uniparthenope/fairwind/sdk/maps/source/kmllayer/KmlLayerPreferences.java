package it.uniparthenope.fairwind.sdk.maps.source.kmllayer;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 11/08/16.
 */
public class KmlLayerPreferences extends MapPreferences {
    private String url;
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url=url; }

    public KmlLayerPreferences() {
        super();
        setName("Kml Layer");
        setDesc("Overlay a Kml layer on the map");
    }

    public KmlLayerPreferences(String name, String url) {
        super(name);
        this.url=url;
    }




    public KmlLayerPreferences(Json json) {
        super(json);
    }

    @Override
    public void byJson(Json json) {
        super.byJson(json);

        this.url=json.at("url").toString().replace("\"","");
    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("url",url);

        return json;
    }
}
