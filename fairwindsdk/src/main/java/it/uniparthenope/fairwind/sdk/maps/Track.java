package it.uniparthenope.fairwind.sdk.maps;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import org.osmdroid.views.MapView;

//import org.osmdroid.bonuspack.routing.OSRMRoadManager;
//import org.osmdroid.bonuspack.routing.Road;
//import org.osmdroid.bonuspack.routing.RoadManager;
//import org.osmdroid.bonuspack.routing.RoadNode;

import it.uniparthenope.fairwind.sdk.maps.core.Polyline1;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 08/07/16.
 */
public class Track extends Polyline1 implements FairWindEventListener {
    public static final String LOG_TAG="TRACK";

    private String vessel_uuid;
    private Handler handler=new Handler();

    public Track(MapView mapView, FairWindModel fairWindModel, String vessel_uuid) {
        super(mapView,fairWindModel);
        this.vessel_uuid= FairWindModelBase.fixSelfKey(vessel_uuid);

        fairWindModel.register(
                new FairWindEvent(FairWindModelBase.getNavPositionPath(this.vessel_uuid)+".*",null,0, PathEvent.EventType.ADD,this));

        setColor(Color.BLUE);
        setTitle(fairWindModel.getBoatName());
    }

    @Override
    public void onEvent(FairWindEvent event) {
        Log.d(LOG_TAG,event.getPath());
        final Position position=fairWindModel.getNavPosition(vessel_uuid);

        if (position != null ) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    addPoint(position.asGeoPoint());
                    mapView.invalidate();
                }
            });
        }
    }
}
