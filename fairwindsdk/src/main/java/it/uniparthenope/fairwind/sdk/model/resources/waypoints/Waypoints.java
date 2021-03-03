package it.uniparthenope.fairwind.sdk.model.resources.waypoints;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.UUID;


import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.Resources;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 08/03/2017.
 */

public class Waypoints extends Resources {
    public static final String LOG_TAG="WAYPOINTS";



    public Waypoints(SignalKModel signalKModel) {
        super(signalKModel,SignalKConstants.resources_waypoints,"waypoints");

    }

    @Override
    public Resource onNewResource(String uuid, Json jsonItem) {
        return new Waypoint(uuid,jsonItem);
    }

    public void add(Waypoint waypoint) {
        SignalKModel signalKModel=getSignalKModel();
        SignalKModel signalKObject=waypoint.asSignalK();
        SortedMap<String,Object> sortedMap=signalKObject.getFullData();
        for(String key:sortedMap.keySet()) {
            signalKModel.getFullData().put(key,sortedMap.get(key));
        }
        String id=sortedMap.firstKey().split("[.]")[2];
        PathEvent pathEvent=new PathEvent(SignalKConstants.resources_waypoints+SignalKConstants.dot+id,1, PathEvent.EventType.ADD);
        signalKModel.getEventBus().post(pathEvent);

        Log.d(LOG_TAG,"Added:"+waypoint);
    }

    public void remove(UUID uuid) {
        Waypoint waypoint=(Waypoint) asMap().get(uuid);
        if (waypoint!=null) {
            remove(waypoint);
        }
    }
    public void remove(Waypoint waypoint) {
        SignalKModel signalKModel=getSignalKModel();
        String id=waypoint.getUuid().toString();

        // Check if this waypoint is the next point of the current course
        String nextPointHRef=(String) (signalKModel.get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_nextPoint));
        String previousPointHRef=(String) (signalKModel.get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseRhumbline_previousPoint));

        if ((nextPointHRef!=null && nextPointHRef.split("[.]")[2].equals(id)) || (previousPointHRef!=null && previousPointHRef.split("[.]")[2].equals(id))) {
            goTo();
        }


        NavigableMap<String,Object> map=signalKModel.getSubMap(SignalKConstants.resources_waypoints+SignalKConstants.dot+id);
        if (map!=null) {
            PathEvent pathEvent=null;
            Iterator<String> iterator=map.keySet().iterator();
            while(iterator.hasNext()) {
                String key=iterator.next();
                pathEvent = new PathEvent(key, 1, PathEvent.EventType.DEL);
                signalKModel.getFullData().remove(key);
                signalKModel.getEventBus().post(pathEvent);
            }

            pathEvent = new PathEvent(SignalKConstants.resources_waypoints + SignalKConstants.dot + id, 1, PathEvent.EventType.DEL);
            signalKModel.getEventBus().post(pathEvent);

            Log.d(LOG_TAG, "Removed:" + waypoint);
        }
    }

    public void goTo() {
        SignalKModel signalKModel=getSignalKModel();
        String keyNavCourse=SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_course;
        NavigableMap<String,Object> map=signalKModel.getSubMap(keyNavCourse);
        if (map!=null) {
            PathEvent pathEvent=null;
            Iterator<String> iterator=map.keySet().iterator();
            while(iterator.hasNext()) {
                String key=iterator.next();
                pathEvent = new PathEvent(key, 1, PathEvent.EventType.DEL);
                signalKModel.getFullData().remove(key);
                signalKModel.getEventBus().post(pathEvent);
            }

            pathEvent = new PathEvent(keyNavCourse, 1, PathEvent.EventType.DEL);
            signalKModel.getEventBus().post(pathEvent);
        }
    }
    public void goTo(UUID uuid) {
        Waypoint waypoint=(Waypoint) asMap().get(uuid);
        if (waypoint!=null) {
            goTo(waypoint);
        }
    }

    public void goTo(Waypoint waypoint){
        SignalKModel signalKModel=getSignalKModel();

        // Set the prev waypoint position
        Position previousPosition=new Position(signalKModel);
        Waypoint previousWaypoint=new Waypoint("Start",previousPosition);
        add(previousWaypoint);
        goTo(previousWaypoint,waypoint);
    }

    public void goTo(Waypoint previousWaypoint, Waypoint waypoint) {
        SignalKModel signalKModel=getSignalKModel();

        // Cancel previous navigation
        goTo();

        if (previousWaypoint!=null) {
            // Get the waypoint reference key
            String keyPreviousPointHRef = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_previousPoint;

            // Create an event for the key
            PathEvent pathEvenPrevioustPointHRef = new PathEvent(keyPreviousPointHRef, 1, PathEvent.EventType.ADD);

            // Get the value
            String valuePreviousPointHRef = previousWaypoint.getHRef();

            // Set the document
            signalKModel.getFullData().put(keyPreviousPointHRef, valuePreviousPointHRef);

            // Fire the event
            signalKModel.getEventBus().post(pathEvenPrevioustPointHRef);

            // Get the previous point position key
            String keyPreviousPointPosition = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_previousPoint_position;

            // Create the event
            PathEvent pathEventPreviousPointPosition = new PathEvent(keyPreviousPointPosition, 1, PathEvent.EventType.ADD);

            // Get the value
            SortedMap<String, Object> mapPrev = Utils.Json2SortedMap(keyPreviousPointPosition, previousWaypoint.getPosition().asJson());

            // Set the document
            signalKModel.getFullData().putAll(mapPrev);

            // Fire the event
            signalKModel.getEventBus().post(pathEventPreviousPointPosition);
        }

        if (waypoint!=null) {
            // Get the waypoint reference key
            String keyNextPointHRef = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint;

            // Create an event for the key
            PathEvent pathEventNextPointHRef = new PathEvent(keyNextPointHRef, 1, PathEvent.EventType.ADD);

            // Get the value
            String valueNextPointHRef = waypoint.getHRef();

            // Set the document
            signalKModel.getFullData().put(keyNextPointHRef, valueNextPointHRef);

            // Fire the event
            signalKModel.getEventBus().post(pathEventNextPointHRef);

            // Get the next point position key
            String keyNextPointPosition = SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseRhumbline_nextPoint_position;

            // Create the event
            PathEvent pathEventNextPointPosition = new PathEvent(keyNextPointPosition, 1, PathEvent.EventType.ADD);

            // Set the next waypoint position
            SortedMap<String, Object> mapNext = Utils.Json2SortedMap(keyNextPointPosition, waypoint.getPosition().asJson());

            // Set the document
            signalKModel.getFullData().putAll(mapNext);

            // Fire the event
            signalKModel.getEventBus().post(pathEventNextPointPosition);
        }
    }

    private Order orderBy=Order.DATEANDTIME;
    private Sort sortBy=Sort.ASCENDANT;


    public ArrayList<Resource> asOrderedList() {
        ArrayList<Resource> result=super.asList();
        // Sorting
        Collections.sort(result, new Comparator<Resource>() {
            @Override
            public int compare(Resource resource2, Resource resource1)
            {
                int result=0;
                Waypoint waypoint1, waypoint2;
                switch (sortBy) {

                    case DESCENDANT:
                        waypoint1=(Waypoint)resource2;
                        waypoint2=(Waypoint)resource1;
                        break;

                    default:
                        waypoint1=(Waypoint)resource1;
                        waypoint2=(Waypoint)resource2;
                        break;
                }

                switch (orderBy) {
                    case DATEANDTIME:
                        result=waypoint1.getTimeStamp().compareTo(waypoint2.getTimeStamp());
                        break;
                    case NAME:
                        result=waypoint2.getId().compareTo(waypoint1.getId());
                    break;
                    case RANGE:
                        Position position= Utils.getFairWindModel().getPosition();
                        result= waypoint1.getRange(position).compareTo(waypoint2.getRange(position));
                    break;
                    case TYPE:
                        result=waypoint1.getId().compareTo(waypoint2.getId());
                    break;
                }
                return result;
            }
        });
        return result;
    }

    public void sortBy(int sort) {
        switch(sort) {
            case 0:
                sortBy=Sort.ASCENDANT;
                break;
            case 1:
                sortBy=Sort.DESCENDANT;
                break;
        }
    }

    public void orderBy(int order) {
        switch(order) {
            case 0:
                orderBy=Order.DATEANDTIME;
                break;
            case 1:
                orderBy=Order.NAME;
                break;
            case 2:
                orderBy=Order.RANGE;
                break;
            case 3:
                orderBy=Order.TYPE;
                break;
        }

    }
}
