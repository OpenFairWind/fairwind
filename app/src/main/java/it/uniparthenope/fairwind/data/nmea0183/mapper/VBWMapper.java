package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VBWSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class VBWMapper extends SentenceSignalKAbstractMapper {
    public VBWMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt){
        return (evt.getSentence() instanceof VBWSentence) ;
    }

    @Override
    public void map() {
        VBWSentence sen = (VBWSentence)evt.getSentence();
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_speedThroughWater , sen.getLongWaterSpeed());
    }
}
