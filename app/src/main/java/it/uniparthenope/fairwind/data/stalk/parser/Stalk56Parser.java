package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.Date;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk56;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk54Mapper;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 15/02/2017.
 */

public class Stalk56Parser extends StalkParser implements Stalk56 {
    public static final String LOG_TAG="STALK_56_PARSER";
    /*
     56  M1  DD  YY  Date: YY year, M month, DD day in month
                 Corresponding NMEA sentence: RMC
     */



    public Stalk56Parser() {
        super((byte)0x56,(byte)0x01);
    }
    public Stalk56Parser(String sentence) {
        super(sentence);
    }


    @Override
    public Date getDate() {
        Log.d(LOG_TAG,"decode");
        Date date=null;
        if (isVaild()) {
            try {
                byte M = (byte) ((getByte(1) & 0xf0) >> 4);
                byte DD = getByte(2);
                byte YY = getByte(3);
                date=new Date(2000+YY,M,DD);
                Stalk54Mapper.setDate(date);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return date;
    }




}
