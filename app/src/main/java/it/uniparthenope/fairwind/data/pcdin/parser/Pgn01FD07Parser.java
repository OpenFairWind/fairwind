package it.uniparthenope.fairwind.data.pcdin.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01FD07;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 10/06/16.
 */
public class Pgn01FD07Parser extends PgnParser implements Pgn01FD07 {
    public static final String LOG_TAG="PGN130311";

    private int instanceId;
    private int sequenceId;
    private Double temperature=null;
    private Double humidity=null;
    private Double barometric=null;

    public static Pgn01FD07Parser newParser() {
        return new Pgn01FD07Parser("$PCDIN,01FD07,0001BF72,0F,6D4175790429DF03*57");
    }

    public Pgn01FD07Parser(String sSentence) {
        super(sSentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        sequenceId=get1ByteInt();
        instanceId=get1ByteInt();
        temperature=get2ByteDouble(0.01);
        humidity=get2ByteDouble(0.004);
        barometric=get2ByteDouble(100);
    }

    @Override
    public void parse(SignalKModel signalKModel, String src) {

        //Log.d(LOG_TAG,"i:"+instanceId+" temperature K:"+ temperature +" temperature:"+(temperature-273.15)+" humidity:"+humidity+" barometric:"+(barometric/1000)+" s:"+sequenceId);
        String pathBase=SignalKConstants.vessels_dot_self_dot;

        if (temperature!=null) {
            signalKModel.put(pathBase + SignalKConstants.env_inside_temperature, temperature, src, now);
        }
        if (humidity!=null && humidity>0 && humidity<=100) {
            signalKModel.put(pathBase + SignalKConstants.env_inside_humidity, humidity, src, now);
        }
        if (barometric!=null) {
            signalKModel.put(pathBase + SignalKConstants.env_outside_pressure, barometric, src, now);
        }
    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }

    @Override
    public int getSequenceId(){
        return sequenceId;
    }

    @Override
    public int getInstanceId(){
        return instanceId;
    }

    @Override
    public Double getAirTemp(){ return temperature; }

    @Override
    public Double getHumidity(){
        return humidity;
    }

    @Override
    public Double getBarometric(){
        return barometric;
    }

    /*

    N2kMsg.AddByte(SID);
    N2kMsg.AddByte(((HumidityInstance) & 0x03)<<6 | (TempInstance & 0x3f));
    N2kMsg.Add2ByteUDouble(Temperature,0.01);
    N2kMsg.Add2ByteDouble(Humidity,0.004);
    N2kMsg.Add2ByteUDouble(AtmosphericPressure,1);

     */

}
