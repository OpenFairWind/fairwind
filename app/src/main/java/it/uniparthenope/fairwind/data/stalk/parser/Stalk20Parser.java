package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk20;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk20Parser extends StalkParser implements Stalk20 {
    public static final String LOG_TAG="STALK_20_PARSER";
    /*
    20  01  XX  XX  Speed through water: XXXX/10 Knots
                 Corresponding NMEA sentence: VHW
     */
    public Stalk20Parser(String sentence) {
        super(sentence);
    }

    public Stalk20Parser() {
        super((byte)0x20, (byte)0x01);
    }

    @Override
    public Double getSpeedThroughWater() {
        Double speedThroughWater=Double.NaN;
        if (isVaild()) {
            try {
                speedThroughWater = get2BytesInteger(2) / 10.0;
                Log.d(LOG_TAG,"STW:"+speedThroughWater);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return speedThroughWater;
    }
}
