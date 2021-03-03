package it.uniparthenope.fairwind.sdk.model.resources.waypoints;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.MapView;

import java.util.SortedMap;
import java.util.UUID;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.maps.MapWaypoint;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 07/03/2017.
 */

public class Waypoint extends Resource {
    public final static String LOG_TAG="WAYPOINT";

    private Position position;
    private String id;

    private Feature feature;
    public Feature getFeature() { return feature; }
    public void setFeature(Feature feature) { this.feature=feature;}

    private String group;
    private int type;




    public Waypoint(String id, Position position) {
        this.id=id;
        setPosition(position);
    }



    public Waypoint(String uuid,Json json) {
        byJson(json);
        setUuid(uuid);

    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;

        // Create geometry
        Point point = new Point(position.getLatitude(), position.getLongitude());

        Feature feature=getFeature();

        // Create feature with geometry
        if (feature == null) {
            feature = new Feature(point);
            setFeature(feature);
        }
        feature.setIdentifier(id);
        JSONObject jsonObject=feature.getProperties();
        if (jsonObject==null) {
            jsonObject=new JSONObject();
            feature.setProperties(jsonObject);
        }
        try {
            jsonObject.put("id",id);
            jsonObject.put("description",getDescription());
        } catch (JSONException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
    }

    @Override
    public void byJson(Json json) {
        super.byJson(json);
        try {
            Json jsonPosition=json.at("position");
            if (jsonPosition!=null) {
                Position position = new Position(jsonPosition);
                setPosition(position);
            }
        } catch (UnsupportedOperationException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }

        try {
            Json jsonFeature=json.at("feature");
            if (jsonFeature!=null) {
                String jsonString = jsonFeature.toString();
                try {
                    GeoJSONObject geoJsonObject = GeoJSON.parse(jsonString);
                    if (geoJsonObject.getType().equalsIgnoreCase("Feature")) {
                        feature = (Feature) geoJsonObject;
                        setId(feature.getIdentifier());
                        if (feature.getProperties()!=null) {
                            String jsonStringPropierties = feature.getProperties().toString();
                            Json jsonPropierties=Json.read(jsonStringPropierties);
                            if (jsonPropierties != null) {
                                try {
                                    String description = jsonPropierties.asMap().get("description").toString();
                                    setDescription(description);
                                } catch (UnsupportedOperationException ex) {
                                    Log.e(LOG_TAG,ex.getMessage());
                                }
                            }
                        }
                    }
                } catch (RuntimeException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        } catch (JSONException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
    }

    @Override
    public Json asJson() {
        Json result=super.asJson();
        result.set("position",getPosition().asJson());
        try {
            JSONObject jsonObject=feature.toJSON();
            String jsonString=jsonObject.toString();
            result.set("feature",Json.read(jsonString));
        } catch (JSONException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
        return result;
    }

    @Override
    public SignalKModel asSignalK() {
        SignalKModel signalKobject= super.asSignalK();
        String source=getPosition().getSource();
        String basePath=SignalKConstants.resources_waypoints+SignalKConstants.dot+getUuid().toString();
        signalKobject.putPosition(basePath+".position",position.getLatitude(),position.getLongitude(),position.getAltitude(),source,position.getTimeStamp().toDateTimeISO().toString());
        try {
            JSONObject jsonObject=feature.toJSON();
            String jsonString=jsonObject.toString();
            Json json=Json.read(jsonString);
            SortedMap<String,Object> sortedMap= Utils.Json2SortedMap(basePath+".feature",json);
            signalKobject.putAll(sortedMap);
        } catch (JSONException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        return signalKobject;
    }

    @Override
    public String toString() {
        return getUuid().toString()+":"+id+"("+Formatter.formatLatitude(Formatter.COORDS_STYLE_DDMM,position.getLatitude(),"n/a")+","+
                Formatter.formatLongitude(Formatter.COORDS_STYLE_DDMM,position.getLongitude(),"n/a")+","+
                position.getAltitude()+") "+getDescription();
    }

    public MapWaypoint asMapWaypoint(MapView mapView, FairWindModel fairWindModel) {
       Drawable drawable= mapView.getContext().getResources().getDrawable(R.drawable.cross_50x50);
       int color=1;
       MapWaypoint mapWaypoint=new MapWaypoint(mapView, fairWindModel, drawable, getPosition(), 0.0, 1, color, id,getUuid());

       return mapWaypoint;
    }



    public String getId() { return id; }


    public void setId(String id) {
        this.id=id;
        if (feature!=null) {
            feature.setIdentifier(id);
            JSONObject jsonObject = feature.getProperties();
            if (jsonObject==null) {
                jsonObject = new JSONObject();
                feature.setProperties(jsonObject);
            }
            try {
                jsonObject.put("id", id);
            } catch (JSONException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);

        if (feature!=null) {
            JSONObject jsonObject = feature.getProperties();
            if (jsonObject==null) {
                jsonObject = new JSONObject();
                feature.setProperties(jsonObject);
            }
            try {
                jsonObject.put("description", description);
            } catch (JSONException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        }
    }

    public Double getLatitude() { return position.getLatitude(); }
    public Double getLongitude() { return position.getLongitude(); }
    public DateTime getTimeStamp() { return position.getTimeStamp(); }

    public String getHRef() {
        return SignalKConstants.resources_waypoints+SignalKConstants.dot+getUuid().toString();
    }

    public Double getRange(Position currentPosition) {

        Position position=getPosition();
        if (position!=null && currentPosition!=null) {
            return position.distanceTo(currentPosition);
        }
        return null;
    }

    public Double getBearing(Position currentPosition) {
        Position position=getPosition();
        if (position!=null && currentPosition!=null) {
            return Math.toRadians(position.bearingTo(currentPosition));
        }
        return null;
    }
}
