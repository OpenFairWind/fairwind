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
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 11/04/2017.
 */

public class BearingLine extends Polyline1 implements FairWindEventListener {

    public static final String LOG_TAG="BEARINGLINE";

    private String vessel_uuid;

    private FairWindEvent nextPointHRefAdd;
    private FairWindEvent nextPointHRefDel;
    private FairWindEvent navPositionTimestampAdd;

    private GeoPoint nextPoint=null;

    private Handler handler=new Handler();

    public BearingLine(MapView mapView, FairWindModel fairWindModel, String vessel_uuid) {
        super(mapView, fairWindModel);
        this.vessel_uuid=vessel_uuid;
        init();
    }

    private void init() {
        String basePath= SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;


        nextPointHRefAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint,
                null,1, PathEvent.EventType.ADD,this);

        nextPointHRefDel=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint,
                null,1, PathEvent.EventType.DEL,this);

        navPositionTimestampAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_position+".timestamp",
                null,1, PathEvent.EventType.ADD,this);



        fairWindModel.register(nextPointHRefAdd);
        fairWindModel.register(nextPointHRefDel);
        fairWindModel.register(navPositionTimestampAdd);
    }

    @Override
    public void onEvent(final FairWindEvent event) {
        Log.d(LOG_TAG,"BearingLine Event:"+event.getType()+"/"+event.getPath());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG,"BearingLine Event run():"+event.getType()+"/"+event.getPath());

                String basePath=SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;

                if (event.isMatching(navPositionTimestampAdd) && nextPoint!=null) {

                    Position position=fairWindModel.getNavPosition(vessel_uuid);

                    if (position != null && position.isValid() ) {
                        ArrayList<GeoPoint> points=new ArrayList<>();
                        points.add(nextPoint);
                        points.add(position.asGeoPoint());
                        setColor(Color.YELLOW);
                        setWidth(1);
                        setPoints(points);
                        mapView.invalidate();
                    }
                } else if (event.isMatching(nextPointHRefAdd) && nextPoint==null) {

                    String nextPointHRef = (String) ((SignalKModel) fairWindModel).get(basePath + SignalKConstants.nav_courseRhumbline_nextPoint);
                    Position position=fairWindModel.getNavPosition(vessel_uuid);
                    if (nextPointHRef!=null && nextPointHRef.isEmpty()==false && position!=null && position.isValid()) {

                        Map<String,Resource> waypointMap=fairWindModel.getWaypoints().asMap();
                        String nextPointHRefId=nextPointHRef.split("[.]")[2];
                        Waypoint nextWaypoint=(Waypoint) waypointMap.get(nextPointHRefId);
                        nextPoint=nextWaypoint.getPosition().asGeoPoint();
                        add();
                        mapView.invalidate();


                    }
                } else if (event.isMatching(nextPointHRefDel) && nextPoint!=null) {

                    remove();
                    mapView.invalidate();
                    nextPoint=null;
                }
            }
        });
    }
}
