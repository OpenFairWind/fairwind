package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk53;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public class Stalk53Parser extends StalkParser implements Stalk53 {
    public static final String LOG_TAG="STALK_53_PARSER";
    /*
    53  U0  VW      Magnetic Course in degrees:
                 The two lower  bits of  U * 90 +
                    the six lower  bits of VW *  2 +
                    the two higher bits of  U /  2 =
                    (U & 0x3) * 90 + (VW & 0x3F) * 2 + (U & 0xC) / 8
                 The Magnetic Course may be offset by the Compass Variation (see datagram 99) to get the Course Over Ground (COG).
                 Corresponding NMEA sentences: RMC, VTG


     */




    public Stalk53Parser() {
        super((byte)0x53,(byte)0x0);
    }
    public Stalk53Parser(String sentence) {
        super(sentence);
    }

    @Override
    public Double getMagneticCourse() {
        Log.d(LOG_TAG,"getMagneticCourse");
        Double magneticCourse=Double.NaN;
        if (isVaild()) {
            try {
                byte U = (byte) ((getByte(1) & 0xf0) >> 4);
                byte VW = (byte) (getByte(2) & 0xff);

                magneticCourse = (U & 0x3) * 90 + (VW & 0x3F) * 2 + (U & 0xC) / 8.0;
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return magneticCourse;
    }

}
