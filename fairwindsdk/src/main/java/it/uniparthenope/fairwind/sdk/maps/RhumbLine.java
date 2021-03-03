package it.uniparthenope.fairwind.sdk.maps;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Map;

import it.uniparthenope.fairwind.sdk.maps.core.Polyline1;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 10/04/2017.
 */

public class RhumbLine extends Polyline1 implements FairWindEventListener {
    public static final String LOG_TAG="RHUMLINE";

    private String vessel_uuid;

    private FairWindEvent nextPointHRefAdd;
    private FairWindEvent nextPointHRefDel;
    private FairWindEvent previousPointHRefAdd;
    private FairWindEvent previousPointHRefDel;

    private GeoPoint previousPoint;
    private GeoPoint nextPoint;

    Handler handler=new Handler();


    public RhumbLine(MapView mapView, FairWindModel fairWindModel, String vessel_uuid) {
        super(mapView, fairWindModel);
        this.vessel_uuid=vessel_uuid;
        init();
    }

    private void init() {
        String basePath=SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;


        nextPointHRefAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint,
                null,1, PathEvent.EventType.ADD,this);

        nextPointHRefDel=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint,
                null,1, PathEvent.EventType.DEL,this);

        previousPointHRefAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_previousPoint,
                null,1, PathEvent.EventType.ADD,this);

        previousPointHRefDel=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_previousPoint,
                null,1, PathEvent.EventType.DEL,this);

        fairWindModel.register(nextPointHRefAdd);
        fairWindModel.register(nextPointHRefDel);
        fairWindModel.register(previousPointHRefAdd);
        fairWindModel.register(previousPointHRefDel);
    }

    @Override
    public void onEvent(final FairWindEvent event) {
        Log.d(LOG_TAG,"RhumbLine Event:"+event.getType()+"/"+event.getPath());

        handler.post(new Runnable() {
            @Override
            public void run() {

                String basePath=SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;

                if (event.isMatching(nextPointHRefAdd) && nextPoint==null) {
                    String nextPointHRef = (String) ((SignalKModel) fairWindModel).get(basePath + SignalKConstants.nav_courseRhumbline_nextPoint);
                    if (nextPointHRef!=null && nextPointHRef.isEmpty()==false){
                        Map<String,Resource> waypointMap=fairWindModel.getWaypoints().asMap();
                        String nextPointHRefId=nextPointHRef.split("[.]")[2];
                        Waypoint nextWaypoint=(Waypoint) waypointMap.get(nextPointHRefId);
                        nextPoint=nextWaypoint.getPosition().asGeoPoint();
                    }
                } else if (event.isMatching(previousPointHRefAdd) && previousPoint==null) {
                    String previousPointHRef=(String)((SignalKModel)fairWindModel).get(basePath+SignalKConstants.nav_courseRhumbline_previousPoint);
                    if (previousPointHRef!=null && previousPointHRef.isEmpty()==false) {
                        Map<String,Resource> waypointMap=fairWindModel.getWaypoints().asMap();
                        String previousPointHRefId=previousPointHRef.split("[.]")[2];
                        Waypoint previousWaypoint=(Waypoint) waypointMap.get(previousPointHRefId);
                        previousPoint=previousWaypoint.getPosition().asGeoPoint();
                    }
                } else if (event.isMatching(previousPointHRefDel) && previousPoint!=null) {
                    previousPoint=null;

                } else if (event.isMatching(nextPointHRefDel) && nextPoint!=null) {
                    nextPoint=null;
                }

                if (nextPoint!=null && previousPoint!=null) {
                    ArrayList<GeoPoint> points=new ArrayList<GeoPoint>();
                    points.add(previousPoint);
                    points.add(nextPoint);
                    setColor(Color.BLACK);
                    setWidth(1);
                    setPoints(points);
                    add();
                    mapView.invalidate();
                } else if (nextPoint==null && previousPoint==null) {
                    remove();
                    mapView.invalidate();
                }
            }
        });
    }
}
