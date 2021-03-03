package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VDRSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class VDRMapper extends SentenceSignalKAbstractMapper {
    public VDRMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VDRSentence);
    }

    @Override
    public void map() {
        VDRSentence sen = (VDRSentence) evt.getSentence();

        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_speedThroughWater , sen.getSpeed());
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue , Math.toRadians(sen.getTrueDirection()));
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic , Math.toRadians(sen.getMagneticDirection()));

    }
}
