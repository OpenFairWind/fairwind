package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk23;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk23Parser extends StalkParser implements Stalk23 {
    public static final String LOG_TAG="STALK_23_PARSER";
    /*
    23  Z1  XX  YY  Water temperature (ST50): XX deg Celsius, YY deg Fahrenheit
                 Flag Z&4: Sensor defective or not connected (Z=4)
                 Corresponding NMEA sentence: MTW
     */
    public Stalk23Parser(String sentence) {
        super(sentence);
    }

    public Stalk23Parser() {
        super((byte)0x23, (byte)0x01);
    }

    @Override
    public Double getWaterTemperature() {
        Double waterTemperature=Double.NaN;
        if (isVaild()) {
            try {
                waterTemperature=(double)getByte(2);
                Log.d(LOG_TAG,"Water Temperature:"+waterTemperature);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return waterTemperature;
    }
}
