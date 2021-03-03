package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk57;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public class Stalk57Parser extends StalkParser implements Stalk57 {
    public static final String LOG_TAG="STALK_57_PARSER";
    /*
     57  S0  DD      Sat Info: S number of sats, DD horiz. dillution of position, if S=1 -> DD=0x94
                 Corresponding NMEA sentences: GGA, GSA
     */


    public Stalk57Parser() {
        super((byte)0x57,(byte)0x00);
    }

    public Stalk57Parser(String sentence) {
        super(sentence);
    }

    @Override
    public Integer getSats() {
        return ((getByte(1) & 0xf0) >> 4);
    }

    @Override
    public Integer getHDOP() {
        return (int)getByte(2);
    }

}
