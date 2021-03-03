package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.ZDASentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class ZDAMapper extends SentenceSignalKAbstractMapper {
    public ZDAMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof ZDASentence);
    }

    @Override
    public void map() {
        ZDASentence sen = (ZDASentence) evt.getSentence();

        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_time_date , sen.getDate().toISO8601());
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_time_time , sen.getTime().toISO8601());

    }
}
