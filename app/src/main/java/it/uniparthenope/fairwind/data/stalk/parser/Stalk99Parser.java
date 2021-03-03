package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk99;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk53Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk54Mapper;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public class Stalk99Parser extends StalkParser implements Stalk99 {
    public static final String LOG_TAG="STALK_99_PARSER";

    /*
    99  00  XX        Compass variation sent by ST40 compass instrument
                     or ST1000, ST2000, ST4000+, E-80 every 10 seconds
                     but only if the variation is set on the instrument
                     Positive XX values: Variation West, Negative XX values: Variation East
                     Examples (XX => variation): 00 => 0, 01 => -1 west, 02 => -2 west ...
                                                 FF => +1 east, FE => +2 east ...
                   Corresponding NMEA sentences: RMC, HDG
     */


    public Stalk99Parser() {
        super((byte)0x99,(byte)0x00);
    }
    public Stalk99Parser(String sentence) {
        super(sentence);
        getCompassVariation();
    }

    @Override
    public Double getCompassVariation() {
        Double compassVariation=Double.NaN;
        if (isVaild()) {
            try {
                compassVariation = -1.0 * getByte(2);

            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return compassVariation;
    }

}
