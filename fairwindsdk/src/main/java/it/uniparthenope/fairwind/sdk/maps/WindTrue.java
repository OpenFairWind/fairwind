package it.uniparthenope.fairwind.sdk.maps;

import android.graphics.Color;
import android.util.Log;

import org.osmdroid.views.MapView;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.maps.core.Marker1;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.GroundWaterType;
import it.uniparthenope.fairwind.sdk.util.Position;
import it.uniparthenope.fairwind.sdk.util.WindMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 07/07/16.
 */
public class WindTrue extends Marker1 implements FairWindEventListener {
    public static final String LOG_TAG="WIND_TRUE";

    private CompassMode compassMode;
    private GroundWaterType groundWaterType;
    private WindMode windMode=WindMode.TRUE;
    private String vessel_uuid;

    public WindTrue(MapView mapView, FairWindModel fairWindModel, String vessel_uuid, CompassMode compassMode, GroundWaterType groundWaterType) {

        super(mapView,fairWindModel,R.drawable.blue_arrow_300x300, 3, Color.BLUE);


        this.vessel_uuid= FairWindModelBase.fixSelfKey(vessel_uuid);

        this.compassMode=compassMode;
        this.groundWaterType=groundWaterType;


        fairWindModel.register(new FairWindEvent(FairWindModelBase.getWindDirectionPath(this.vessel_uuid,compassMode,windMode)+".*",null,0, PathEvent.EventType.ADD,this));

        setTitle("Wind True");
        update();
    }

    private void update() {
        Double trueWindDirection = fairWindModel.getWindDirection(vessel_uuid, compassMode, windMode);
        Position position = fairWindModel.getNavPosition(vessel_uuid);


        if (position != null && trueWindDirection != null) {
            trueWindDirection=Math.toDegrees(trueWindDirection);
            Log.d(LOG_TAG,"trueWindDirection:"+trueWindDirection);

            update(
                    position,
                    trueWindDirection,
                    Formatter.formatDirection(Math.toRadians(trueWindDirection), "n/a"));
        }
    }

    @Override
    public void onEvent(FairWindEvent event) {

        update();
    }
}
