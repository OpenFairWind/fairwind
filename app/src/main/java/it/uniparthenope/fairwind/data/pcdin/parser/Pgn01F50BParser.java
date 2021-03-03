package it.uniparthenope.fairwind.data.pcdin.parser;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F50B;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 14/02/2017.
 */

public class Pgn01F50BParser extends PgnParser implements Pgn01F50B {

    private int sequenceId;
    private Double waterDepth;
    private Double tranOffset;

    public Pgn01F50BParser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        sequenceId=get1ByteInt();
        waterDepth=get4ByteDouble(.01);
        tranOffset=get2ByteDouble(.001);
    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {
        if (waterDepth!=null) {
            signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_belowTransducer, waterDepth, src, now);
        }

        if (tranOffset!=null) {
            signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_belowSurface, waterDepth+tranOffset, src, now);
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
    public Double getWaterDepth() {
        return waterDepth;
    }

    @Override
    public Double getTranOffset() {
        return tranOffset;
    }
}
