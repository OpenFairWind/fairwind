package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.Position;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk51;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk50Mapper;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 19/08/15.
 */
public class Stalk51Parser extends StalkParser implements Stalk51 {
    public static final String LOG_TAG="STALK_51_PARSER";

    /*

    51  Z2  XX  YY  YY  LON position: XX degrees, (YYYY & 0x7FFF)/100 minutes
                                           MSB of Y = YYYY & 0x8000 = East if set, West if cleared
                                           Z= 0xA or 0x0 (reported for Raystar 120 GPS), meaning unknown
                     Stable filtered position, for raw data use command 58
                     Corresponding NMEA sentences: RMC, GAA, GLL

     */



    public Stalk51Parser() {
        super((byte)0x51,(byte)0x02);
    }
    public Stalk51Parser(String sentence) {
        super(sentence);
    }

    @Override
    public Double getLongitude() {
        Double longitude=Double.NaN;
        if (isVaild()) {
            try {
                int s=1;
                longitude=(getByte(3)+(((getByte(4)&0x7f) & 0xffff)<<8))/100.0;
                if ((getByte(4) & 0x80)==0) {
                    s=-1;
                }
                longitude=s*(longitude/60.0+getByte(2));
                Log.d(LOG_TAG,"Longitude:"+longitude);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return longitude;
    }

    @Override
    public void setLongitude(Double longitude) {
        int longitude_degrees=(int)(longitude.doubleValue());
        int longitude_minutes_int=(int)(100*(Math.abs(longitude)-Math.abs(longitude_degrees)));

        if(longitude_degrees>0)
        {
            longitude_minutes_int|=0x8000;
        }
        /*
        message=new byte[getLen()];
        message[0]=getId();
        message[1]=(byte)(getLen()-3);
        message[2]=(byte)(longitude_degrees);
        message[3]=(byte)longitude_minutes_int;
        message[4]=(byte)(longitude_minutes_int>>8);
        */
    }

}
