package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VWTSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class VWTMapper extends SentenceSignalKAbstractMapper {
    public VWTMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VWTSentence);
    }

    @Override
    public void map() {
        VWTSentence sen=(VWTSentence) evt.getSentence();


        put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_speedTrue,sen.getSpeedKnots()*0.51444444444);
        put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_angleTrueGround,Math.toRadians(sen.getWindAngle()));

    }
}
