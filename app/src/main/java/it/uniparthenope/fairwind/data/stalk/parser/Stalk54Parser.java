package it.uniparthenope.fairwind.data.stalk.parser;


import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.Time;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk54;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk56Mapper;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public class Stalk54Parser extends StalkParser implements Stalk54 {
    public static final String LOG_TAG="STALK_54_PARSER";
    /*
    54  T1  RS  HH  GMT-time: HH hours,
                           6 MSBits of RST = minutes = (RS & 0xFC) / 4
                           6 LSBits of RST = seconds =  ST & 0x3F
                 Corresponding NMEA sentences: RMC, GAA, BWR, BWC
     */



    public Stalk54Parser() {
        super((byte)0x54,(byte)0x01);
    }
    public Stalk54Parser(String sentence) {
        super(sentence);
    }

    @Override
    public Time getTime() {
        Log.d(LOG_TAG,"getTime");
        Time time=null;
        if (isVaild()) {
            try {
                byte T=(byte) ((getByte(1) & 0xf0) >> 4);;
                byte S=(byte)(getByte(2) & 0xF);
                byte ST=(byte)((S<<4) | T );
                byte RS=getByte(2);
                byte HH=getByte(3);
                time=new Time(HH,(RS & 0xFC) / 4,ST & 0x3F);
                Stalk56Mapper.setTime(time);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return time;
    }




}
