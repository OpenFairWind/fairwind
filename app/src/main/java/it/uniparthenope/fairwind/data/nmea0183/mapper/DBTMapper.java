package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.DBTSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class DBTMapper extends SentenceSignalKAbstractMapper {
    public DBTMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof DBTSentence);
    }

    @Override
    public void map() {
        DBTSentence sen=(DBTSentence) evt.getSentence();

        put(SignalKConstants.vessels_dot_self_dot + env_depth_belowTransducer , sen.getDepth());

    }
}
