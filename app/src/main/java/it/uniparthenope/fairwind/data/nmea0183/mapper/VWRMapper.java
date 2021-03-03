package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VWRSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class VWRMapper extends SentenceSignalKAbstractMapper {

    public static final String LOG_TAG="VWRMapper";

    public VWRMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VWRSentence);
    }

    @Override
    public void map() {
        VWRSentence sen=(VWRSentence) evt.getSentence();

        Double aWA=sen.getWindAngle();
        Double aWS=sen.getSpeedKnots();

        Log.d(LOG_TAG,"aWA:"+aWA+" aWS:"+aWS);
        if (aWA.isNaN()) aWA=null; else aWA=Math.toRadians(aWA);


        if (aWS.isNaN()) aWS=null; else aWS=aWS*0.51444444444;

        put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_speedApparent,aWS);
        put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_angleApparent,aWA);


    }
}
