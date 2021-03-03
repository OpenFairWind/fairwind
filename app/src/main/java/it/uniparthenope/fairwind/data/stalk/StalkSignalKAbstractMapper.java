package it.uniparthenope.fairwind.data.stalk;

import android.util.Log;

import net.sf.marineapi.nmea.util.Position;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public abstract class StalkSignalKAbstractMapper {
    public static final String LOG_TAG="StalkSignalKA...Mapper";


    protected StalkEvent evt;


    public StalkSignalKAbstractMapper(StalkEvent evt) {
        Log.d(LOG_TAG,"");
        this.evt=evt;

    }


    public abstract void map();

    public void put(String key, Object value) {
        evt.getSignalKModel().put(key ,value,evt.getSourceRef(), evt.getNow());
    }

    public void putPosition(String destinationWaypointId, Position destinationWaypointPosition, double a) {
        evt.getSignalKModel().putPosition(SignalKConstants.resources_waypoints + SignalKConstants.dot + destinationWaypointId, destinationWaypointPosition.getLatitude(), destinationWaypointPosition.getLongitude(), a, evt.getSourceRef(), evt.getNow());

    }
    public void putPosition(Double latitude, Double longitude) {
        evt.getSignalKModel().putPosition(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_position, latitude, longitude, 0.0, evt.getSourceRef(), evt.getNow());
    }


}
