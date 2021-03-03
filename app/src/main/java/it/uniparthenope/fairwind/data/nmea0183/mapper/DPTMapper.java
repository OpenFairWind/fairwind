package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.DPTSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class DPTMapper extends SentenceSignalKAbstractMapper {
    public DPTMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof DPTSentence);
    }

    @Override
    public void map() {
        DPTSentence sen=(DPTSentence) evt.getSentence();


        put(SignalKConstants.vessels_dot_self_dot + env_depth_belowTransducer , sen.getDepth());

    }
}
