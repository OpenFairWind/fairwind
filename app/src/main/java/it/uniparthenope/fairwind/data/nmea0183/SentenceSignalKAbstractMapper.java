package it.uniparthenope.fairwind.data.nmea0183;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.util.Position;

import nz.co.fortytwo.signalk.handler.SentenceEventSource;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public abstract class SentenceSignalKAbstractMapper {

    public static final String LOG_TAG="Sen...SignalKA...Mapper";

    private SignalKModel sk;
    protected SentenceEvent evt;
    private SentenceEventSource src;

    public SentenceSignalKAbstractMapper(SentenceEvent evt) {
        Log.d(LOG_TAG,"");
        this.evt=evt;
        src = (SentenceEventSource) evt.getSource();
        sk=src.getModel();
    }


    public abstract void map();

    public void put(String key, Object value) {
        sk.put(key ,value,src.getSourceRef(), src.getNow());
    }

    public void putPosition(String destinationWaypointId, Position destinationWaypointPosition, double a) {
        sk.putPosition(SignalKConstants.resources_waypoints + dot + destinationWaypointId, destinationWaypointPosition.getLatitude(), destinationWaypointPosition.getLongitude(), a, src.getSourceRef(), src.getNow());

    }
}
