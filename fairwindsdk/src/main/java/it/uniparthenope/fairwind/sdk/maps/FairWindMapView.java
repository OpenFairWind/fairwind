package it.uniparthenope.fairwind.sdk.maps;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;


import org.joda.time.DateTime;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.core.Line;
import it.uniparthenope.fairwind.sdk.maps.source.tiledlayer.TiledLayer;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.util.GroundWaterType;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.JsonGetHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 04/06/16.
 */
public class FairWindMapView extends MapView implements Runnable {
    public static final String LOG_TAG="MAP_VIEW";

    public static int REFRESH_MILLS=1000;

    private FolderOverlay activeLatLonGrid;
    private ScaleBarOverlay scaleBarOverlay;
    private Line line;

    private Handler handler=new Handler();
    private FairWindModel fairWindModel;


    public FairWindMapView(Context context) {
        super(context);
        initializeViews( context,  null, 0);
    }

    public FairWindMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initializeViews( context,  attributeSet, 0);
    }

    public static Json getMapsConfigOverlaysAsJson(FairWindModel fairWindModel, Context context) {
        Json result=null;
        String jsonAsString=fairWindModel.getPreferences().getConfigProperty(Constants.PREF_KEY_MAPS_CONFIG_OVERLAYS, null);
        if (jsonAsString==null || jsonAsString.isEmpty()==true) {
            try {
                jsonAsString=Utils.readTextFromResource(context.getResources(),R.raw.default_mappreferences);
                fairWindModel.getPreferences().setConfigProperty(Constants.PREF_KEY_MAPS_CONFIG_OVERLAYS, jsonAsString);
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        }
        result=Json.read(jsonAsString);
        return result;
    }


    public  void findVessel() {
        GeoPoint position=new GeoPoint(0, 0);
        if (fairWindModel.getNavPosition("self")!=null) {
            Position pos=fairWindModel.getNavPosition("self");
            if (pos!=null) {
                position =pos.asGeoPoint();
            }
        }

        IMapController mapController = getController();
        mapController.setCenter(position);
    }

    public void setCenter(Position position) {
        IMapController mapController = getController();
        mapController.setCenter(position.asGeoPoint());
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {

        Configuration.getInstance().setTileFileSystemCacheMaxBytes(1000000000L);
        Configuration.getInstance().setTileFileSystemCacheTrimBytes(900000000L);




        //Disable StrictMode.ThreadPolicy to perform network calls in the UI thread.
        //Yes, it's not the good practice, but this is just a tutorial!
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        fairWindModel= Utils.getFairWindModel();

        setBuiltInZoomControls(true);
        setMultiTouchControls(true);


        GeoPoint position=new GeoPoint(0, 0);
        if (fairWindModel.getNavPosition("self")!=null) {
            Position pos=fairWindModel.getNavPosition("self");
            if (pos!=null) {
                position =pos.asGeoPoint();
            }
        }

        IMapController mapController = getController();

        int zoomLevel=fairWindModel.getPreferences().getConfigPropertyInt(Constants.PREF_KEY_MAPS_ZOOMLEVEL, 1);
        Json jsonMapCenter=fairWindModel.getPreferences().getConfigPropertyJson(Constants.PREF_KEY_MAPS_MAPCENTER,null);
        if (jsonMapCenter!=null) {
            position.setCoords(jsonMapCenter.at("lat").asDouble(),jsonMapCenter.at("lon").asDouble());
        } else {
            jsonMapCenter=Json.object();
            jsonMapCenter.set("lon",position.getLongitude());
            jsonMapCenter.set("lat",position.getLatitude());
            fairWindModel.getPreferences().setConfigPropertyJson(Constants.PREF_KEY_MAPS_MAPCENTER,jsonMapCenter);
        }
        mapController.setZoom(zoomLevel);
        mapController.setCenter(position);

        setClickable(true);

        Json jsonMapPreferences=FairWindMapView.getMapsConfigOverlaysAsJson(fairWindModel,context);
        Map<String,Json> jsonMapPreferencesMap=jsonMapPreferences.asJsonMap();
        Collection<Json> jsonMapPreferencesCollection= jsonMapPreferencesMap.values();
        List<Json> jsonMapPreferencesList=new ArrayList(jsonMapPreferencesCollection);

        Collections.sort(jsonMapPreferencesList, new Comparator<Json>(){
            public int compare(Json o1, Json o2){
                int i1=MapPreferences.DEFAULT_ORDER,i2=MapPreferences.DEFAULT_ORDER;
                try {
                    i1=o1.at("order").asInteger();
                } catch (UnsupportedOperationException ex) {
                } catch (NumberFormatException ex) {
                }

                try {
                    i2=o2.at("order").asInteger();
                } catch (UnsupportedOperationException ex) {
                } catch (NumberFormatException ex) {
                }
                return i1 - i2 ;
            }
        });


        for(Json jsonMapPreferencesItem:jsonMapPreferencesList) {
            try {
                if (jsonMapPreferencesItem.has("className")) {


                    Json json = jsonMapPreferencesItem.at("className", null);
                    if (json != null) {
                        String className = json.asString().replace("\"", "");
                        Log.d(LOG_TAG, "className:" + className + " json:" + json);
                        Class mapPreferencesClass = Class.forName(className + "Preferences");
                        MapPreferences mapPreferences = (MapPreferences) mapPreferencesClass.newInstance();
                        mapPreferences.byJson(jsonMapPreferencesItem);
                        ITileSource tileSource = addByMapPreferences(mapPreferences);
                        if (mapPreferences.getOrder() == 0) {
                            setTileSource(tileSource);
                        }
                    }
                }
            } catch (UnsupportedOperationException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            }
        }


        setUseDataConnection(true); //keeps the mapView from loading online tiles using network connection.


        MapWaypoints mapWaypoints =new MapWaypoints(this,fairWindModel);
        mapWaypoints.add();

        Boat boat = new Boat(this,fairWindModel,"self",GroundWaterType.GROUND,5);
        boat.add();



        handler.postDelayed(this, REFRESH_MILLS);

        setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {
                MapView mapView=e.getSource();
                fairWindModel.getPreferences().setConfigPropertyInt(Constants.PREF_KEY_MAPS_ZOOMLEVEL, mapView.getZoomLevel());
                updateGridlines();
                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
                MapView mapView=e.getSource();
                Json json=Json.object();
                IGeoPoint mapCenter=mapView.getMapCenter();
                json.set("lon",mapCenter.getLongitude());
                json.set("lat",mapCenter.getLatitude());
                fairWindModel.getPreferences().setConfigPropertyJson(Constants.PREF_KEY_MAPS_MAPCENTER,json);
                updateGridlines();
                return true;
            }
        }, 1000 ));


        LatLonGridlineOverlay.setDefaults();
        LatLonGridlineOverlay.fontSizeDp=12;
        setTilesScaledToDpi(true);

        updateGridlines();



        MapEventsReceiver mReceive = new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Position position=new Position(p);
                Waypoint waypoint=new Waypoint("Temporary",position);
                fairWindModel.getWaypoints().add(waypoint);
                return true;
            }


        };


        //Creating a handle overlay to capture the gestures
        MapEventsOverlay OverlayEventos = new MapEventsOverlay(getContext(), mReceive);
        getOverlays().add(OverlayEventos);

        scaleBarOverlay=new ScaleBarOverlay(this);
        scaleBarOverlay.setAlignBottom(true);
        scaleBarOverlay.setAlignRight(true);
        scaleBarOverlay.setUnitsOfMeasure(ScaleBarOverlay.UnitsOfMeasure.nautical);
        getOverlays().add(scaleBarOverlay);



    }

    private ITileSource addByMapPreferences(MapPreferences mapPreferences) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ITileSource tileSource=null;
        if (mapPreferences.isVisible()) {
            Class fairWindMapOverlayClass=Class.forName(mapPreferences.getClassName());
            FairWindMapOverlay fairWindMapOverlay=(FairWindMapOverlay)fairWindMapOverlayClass.newInstance();
            if (fairWindMapOverlay!=null) {
                fairWindMapOverlay.setMapPreferences(this,fairWindModel,mapPreferences);
                fairWindMapOverlay.add();
                if (fairWindMapOverlay instanceof TiledLayer) {
                    TiledLayer tiledLayer=(TiledLayer)fairWindMapOverlay;
                    tileSource=tiledLayer.getTileSource();
                }
            }

        }
        return tileSource;
    }

    HashMap<String,Boat> boatsSet=new HashMap<String,Boat>();

    @Override
    public void run() {
        Log.d(LOG_TAG, "run");

        Position selfPosition=fairWindModel.getNavPosition("self");
        if (selfPosition==null) {
            return;
        }

        JsonGetHandler jsonGetHandler=new JsonGetHandler();

        List<String> vesselsList = jsonGetHandler.getMatchingPaths((SignalKModel)fairWindModel,"vessels.*");

        if (vesselsList.size()>0) {


            for (String key:vesselsList) {

                String uuid = key.split("\\.")[1];
                Boat boat=boatsSet.get(uuid);
                if (boat==null && uuid.equals(SignalKConstants.self)==false && uuid.equals("self")==false ) {
                    Position position = fairWindModel.getNavPosition(uuid);
                    //if (latitude != null && longitude != null && (fairWindModel.getPosition().distanceTo(position)<mAISRangeSeekBar.getProgress() || mAISRangeSeekBar.getProgress()==101)) {
                    if (position!=null) {
                        boat=new Boat(this,fairWindModel,uuid,GroundWaterType.GROUND,5);
                        boat.add();
                        boatsSet.put(uuid,boat);
                    }
                }

            }
        }


        Iterator<Map.Entry<String,Boat>> iter = boatsSet.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,Boat> entry = iter.next();
            Boat boat=entry.getValue();
            if (boat.getLastUpdate()!=null) {
                long delta= DateTime.now().getMillis() - boat.getLastUpdate().getMillis();
                if (delta > 60000) {
                    boat.remove();
                    iter.remove();
                }
            }

        }

        // Set the next call
        handler.postDelayed(this, REFRESH_MILLS);
    }


    protected void updateGridlines(){

        if (activeLatLonGrid != null) {
            getOverlayManager().remove(activeLatLonGrid);
            activeLatLonGrid.onDetach(this);
        }
        LatLonGridlineOverlay.backgroundColor= Color.LTGRAY;
        LatLonGridlineOverlay.fontColor=Color.GRAY;
        LatLonGridlineOverlay.lineColor=Color.GRAY;
        activeLatLonGrid = LatLonGridlineOverlay.getLatLonGrid(getContext(), this);
        getOverlays().add(activeLatLonGrid);

    }


    public void toggleRuler() {
        if (line==null) {
            BoundingBox boundingBox=getBoundingBox();
            double dLat=boundingBox.getLatitudeSpan()*.25;
            double dLon=boundingBox.getLongitudeSpan()*.25;

            line=new Line(this,fairWindModel,
                    new Position(boundingBox.getLatSouth()+dLat,boundingBox.getLonWest()+dLon,0.0),
                    new Position(boundingBox.getLatNorth()-dLat,boundingBox.getLonEast()-dLon,0.0));
            line.add();
        } else {
            line.remove();
            line=null;

        }
    }

    public void layersUpdate() {

    }
}
