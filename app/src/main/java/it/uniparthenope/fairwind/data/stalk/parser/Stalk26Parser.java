package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk26;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk26Parser extends StalkParser implements Stalk26 {
    public static final String LOG_TAG="STALK_26_PARSER";
    /*
    26  04  XX  XX  YY  YY DE  Speed through water:
                     XXXX/100 Knots, sensor 1, current speed, valid if D&4=4
                     YYYY/100 Knots, average speed (trip/time) if D&8=0
                              or data from sensor 2 if D&8=8
                     E&1=1: Average speed calulation stopped
                     E&2=2: Display value in MPH
                     Corresponding NMEA sentence: VHW
     */
    public Stalk26Parser(String sentence) {
        super(sentence);
    }

    public Stalk26Parser() {
        super((byte)0x26, (byte)0x04);
    }

    @Override
    public Double getSpeedThroughWater() {
        Double speedThroughWater=Double.NaN;
        if (isVaild()) {
            try {
                speedThroughWater = get2BytesInteger(2) / 100.0;
            Log.d(LOG_TAG,"STW:"+speedThroughWater);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return speedThroughWater;
    }

    @Override
    public Double getAverageSpeed() {
        Double averageSpeed=Double.NaN;
        if (isVaild()) {
            try {
                int D=(getByte(6) & 0xf0)>>4;
                if ((D & 0x08) == 0x00 ) {
                    averageSpeed = get2BytesInteger(4) / 100.0;
                    Log.d(LOG_TAG,"Average speed:"+averageSpeed);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return averageSpeed;
    }

    @Override
    public Double getSpeedThroughWater2() {
        Double speedThroughWater=Double.NaN;
        if (isVaild()) {
            try {
                int D=(getByte(6) & 0xf0)>>4;
                if ((D & 0x08) == 0x08 ) {
                    speedThroughWater = get2BytesInteger(4) / 100.0;
                    Log.d(LOG_TAG,"STW2:"+speedThroughWater);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return speedThroughWater;
    }
}
