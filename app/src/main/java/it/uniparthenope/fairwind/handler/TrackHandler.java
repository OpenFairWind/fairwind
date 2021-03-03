package it.uniparthenope.fairwind.handler;

import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Geometry;
import com.cocoahero.android.geojson.LineString;

import org.json.JSONArray;
import org.json.JSONException;

import nz.co.fortytwo.signalk.model.SignalKModel;

import static nz.co.fortytwo.signalk.util.SignalKConstants.PUT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.name;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.values;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mjson.Json;
import nz.co.fortytwo.signalk.util.Position;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.TrackSimplifier;
import nz.co.fortytwo.signalk.util.Util;


/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class TrackHandler extends HandlerBase {

    public static final String LOG_TAG="TRACK_HANDLER";

    //private static final String COORDINATES = "coordinates";
    private static final String FEATURE = "feature";

    // simplify to about 2m out of true (at equator)
    private static final double TRACK_TOLERANCE = 0.00002;

    private static final int MAX_COUNT = 5000;
    private static final int SAVE_COUNT = 10;



    private Json msg ;
    //TODO: thread safety??
    private List<Position> positions = new ArrayList<Position>();

    private double distance=0;



    public TrackHandler()  {

        msg = createTrackMsg();
    }


    // @Override
    public void handle(SignalKModel node) {
        if (node.getData().size() == 0)
            return;

        if (node.getData().containsKey(vessels_dot_self_dot + nav_position_latitude) && node.getData().containsKey(vessels_dot_self_dot + nav_position_longitude)) {

            // we have a track change.
            Position current=new Position((double)node.get(vessels_dot_self_dot + nav_position_latitude), (double)node.get(vessels_dot_self_dot + nav_position_longitude));
            positions.add(current);

            // Update the distance
            if (positions.size()>=2) {
                Position previous=positions.get(positions.size()-2);
                distance+=Util.haversineMeters(current.latitude(),current.longitude(),previous.latitude(),previous.longitude());
            }

            // append to file
            if (positions.size() % SAVE_COUNT == 0) {
                // save it

                Json geoJson=getTrackAsGeoJson();
                Log.d(LOG_TAG,"Saving track:"+positions.size());

                updateSourceAndTime(msg,geoJson);
                Log.d(LOG_TAG,"Message:"+msg.toString());
                parse(msg);

            }
            if (positions.size() % (SAVE_COUNT * 4) == 0) {

                //Log.d(LOG_TAG,"Simplify track, size:" + coords.asList().size());

                positions = TrackSimplifier.simplify(positions, TRACK_TOLERANCE);

                //Log.d(LOG_TAG,"Simplify track, done, size:" + coords.asList().size());


            }
            // reset?
            if (positions.size() > MAX_COUNT) {

                Log.d(LOG_TAG,"Rolling to new track:"+positions.size());

                Json geoJson=getTrackAsGeoJson();
                updateSourceAndTime(msg,geoJson);
                Log.d(LOG_TAG,"Message:"+msg.toString());
                parse(msg);


                msg = createTrackMsg();
                //save new message
                Log.d(LOG_TAG,"Message:"+msg.toString());
                parse(msg);

            }
        }
    }

    private Json createTrackMsg(){
        positions.clear();
        distance=0;

        Json val = Json.object();
        val.set(SignalKConstants.PATH, "tracks" + dot + "urn:mrn:signalk:uuid:"+UUID.randomUUID().toString());
        Json currentTrack = Json.object();
        val.set(value, currentTrack);
        String time = Util.getIsoTimeString();
        time = time.substring(0, time.indexOf("."));

        currentTrack.set(name, "Vessel Track from "+time);
        currentTrack.set("description", "Auto saved vessel track from "+time);
        currentTrack.set("distance",distance);

        Json values = Json.array();
        values.add(val);

        Json update = Json.object();

        update.set(SignalKConstants.values, values);

        Json updates = Json.array();
        updates.add(update);
        Json msg = Json.object();
        msg.set(SignalKConstants.CONTEXT, resources);
        msg.set(SignalKConstants.PUT, updates);

        Json geoJson=getTrackAsGeoJson();
        updateSourceAndTime(msg,geoJson);



        Log.d(LOG_TAG,"Created new track msg:"+msg);

        return msg;
    }

    private void updateSourceAndTime(Json msg, Json geoJson) {
        Json update = msg.at(PUT).at(0);
        update.set(timestamp, Util.getIsoTimeString());
        update.set(source, Json.read("{\"internal\": {"+
                "\"value\": \"TrackProcessor\","+
                "\"timestamp\": \""+Util.getIsoTimeString()+"\"}}"));


        Json value=update.at(values).at(0).at("value");
        value.set(FEATURE,geoJson);
        value.set("distance",distance);
    }

    private Json getTrackAsGeoJson() {
        Json json=null;
        Feature feature=new Feature();
        LineString geometry = new LineString();

        JSONArray coords=new JSONArray();
        for(Position p: positions){

            JSONArray position=new JSONArray();
            try {
                position.put(p.longitude());
                position.put(p.latitude());
                coords.put(position);
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }

        geometry.setPositions(coords);

        geometry.setPositions(coords);
        feature.setGeometry(geometry);

        try {
            String sGeoJson=feature.toJSON().toString();
            json = Json.read(sGeoJson);

        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
        return json;
    }
}
