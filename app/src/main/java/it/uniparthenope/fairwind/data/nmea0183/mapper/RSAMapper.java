package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.RSASentence;
import net.sf.marineapi.nmea.util.Side;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class RSAMapper extends SentenceSignalKAbstractMapper {
    public RSAMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof RSASentence);
    }

    @Override
    public void map() {
        RSASentence sen=(RSASentence) evt.getSentence();


        if (!Double.isNaN(sen.getRudderAngle(Side.STARBOARD))) {
            put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.steering_rudderAngle,Math.toRadians(sen.getRudderAngle(Side.STARBOARD)));
        }
        if (!Double.isNaN(sen.getRudderAngle(Side.PORT))) {
            put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.steering_rudderAngle,Math.toRadians(sen.getRudderAngle(Side.PORT)));
        }
    }
}
