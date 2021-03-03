package it.uniparthenope.fairwind.sdk.maps;


import android.util.Log;


//import org.osmdroid.bonuspack.overlays.FolderZOverlay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;


import java.util.HashMap;

import java.util.List;


import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.core.Marker1;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;

import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoints;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 21/03/2017.
 */

public class MapWaypoints extends FolderOverlay implements FairWindMapOverlay, FairWindEventListener {
    public final static String LOG_TAG="WAYPOINTS";

    private MapView mapView;
    private FairWindModel fairWindModel;
    private HashMap<String, MapWaypoint> map=new HashMap<String, MapWaypoint>();

    public MapWaypoints(MapView mapView, FairWindModel fairWindModel) {
        super();
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
        fairWindModel.register(new FairWindEvent(SignalKConstants.resources_waypoints+".*",null,0, PathEvent.EventType.ADD,this));
        fairWindModel.register(new FairWindEvent(SignalKConstants.resources_waypoints+".*",null,0, PathEvent.EventType.DEL,this));

    }

    @Override
    public void onAdd() {

    }

    @Override
    public void onRemove() {

    }



    @Override
    public void add() {
        // Add the overlay to the map
        mapView.getOverlays().add(this);

        // Get the waypoints present in the document as list
        List<Resource> waypoints=fairWindModel.getWaypoints().asList();

        // For each waypoint...
        for(Resource resource:waypoints) {
            Waypoint waypoint=(Waypoint)resource;
            // Get the waypoint as Marker1
            MapWaypoint mapWaypoint=waypoint.asMapWaypoint(mapView,fairWindModel);

            // Put the marker in the local data structure
            map.put(waypoint.getUuid().toString(),mapWaypoint);

            // Add the marker to the ZFolderOverlay
            add(mapWaypoint);
        }

        // Invoke other custom code
        onAdd();

        // Invalidate the map and redraw it
        mapView.invalidate();
    }

    @Override
    public void remove() {
        onRemove();
        for(Marker1 marker:map.values()) {
            remove(marker);
        }
        map.clear();
        mapView.getOverlays().remove(this);
        mapView.invalidate();
    }

    @Override
    public void setMapPreferences(MapView mapView, FairWindModel fairWindModel, MapPreferences mapPreferences) {

    }

    @Override
    public void onEvent(FairWindEvent event) {
        boolean invalidate=false;
        String path=event.getPath();
        String id=path.split("[.]")[2];
        if (event.getType()== PathEvent.EventType.ADD) {

            // Get the map waypoint from the internal data structure
            MapWaypoint mapWaypoint = map.get(id);

            // Check if the marker already is on the map
            if (mapWaypoint!=null) {
                // It is an update: remove the mapWaypoint
                map.remove(mapWaypoint);
                remove(mapWaypoint);
            }

            // Create a new waypoint
            Waypoints waypoints=fairWindModel.getWaypoints();
            if (waypoints!=null) {

                // Get the waypoint map
                HashMap<String,Resource> mapWaypoints=waypoints.asMap();

                // Check if the map exists and is populated
                if (mapWaypoints!=null && mapWaypoints.size()>0) {

                    // Get the waypoint by its id
                    Waypoint waypoint = (Waypoint) mapWaypoints.get(id);

                    // Check if the waypoint id is consistent
                    if (waypoint!=null) {

                        // Create the marker
                        mapWaypoint = waypoint.asMapWaypoint(mapView, fairWindModel);

                        // Add the marker to the local data structure
                        map.put(waypoint.getUuid().toString(), mapWaypoint);

                        try {
                            // Add the overlay

                            //ZOverlay zOverlay=new ZOverlay(marker,0);

                            //mList.add(zOverlay);
                            //add(mapWaypoint,map.size());
                            add(mapWaypoint);
                        } catch (RuntimeException ex) {
                            Log.e(LOG_TAG,ex.getMessage());
                        }
                        // Invalidate the map
                        invalidate=true;
                    }
                }
            }
        } else {
            // Checl if the marker is on the map
            Marker1 marker = map.get(id);
            if (marker!=null) {
                // Remove it from the ZFolderOverlay
                remove(marker);

                // Remove from the local data structure
                map.remove(id);

                // Invalidate the map
                invalidate=true;
            }
        }

        if (invalidate) {
            // Invalidate the map and redraw it
            mapView.invalidate();
        }
    }
}
