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
public class WindApparent extends Marker1 implements FairWindEventListener{
    public static final String LOG_TAG="WIND_APPARENT";


    private String vessel_uuid;
    private CompassMode compassMode;
    private GroundWaterType groundWaterType;

    public WindApparent(MapView mapView, FairWindModel fairWindModel, String vessel_uuid, CompassMode compassMode, GroundWaterType groundWaterType) {
        super(mapView,fairWindModel,R.drawable.yellow_arrow_300x300, 3, Color.YELLOW);


        this.vessel_uuid= FairWindModelBase.fixSelfKey(vessel_uuid);

        this.compassMode=compassMode;
        this.groundWaterType=groundWaterType;


        fairWindModel.register(
                new FairWindEvent(FairWindModelBase.getWindDirectionPath(vessel_uuid,compassMode, WindMode.APPARENT)+".*",null,0, PathEvent.EventType.ADD,this)
        );

        setTitle("Wind Apparent");
        update();
    }

    private void update() {
        //Double apparentWindDirection = fairWindModel.getWindDirection(vessel_uuid,compassMode, WindMode.APPARENT);
        Double apparentWindAngle = fairWindModel.getWindAngle(vessel_uuid,WindMode.APPARENT);
        Position position = fairWindModel.getNavPosition(vessel_uuid);

        if (position != null && apparentWindAngle!=null ) {
            apparentWindAngle=Math.toDegrees(apparentWindAngle);
            Double course=fairWindModel.getCourse(vessel_uuid,groundWaterType, CompassMode.TRUE);
            if (course==null) {
                course=fairWindModel.getCourse(vessel_uuid,groundWaterType, CompassMode.MAGNETIC);
            }
            course=Math.toDegrees(course);
            Double apparentWindDirection=course+apparentWindAngle;
            if (apparentWindDirection<0) {
                apparentWindDirection=360+apparentWindDirection;
            } else if (apparentWindDirection>360) {
                apparentWindDirection=apparentWindDirection-360;
            }



            Log.d(LOG_TAG,
                    "apparentWindAngle:"+Math.toDegrees(apparentWindAngle)+
                            " apparentWindDirection:"+apparentWindDirection);
            update(
                    position,
                    apparentWindDirection,
                    Formatter.formatAngle(Math.toRadians(apparentWindAngle), "n/a"));
        }
    }

    @Override
    public void onEvent(FairWindEvent event) {

        update();
    }
}
