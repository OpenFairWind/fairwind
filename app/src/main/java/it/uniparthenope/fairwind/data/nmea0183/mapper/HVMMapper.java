package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.nmea0183.sentence.HVMSentence;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class HVMMapper extends SentenceSignalKAbstractMapper {
    public HVMMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof HVMSentence);
    }

    @Override
    public void map() {
        HVMSentence sen=(HVMSentence) evt.getSentence();


        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_magneticVariation,Math.toRadians(sen.getMagneticVariation()));

    }
}
