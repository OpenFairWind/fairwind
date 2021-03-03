package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.nmea0183.sentence.HSCSentence;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class HSCMapper extends SentenceSignalKAbstractMapper {
    public HSCMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof HSCSentence);
    }
    @Override
    public void map() {
        HSCSentence sen=(HSCSentence) evt.getSentence();


        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic , Math.toRadians(sen.getMagneticDirection()));
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue , Math.toRadians(sen.getTrueDirection()));

    }
}
