package it.uniparthenope.fairwind.sdk.maps;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.HashMap;


import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.maps.core.Marker1;
import it.uniparthenope.fairwind.sdk.maps.core.Polyline1;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoints;
import it.uniparthenope.fairwind.sdk.ui.EditLatitudeLongitude;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 25/03/2017.
 */

public class MapWaypoint extends Marker1 implements FairWindEventListener {

    public static final String LOG_TAG="MAPWAYPOINT";

    private String vessel_uuid=SignalKConstants.self;
    private String uuid;
    private Handler handler=new Handler();
    private Polyline1 layline1=null;
    private Polyline1 layline2=null;
    boolean laylines=false;

    private FairWindEvent nextPointHRefAdd;
    private FairWindEvent nextPointHRefDel;
    private FairWindEvent envWindDirectionTrueAdd;


    public MapWaypoint(MapView mapView, FairWindModel fairWindModel, String uuid) {
        super(mapView, fairWindModel);
        this.uuid=uuid;
        init();
    }

    public MapWaypoint(MapView mapView, FairWindModel fairWindModel, int resourceId, int scale, int color, String uuid) {
        super(mapView, fairWindModel, resourceId, scale, color);
        this.uuid=uuid;
        init();
    }

    public MapWaypoint(MapView mapView, FairWindModel fairWindModel, Drawable drawable, Position position, Double angle, int scale, int color, String text, String uuid) {
        super(mapView, fairWindModel, drawable, position, angle, scale, color, text);
        this.uuid=uuid;
        init();
    }





    private void init() {
        String basePath= SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;
        setInfoWindow(new MapWaypoint.MyInfoWindow());


        nextPointHRefAdd=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint,
                null,1, PathEvent.EventType.ADD,this);

        nextPointHRefDel=new FairWindEvent(
                basePath+SignalKConstants.nav_courseRhumbline_nextPoint,
                null,1, PathEvent.EventType.DEL,this);
        envWindDirectionTrueAdd=new FairWindEvent(
                basePath+SignalKConstants.env_wind_directionTrue+".value",
                null,1, PathEvent.EventType.ADD,this);

        fairWindModel.register(nextPointHRefAdd);
        fairWindModel.register(nextPointHRefDel);
        fairWindModel.register(envWindDirectionTrueAdd);
    }



    @Override
    public void onAdd() {
        if (layline1!=null) {
            layline1.add();
        }
        if (layline2!=null) {
            layline2.add();
        }
    }

    @Override
    public void onRemove() {
        if (layline1!=null) {
            layline1.remove();
        }
        if (layline2!=null) {
            layline2.remove();
        }
    }

    @Override
    public void onEvent(final FairWindEvent event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG,"onEvent:"+event.getType()+"/"+event.getPath());
                String basePath= SignalKConstants.vessels+SignalKConstants.dot+ Util.fixSelfKey(vessel_uuid)+SignalKConstants.dot;
                if (event.isMatching(envWindDirectionTrueAdd) && layline1!=null && layline2!=null) {
                    Double windDirectionTrue = (Double) (((SignalKModel) fairWindModel).get(event.getPath()));
                    if (windDirectionTrue != null && Double.isNaN(windDirectionTrue) == false) {
                        windDirectionTrue = Math.toDegrees(windDirectionTrue);

                        float angle=45;
                        Double bearingTrackTrue=(Double) ((SignalKModel)fairWindModel).get(basePath+SignalKConstants.nav_courseRhumbline_nextPoint_bearingMagnetic);
                        if (bearingTrackTrue!=null && Double.isNaN(bearingTrackTrue)==false) {
                            bearingTrackTrue=Math.toDegrees(bearingTrackTrue);
                            double delta=(windDirectionTrue-bearingTrackTrue);
                            if (Math.abs(delta)>90) {
                                angle=60;
                            }
                            if (delta<0) {
                                layline1.setColor(Color.GREEN);
                                layline2.setColor(Color.RED);
                            } else {
                                layline1.setColor(Color.RED);
                                layline2.setColor(Color.GREEN);
                            }
                        }

                        float deg1=(float) (windDirectionTrue - angle);
                        if (deg1<0) {
                            deg1=360+deg1;
                        }
                        float deg2=(float) (windDirectionTrue + angle);
                        if (deg2>360) {
                            deg2=deg2-360;
                        }

                        Position position = fairWindModel.getNavPosition(vessel_uuid);
                        double distance = getPosition().distanceTo(position.asGeoPoint())/2.0;
                        GeoPoint point1=getPosition().destinationPoint(distance, deg1);
                        GeoPoint point2=getPosition().destinationPoint(distance, deg2);

                        ArrayList<GeoPoint> points1 = new ArrayList<>();
                        points1.add(new GeoPoint(getPosition().getLatitude(), getPosition().getLongitude()));
                        points1.add(point1);
                        layline1.setWidth(2);
                        layline1.setPoints(points1);
                        ArrayList<GeoPoint> points2 = new ArrayList<>();
                        points2.add(new GeoPoint(getPosition().getLatitude(), getPosition().getLongitude()));
                        points2.add(point2);
                        layline2.setWidth(2);
                        layline2.setPoints(points2);
                        mapView.invalidate();
                    }
                } else if (event.isMatching(nextPointHRefAdd) && layline1==null && layline2==null) {
                    String nextPointHRef=(String)((SignalKModel)fairWindModel).get(basePath+SignalKConstants.nav_courseRhumbline_nextPoint);
                    if (nextPointHRef!=null && nextPointHRef.isEmpty()==false) {
                        String nextPointHRefId=nextPointHRef.split("[.]")[2];
                        if (nextPointHRefId!=null && nextPointHRefId.isEmpty()==false) {
                            if (uuid.toString().equals(nextPointHRefId)) {
                                layline1 = new Polyline1(mapView, fairWindModel);
                                layline2 = new Polyline1(mapView, fairWindModel);
                                layline1.add();
                                layline2.add();
                                mapView.invalidate();
                            }
                        }
                    }
                } else if (event.isMatching(nextPointHRefDel) && layline1!=null && layline2!=null) {

                    layline1.remove();
                    layline2.remove();
                    layline1 = null;
                    layline2 = null;
                    mapView.invalidate();

                }
            }
        });
    }

    class MyInfoWindow extends InfoWindow implements FairWindEventListener {
        Waypoint waypoint=null;


        public MyInfoWindow() {
            super(R.layout.infowindow_waypoint, mapView);
            Waypoints waypoints=fairWindModel.getWaypoints();
            HashMap<String,Resource> map=waypoints.asMap();
            waypoint=(Waypoint) map.get(uuid.toString());


            update();

            FairWindEvent fairWindEvent=new FairWindEvent(FairWindModelBase.getNavPositionPath("self")+".*",null,0, PathEvent.EventType.ADD,this);
            fairWindModel.register(fairWindEvent);

        }
        public void onClose() {
            fairWindModel.unregister(this);
        }

        public void onOpen(Object arg0) {
            final EditText editId = (EditText) mView.findViewById(R.id.editText_waypoint_id);
            final EditText editDesc = (EditText) mView.findViewById(R.id.editText_waypoint_desc);
            final EditLatitudeLongitude editLatitudeLongitude=(EditLatitudeLongitude)mView.findViewById(R.id.editLatitudelongitude);

            if (waypoint!=null) {
                editId.setText(Formatter.formatString(waypoint.getId(),"n/a"));
                editDesc.setText(Formatter.formatString(waypoint.getDescription(),"n/a"));
                Position position=waypoint.getPosition();
                editLatitudeLongitude.setPosition(position);
            }

            Button buttonGoto=(Button)mView.findViewById(R.id.button_goto);
            buttonGoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fairWindModel.getWaypoints().goTo(waypoint);
                    closeInfoWindow();
                }
            });

            Button buttonSave=(Button)mView.findViewById(R.id.button_save);
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id=editId.getText().toString();
                    String desc=editDesc.getText().toString();
                    waypoint.setId(id);
                    waypoint.setDescription(desc);
                    Position position=editLatitudeLongitude.getPosition();
                    waypoint.setPosition(position);
                    fairWindModel.getWaypoints().add(waypoint);
                    closeInfoWindow();
                }
            });

            Button buttonDelete=(Button)mView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fairWindModel.getWaypoints().remove(waypoint);
                    closeInfoWindow();
                }
            });

            Button buttonClose=(Button)mView.findViewById(R.id.button_close);
            buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeInfoWindow();
                }
            });
        }

        @Override
        public void onEvent(FairWindEvent event) {
            if (mView!=null) {
                mView.post(new Runnable() {
                    @Override
                    public void run() {

                        update();

                    }
                });
            }
        }

        private void update() {
            TextView txtRange = (TextView) mView.findViewById(R.id.textView_waypoint_range);
            TextView txtBearing = (TextView) mView.findViewById(R.id.textView_waypoint_bearing);
            TextView txtETA = (TextView) mView.findViewById(R.id.textView_waypoint_eta);

            Position position=fairWindModel.getNavPosition("self");
            if (position != null ) {

                double bearing=waypoint.getPosition().bearingTo(position);
                double range=waypoint.getPosition().distanceTo(position);
                double eta=range/fairWindModel.getAnySpeed("self");

                txtRange.setText(Formatter.formatRange(Formatter.UNIT_RANGE_NM,range,"n/a"));
                txtBearing.setText(Formatter.formatDirection(Math.toRadians(bearing),"n/a"));
                txtETA.setText(Formatter.formatTimeSpan(Formatter.TIME_STYLE_HHMM,eta,"n/a"));
            }
        }
    }
}
