package it.uniparthenope.fairwind.sdk.maps.core;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 14/04/2017.
 */

public class Line extends Polyline1 {
    private Marker1 m0;
    private Marker1 m1;
    private Marker m;

    private Handler handler=new Handler();

    public Line(MapView mapView, FairWindModel fairWindModel, Position p0, Position p1) {
        super(mapView, fairWindModel);
        Drawable d0=mapView.getContext().getResources().getDrawable(R.drawable.cross_50x50);;
        Drawable d1=mapView.getContext().getResources().getDrawable(R.drawable.cross_50x50);;
        m0=new Marker1(mapView,fairWindModel,d0, p0, 0.0, 1, Color.RED, null);
        m0.setDraggable(true);
        m0.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                update();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
        m1=new Marker1(mapView,fairWindModel,d1, p1, 0.0, 1, Color.BLUE, null);
        m1.setDraggable(true);
        m1.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                update();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
        setWidth(2);
        setColor(Color.CYAN);

        m=new Marker(mapView);
    }

    @Override
    public void onAdd() {
        update();
        mapView.getOverlays().add(m);
        m0.add();
        m1.add();

    }


    @Override
    public void onRemove() {
        mapView.getOverlays().remove(m);
        m1.remove();
        m0.remove();
    }



    public synchronized void update() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<GeoPoint> points=new ArrayList<GeoPoint>();
                points.add(m0.getPosition());
                points.add(m1.getPosition());
                setPoints(points);
                Position p0=new Position(m0.getPosition());
                Position p1=new Position(m1.getPosition());
                Double range=p0.distanceTo(p1);
                Double bearing1=p0.bearingTo(p1);
                Double bearing0=p1.bearingTo(p0);
                m1.setTitle(Formatter.formatDirection(Math.toRadians(bearing0),"--.--"));
                m0.setTitle(Formatter.formatDirection(Math.toRadians(bearing1),"--.--"));

                m.setTitle(Formatter.formatRange(Formatter.UNIT_RANGE_NM,range,"--.--"));
                m.setIcon(null);
                m.setPosition(new GeoPoint(
                        (m0.getPosition().getLatitude()+m1.getPosition().getLatitude())/2,
                        (m0.getPosition().getLongitude()+m1.getPosition().getLongitude())/2));


                mapView.invalidate();
            }
        });

    }
}
