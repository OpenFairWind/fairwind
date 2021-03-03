package it.uniparthenope.fairwind.sdk.model.resources.tracks;

import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.LineString;

import org.joda.time.DateTime;
import org.json.JSONException;

import java.util.UUID;

import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class Track extends Resource {

    public final static String LOG_TAG="TRACK";

    private Double distance=null;
    private Feature feature;
    private DateTime timeStamp;
    //private Source source;


    public Feature getFeature() { return feature; }
    public void setFeature(Feature feature) { this.feature=feature;}

    /*

    {
  "feature": [
    {
      "geometry": {
        "coordinates": [

        ],
        "type": "LineString"
      },
      "id": "na",
      "type": "Feature",
      "properties": null
    }
  ],
  "name": "Vessel Track from 2017-09-29T19:08:04",
  "description": "Auto saved vessel track from 2017-09-29T19:08:04",
  "source": {
    "internal": {
      "value": "TrackProcessor",
      "timestamp": "2017-09-29T19:10:56.079Z"
    }
  },
  "timestamp": "2017-09-29T19:10:56.078Z"
}

     */

    public Track() {

    }

    public Track(String uuid, Json json) {
        byJson(json);
        setUuid(uuid);
    }

    public int getPoints() {

        LineString lineString= (LineString) feature.getGeometry();
        lineString.getPositions().size();
        return lineString.getPositions().size();
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public void byJson(Json json) {
        super.byJson(json);

        if (json.at("distance")!=null) {
            distance=json.at("distance").asDouble();
        }

        if (json.at("feature")!=null) {
            Json jsonFeature = json.at("feature");
            if (jsonFeature != null) {
                String jsonString = jsonFeature.toString();
                try {
                    GeoJSONObject geoJsonObject = GeoJSON.parse(jsonString);
                    if (geoJsonObject.getType().equalsIgnoreCase("Feature")) {
                        feature = (Feature) geoJsonObject;
                    }
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }
    }

    @Override
    public Json asJson() {
        Json result=super.asJson();

        return result;
    }

    @Override
    public SignalKModel asSignalK() {
        SignalKModel signalKobject= super.asSignalK();

        return signalKobject;
    }
}
