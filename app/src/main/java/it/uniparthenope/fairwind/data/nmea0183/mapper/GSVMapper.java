package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.GSVSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class GSVMapper extends SentenceSignalKAbstractMapper {
    public GSVMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof GSVSentence);
    }

    @Override
    public void map() {
        GSVSentence sen = (GSVSentence) evt.getSentence();

        /*
        try {
            sk.put(vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId()+dot+source+dot+"src",sen.toSentence());
        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage());
        }
        */
    }
}
