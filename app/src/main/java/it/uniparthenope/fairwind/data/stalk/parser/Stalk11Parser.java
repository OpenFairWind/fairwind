package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk11;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk11Parser extends StalkParser implements Stalk11 {

    public static final String LOG_TAG="STALK_11_PARSER";

    /*
    11  01  XX  0Y  Apparent Wind Speed: (XX & 0x7F) + Y/10 Knots
            Units flag: XX&0x80=0    => Display value in Knots
            XX&0x80=0x80 => Display value in Meter/Second
            Corresponding NMEA sentence: MWV
                    */

    public Stalk11Parser(String sentence) {
                super(sentence);
            }

    public Stalk11Parser() {
                super((byte)0x11, (byte)0x01);
            }

    @Override
    public Double getApparentWindSpeed() {
        Double apparentWindSpeed=Double.NaN;
        if (isVaild()) {
            try {
                int XX = getByte(2);
                int Y = getLowNibble(3);
                apparentWindSpeed = (double) ((XX & 0x7F) + Y / 10.0);
                Log.d(LOG_TAG,"AWS:"+apparentWindSpeed);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return apparentWindSpeed;
    }

    @Override
    public int getDisplayUnits() {
        int result= 0;
        if (isVaild()) {
            try {
                int XX=getByte(2);
                if ((XX & 0x80) == 0x00) {
                    result = Formatter.UNIT_SPEED_KNT;
                }
                else if ((XX & 0x80) == 0x80) {
                    result = Formatter.UNIT_SPEED_MS;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return result;
    }
}
