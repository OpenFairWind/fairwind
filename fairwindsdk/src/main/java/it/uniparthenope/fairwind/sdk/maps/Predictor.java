package it.uniparthenope.fairwind.sdk.maps;

import android.graphics.Color;
import android.os.Handler;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.maps.core.Polyline1;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.maps.core.Marker1;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 10/07/16.
 */
public class Predictor extends Marker1 implements FairWindEventListener {
    public static final String LOG_TAG="PREDICTOR";

    private String vessel_uuid;
    private CompassMode compassMode;
    private int predictorValue;
    private Polyline1 polyline;
    private Handler handler=new Handler();

    public Predictor(MapView mapView, FairWindModel fairWindModel, String vessel_uuid, CompassMode compassMode, int predictorValue) {
        super(mapView,fairWindModel,R.drawable.xpredictor, 3, Color.RED);

        this.vessel_uuid= FairWindModelBase.fixSelfKey(vessel_uuid);
        this.compassMode=compassMode;
        this.predictorValue=predictorValue;




        fairWindModel.register(
                new FairWindEvent(FairWindModelBase.getNavPositionPath(this.vessel_uuid)+".*",null,0, PathEvent.EventType.ADD,this));

        fairWindModel.register(
                new FairWindEvent(FairWindModelBase.getHeadingPath(this.vessel_uuid, this.compassMode)+".*",null,0, PathEvent.EventType.ADD,this));

        polyline=new Polyline1(mapView,fairWindModel);
        polyline.setColor(Color.RED);

        setEnabled(false);

        update();

    }


    private void update() {
        final Position position=fairWindModel.getNavPosition(vessel_uuid);

        if (position != null ) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Position predicted=fairWindModel.getPredictor(vessel_uuid,predictorValue);
                    if (predicted==null || (Math.abs(predicted.getLatitude()-position.getLatitude())<0.0001) && Math.abs(predicted.getLongitude()-position.getLongitude())<0.0001) {
                        predicted = position;
                        setIcon(null);
                    } else {
                        setIcon(getDrawable().mutate());
                    }
                    setPosition(predicted.asGeoPoint());
                    ArrayList<GeoPoint> points = new ArrayList<>();
                    points.add(position.asGeoPoint());
                    points.add(predicted.asGeoPoint());
                    polyline.setPoints(points);
                    mapView.invalidate();

                }
            });
        }
    }

    @Override
    public void onEvent(FairWindEvent event) {
        update();
    }

    @Override
    public void onAdd() {
        polyline.add();
    }

    @Override
    public void onRemove() {
        polyline.remove();
    }
}
