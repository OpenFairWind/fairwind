package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.nmea0183.sentence.ATDSentence;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class ATDMapper extends SentenceSignalKAbstractMapper {
    public ATDMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof ATDSentence);
    }

    @Override
    public void map() {
        ATDSentence sen=(ATDSentence) evt.getSentence();

        /*
        try {
            sk.put(vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId()+dot+source+dot+"src",sen.toSentence());
        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage());
        }
        */

        //DA IMPLEMENTARE
    }
}
