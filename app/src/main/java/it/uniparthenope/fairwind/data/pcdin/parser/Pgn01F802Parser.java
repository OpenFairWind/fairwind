package it.uniparthenope.fairwind.data.pcdin.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F802;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 12/02/2017.
 */

public class Pgn01F802Parser extends PgnParser implements Pgn01F802 {

    public static final String LOG_TAG="PGN01F802";

    int sequenceId;
    int reference;
    Double sog;
    Double cog;

    public Pgn01F802Parser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        sequenceId=get1ByteUInt();
        byte temp=(byte)get1ByteUInt();
        reference=((temp & 0xf0) >> 4) & 0x03;
        int reserved1=temp & 0xf;

        cog=get2ByteDouble(.0001);
        sog=get2ByteDouble(.01);
        int reserved2=get2ByteUInt();
    }

    public static Pgn01F802Parser newParser() {
        return new Pgn01F802Parser("$PCDIN,01F802,000C8286,09,3AFC8CCA0500FFFF*58");
    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {
        Log.d(LOG_TAG,"ref:"+reference+" cog:"+cog+" sog:"+sog);
        if (cog!=null) {
            switch (reference) {
                case 0:
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseOverGroundTrue, cog, src, now);
                    break;
                case 1:
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseOverGroundMagnetic, cog, src, now);
                    break;
            }
        }
        if (sog!=null) {
            if (reference >= 0 && reference <= 1) {
                signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_speedOverGround, sog, src, now);
            }
        }
    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }

    @Override
    public int getSequenceId() {
        return sequenceId;
    }

    @Override
    public int getReference() {
        return reference;
    }

    @Override
    public double getSOG() {
        return sog;
    }

    @Override
    public double getCOG() {
        return cog;
    }
}
