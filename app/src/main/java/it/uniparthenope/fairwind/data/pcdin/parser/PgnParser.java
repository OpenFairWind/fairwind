package it.uniparthenope.fairwind.data.pcdin.parser;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Time;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 10/06/16.
 */
public abstract class PgnParser  {
    public static final String LOG_TAG="PGN PARSER";

    private String pgnId;
    //private String data;
    private int sourceId;
    private Time time=new Time();
    private Date date=new Date();
    protected String now;
    private int offset=0;
    private short[] buffer;

    private ByteBuffer byteBuffer;



    public PgnParser(String sentence) {
        init();
        String[] parts=sentence.split(",");
        pgnId=String.format("%d",Integer.parseInt(parts[1],16));
        String data=parts[4].split("\\*")[0];
        int len=data.length()/2;
        buffer=new short[len];
        for(int i=0;i<data.length();i+=2) {
            String s=data.substring(i,i+2);
            buffer[i/2]=(short)Integer.parseInt(s,16);

        }

        sourceId=Integer.parseInt(parts[3],16) & 0xff;

        long timeStamp=Long.parseLong(parts[2],16);
        if (timeStamp>0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp);

            date.setDay(calendar.get(Calendar.DAY_OF_MONTH));
            date.setMonth(calendar.get(Calendar.MONTH));
            date.setYear(calendar.get(Calendar.YEAR));

            time.setSeconds(calendar.get(Calendar.SECOND));
            time.setMinutes(calendar.get(Calendar.MINUTE));
            time.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        }
        decode();
    }

    private void init() {
        now = Util.getIsoTimeString();

    }

    public short[] getBuffer() { return buffer; }


    public String getId() {
        return pgnId;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public int getSourceId() {
        return sourceId;
    }

    protected int get1ByteInt() {
        int x=buffer[offset] & 0xff;
        int value=( (x & 0x0F)<<4 | (x & 0xF0)>>4 );
        offset++;
        return value;
    }

    protected int get1ByteUInt() {
        return get1ByteInt() & 0xff;
    }

    protected int get2ByteInt() {
        /*
        if (buffer[offset]==0x7f && buffer[offset+1]==0xff) {
            offset+=2;
            return 0x7fff;
        }
        int value=buffer[offset]+256*buffer[offset+1];
        offset+=2;
        return value;
        */
        int result=0x7fff;
        if ((buffer[offset]==0x7f && buffer[offset+1]==0xff)==false) {
            result=(buffer[offset]+256*buffer[offset+1]);
        }
        offset+=2;
        return result;
    }

    protected int get2ByteUInt() {
        int result=0xffff;
        if ((buffer[offset]==0xff && buffer[offset+1]==0xff)==false) {
            result=(buffer[offset]+256*buffer[offset+1])& 0xffff;
        }
        offset+=2;
        return result;
    }

    protected int get4ByteInt() {
        int result=0x7fffffff;
        if ((buffer[offset]==0x7f && buffer[offset+1]==0xff && buffer[offset+2]==0xff && buffer[offset+3]==0xff)==false) {
            result = (buffer[offset + 2] + 256 * buffer[offset + 3]) * 65536 + (buffer[offset + 0] + 256 * buffer[offset + 1]);
        }
        offset+=4;
        return result;
    }

    protected int get4ByteUInt() {
        int result=0xffffffff;
        if ((buffer[offset]==0xff && buffer[offset+1]==0xff && buffer[offset+2]==0xff && buffer[offset+3]==0xff)==false) {
            result = (buffer[offset + 2] + 256 * buffer[offset + 3]) * 65536 + (buffer[offset + 0] + 256 * buffer[offset + 1]) & 0xffffffff;
        }
        offset+=4;
        return result;
    }

    protected Double get2ByteDouble(double precision) {
        Double result=null;
        int value=get2ByteInt();
        if (value!=0x7fff) {
            result=value*precision;
        }
        return result;
    }

    protected Double get2ByteUDouble(double precision) {
        Double result=null;
        int value=get2ByteUInt();
        if (value!=0xffff) {
            result=value*precision;
        }
        return result;
    }

    protected Double get4ByteDouble(double precision) {
        Double result=null;
        int value=get4ByteInt();
        if (value!=0x7fffffff) {
            result=value*precision;
        }
        return result;
    }

    protected Double get4ByteUDouble(double precision) {
        Double result=null;
        int value=get4ByteInt();
        if (value!=0xffffffff) {
            result=value*precision;
        }
        return result;
    }



    abstract public void encode();
    abstract public void decode();
    public abstract void parse(SignalKModel signalKModel, String src);
    public abstract ArrayList<Sentence> asSentences();
}
