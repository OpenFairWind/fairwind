package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.MTWSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class MTWMapper extends SentenceSignalKAbstractMapper {
    public MTWMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof MTWSentence);
    }

    @Override
    public void map() {

            MTWSentence sen = (MTWSentence)evt.getSentence();

            put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_water_temperature,sen.getTemperature());



    }
}
