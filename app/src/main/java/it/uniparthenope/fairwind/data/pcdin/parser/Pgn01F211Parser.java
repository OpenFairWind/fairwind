package it.uniparthenope.fairwind.data.pcdin.parser;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F211;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 14/02/2017.
 */

public class Pgn01F211Parser extends PgnParser implements Pgn01F211 {

    private int instanceId;
    private int fluidType;
    private Double level;
    private Double capacity;

    public static Pgn01F211Parser newParser() {
        return new Pgn01F211Parser("PCDIN,01F211,00000000,70,00FF7FFFFFFFFFFF*53");
    }

    public Pgn01F211Parser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        instanceId=get1ByteInt();
        byte temp=(byte)get1ByteUInt();
        fluidType=temp & 0xf;
        level=get2ByteDouble(.004);
        capacity=get4ByteDouble(.0001);
    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {
        switch (fluidType) {
            case 0:
                if (level!=null) {
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.tanks_fuel_id_currentLevel.replace("*", String.format("%d", instanceId)), level, src, now);
                }
                if (capacity!=null) {
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.tanks_fuel_id_capacity.replace("*", String.format("%d", instanceId)), level, src, now);
                }
                break;

            case 1:
                if (level!=null) {
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.tanks_freshWater_id_currentLevel.replace("*", String.format("%d", instanceId)), level, src, now);
                }
                if (capacity!=null) {
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.tanks_freshWater_id_capacity.replace("*", String.format("%d", instanceId)), level, src, now);
                }
                break;

            case 2:
                if (level!=null) {
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.tanks_blackWater_id_currentLevel.replace("*", String.format("%d", instanceId)), level, src, now);
                }
                if (capacity!=null) {
                    signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.tanks_blackWater_id_capacity.replace("*", String.format("%d", instanceId)), level, src, now);
                }
                break;
        }

    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }

    @Override
    public int getFluidType() {
        return 0;
    }

    @Override
    public Double getLevel() {
        return null;
    }

    @Override
    public Double getCapacity() {
        return null;
    }
}
