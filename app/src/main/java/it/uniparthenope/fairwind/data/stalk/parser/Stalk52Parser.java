package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk52;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public class Stalk52Parser extends StalkParser implements Stalk52 {
    public static final String LOG_TAG="STALK_52_PARSER";

    /*
    52  01  XX  XX  Speed over Ground: XXXX/10 Knots
                 Corresponding NMEA sentences: RMC, VTG
     */



    public Stalk52Parser() {
        super((byte)0x52,(byte)0x02);
    }

    public Stalk52Parser(String sentence) {
        super(sentence);
    }

    @Override
    public Double getSOG() {
        Log.d(LOG_TAG,"getSOG");
        Double speedOverGround=Double.NaN;
        if (isVaild()) {
            try {
                //speedOverGround = (double)message[2];
                //speedOverGround += ((int) (message[3] & 0x7f)) << 8;
                speedOverGround = get2BytesInteger(2)/10.0;
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }

        }
        return speedOverGround;
    }
}
