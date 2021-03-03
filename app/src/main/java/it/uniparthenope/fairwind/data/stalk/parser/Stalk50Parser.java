package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk50;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk51Mapper;

/**
 * Created by raffaelemontella on 19/08/15.
 */
public class Stalk50Parser extends StalkParser implements Stalk50 {
    public static final String LOG_TAG="STALK_50_PARSER";

    /*

    50  Z2  XX  YY  YY  LAT position: XX degrees, (YYYY & 0x7FFF)/100 minutes
                     MSB of Y = YYYY & 0x8000 = South if set, North if cleared
                     Z= 0xA or 0x0 (reported for Raystar 120 GPS), meaning unknown
                     Stable filtered position, for raw data use command 58
                     Corresponding NMEA sentences: RMC, GAA, GLL
     */

    static final double ALPHA = 1 - 1.0 / 6;



    public Stalk50Parser() {
        super((byte) 0x50, (byte) 0x02);
    }
    public Stalk50Parser(String sentence) {
        super(sentence);
    }

    @Override
    public Double getLatitude() {
        Double latitude=Double.NaN;
        if (isVaild()) {
            try {
                int s=1;
                latitude=(getByte(3)+(((getByte(4)&0x7f) & 0xffff)<<8))/100.0;
                if ((getByte(4) & 0x80)!=0) {
                    s=-1;
                }
                // 10/6=y/x => y=x*10/6
                latitude=s*(latitude/60.0+getByte(2));
                Log.d(LOG_TAG,"Latitude:"+latitude);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return latitude;
    }

    @Override
    public void setLatitude(Double latitude) {
        int latitude_degrees=(int)(latitude.doubleValue());
        int latitude_minutes_int=(int)(100*(Math.abs(latitude)-Math.abs(latitude_degrees)));

        if(latitude_degrees<0)
        {
            latitude_minutes_int|=0x8000;
        }
        /*
        message=new byte[getLen()];
        message[0]=getId();
        message[1]=(byte)(getLen()-3);
        message[2]=(byte)(latitude_degrees);
        message[3]=(byte)latitude_minutes_int;
        message[4]=(byte)(latitude_minutes_int>>8);
        */
    }
}
