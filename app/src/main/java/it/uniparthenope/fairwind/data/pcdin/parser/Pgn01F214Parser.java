package it.uniparthenope.fairwind.data.pcdin.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F214;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 11/06/16.
 */
public class Pgn01F214Parser extends PgnParser implements Pgn01F214 {

    public static final String LOG_TAG="PGN127508";

    private int instanceId;
    private int sequenceId;
    private Double temperature;
    private Double current;
    private Double volts;

    public static Pgn01F214Parser newParser() {
        return new Pgn01F214Parser("$PCDIN,01F214,000C7E2C,02,01B0040000FFFF36*20");
    }

    public Pgn01F214Parser(String sentence) {
        super(sentence);


    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        instanceId=get1ByteInt();
        volts=get2ByteDouble(.01);
        current=get2ByteDouble(.1);
        temperature=get2ByteDouble(0.01);
        sequenceId=get1ByteInt();
    }

    @Override
    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public double getBatteryVolts() {
        return volts;
    }

    @Override
    public double getBatteryCurrent() {
        return current;
    }

    @Override
    public double getBatteryTemp() {
        return temperature;
    }

    @Override
    public int getSequenceId() {
        return sequenceId;
    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {

        Log.d(LOG_TAG,"i:"+instanceId+" volts:"+volts+" current:"+current+" temp:"+temperature+" s:"+sequenceId);
        if (volts!=null) {
            signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.electrical_batteries_id_voltage.replace("*", String.format("%d", instanceId)), volts, src, now);
        }

        if (current!=null) {
            signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.electrical_batteries_id_current.replace("*", String.format("%d", instanceId)), current, src, now);
        }

        if (temperature!=null) {
            signalKModel.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.electrical_batteries_id_temperature.replace("*", String.format("%d", instanceId)), temperature, src, now);
        }
    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }

    /*

    N2kMsg.AddByte(BatteryInstance);
    N2kMsg.Add2ByteDouble(BatteryVoltage,0.01);
    N2kMsg.Add2ByteDouble(BatteryCurrent,0.1);
    N2kMsg.Add2ByteUDouble(BatteryTemperature,0.01);
    N2kMsg.AddByte(SID);
     */
}
