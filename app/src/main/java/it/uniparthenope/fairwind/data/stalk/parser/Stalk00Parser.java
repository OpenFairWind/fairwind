package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.DBTSentence;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk00;
import it.uniparthenope.fairwind.data.stalk.util.Bits;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 19/08/15.
 */
public class Stalk00Parser extends StalkParser implements Stalk00 {

    public static final String LOG_TAG="STALK_00_PARSER";

    /*
    00  02  YZ  XX XX  Depth below transducer: XXXX/10 feet
                       Flags in Y: Y&8 = 8: Anchor Alarm is active
                                  Y&4 = 4: Metric display units or
                                           Fathom display units if followed by command 65
                                  Y&2 = 2: Used, unknown meaning
                      Flags in Z: Z&4 = 4: Transducer defective
                                  Z&2 = 2: Deep Alarm is active
                                  Z&1 = 1: Shallow Depth Alarm is active
                    Corresponding NMEA sentences: DPT, DBT


     */

    public Stalk00Parser(String sentence) {
        super(sentence);
    }

    public Stalk00Parser() {
        super((byte)0x00, (byte)0x02);
    }

    @Override
    public Double getDepth() {

        Double depth=Double.NaN;
        if (isVaild()) {
            try {
                if (isTrasducerDefective() == false) {
                    depth = get2BytesInteger(3) / 32.81;
                    Log.d(LOG_TAG,"getDepth:"+depth);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
        return depth;
    }

    @Override
    public boolean isAnchorAlarm() {
        try {
        if ((getHighNibble(2) & 0x8)!=1) return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean isTrasducerDefective() {
        try {
            if ((getHighNibble(2) & 0x4) != 1) return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean isDeepAlarm() {
        try {
        if ((getHighNibble(2) & 0x2)!=1) return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean isShallowDepthAlarm() {
        try {
        if ((getHighNibble(2) & 0x1)!=1) return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        return true;
    }

    @Override
    public void setDepth(Double depth) {
        int depth_int;
        /*
        message=new byte[getLen()];
        message[0]=getId();
        message[1]=(byte)(getLen()-3);
        message[2]=0x00;
        depth_int=(int)(depth*32.81f);
        message[3]=(byte)depth_int;
        message[4]=(byte)(depth_int>>8);
        */
    }

}
