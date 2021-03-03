package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk27;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk27Parser extends StalkParser implements Stalk27 {
    public static final String LOG_TAG="STALK_27_PARSER";
    /*
     27  01  XX  XX  Water temperature: (XXXX-100)/10 deg Celsius
                 Corresponding NMEA sentence: MTW
     */
    public Stalk27Parser(String sentence) {
        super(sentence);
    }

    public Stalk27Parser() {
        super((byte)0x27, (byte)0x01);
    }

    @Override
    public Double getWaterTemperature() {
        Double waterTemperature=Double.NaN;
        if (isVaild()) {
            try {
                waterTemperature = (get2BytesInteger(3)-100) / 10.0;
                Log.d(LOG_TAG,"Water Temperature:"+waterTemperature);

            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }

        }
        return waterTemperature;
    }

}
