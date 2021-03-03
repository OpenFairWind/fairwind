package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk10;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk10Parser extends StalkParser implements Stalk10 {

    public static final String LOG_TAG="STALK_10_PARSER";

    /*
    10  01  XX  YY  Apparent Wind Angle: XXYY/2 degrees right of bow
                 Used for autopilots Vane Mode (WindTrim)
                 Corresponding NMEA sentence: MWV
     */

    public Stalk10Parser(String sentence) {
        super(sentence);
    }

    public Stalk10Parser() {
        super((byte)0x10, (byte)0x01);
    }

    @Override
    public Double getApparentWindAngle() {

        Double apparentWindAngle=Double.NaN;
        if (isVaild()) {
            try {
                apparentWindAngle = ((getByte(2) << 8) | getByte(3)) / 2.0;
                Log.d(LOG_TAG,"AWA:"+apparentWindAngle);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return apparentWindAngle;

    }
}
