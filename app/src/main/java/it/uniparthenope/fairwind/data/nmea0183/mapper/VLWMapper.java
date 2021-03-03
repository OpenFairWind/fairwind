package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VLWSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class VLWMapper extends SentenceSignalKAbstractMapper {
    public VLWMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VLWSentence);
    }

    @Override
    public void map() {
        VLWSentence sen=(VLWSentence) evt.getSentence();

        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_logTrip,sen.getTrip());
        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_log,sen.getTotal());

    }
}
