package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.nmea0183.sentence.VPWSentence;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class VPWMapper extends SentenceSignalKAbstractMapper {
    public VPWMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VPWSentence);
    }

    @Override
    public void map() {
        VPWSentence sen=(VPWSentence) evt.getSentence();
        put(SignalKConstants.vessels_dot_self_dot+"performance.velocityMadeGood",sen.getVMG());

    }
}
