package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 19/08/15.
 */
abstract public class StalkParser {
    public static final String LOG_TAG="STALK_PARSER";

    protected SentenceFactory sentenceFactory;
    protected String now;

    private String label;
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label=label; }

    private byte[] message;
    private byte id;
    private byte len;

    public byte getId() { return id; }
    public byte getLen() { return len; }

    private boolean vaild=false;
    public boolean isVaild() { return vaild; }

    public StalkParser(String sentence) {
        init();

        // Get the data side removing the checksum side
        sentence=sentence.split("\\*")[0].replace("$STALK,","");

        // Split the string in parts
        String[] parts=sentence.split(",");

        // Get the command id
        id=(byte)(Integer.parseInt(parts[0],16) & 0xff);

        // Get the message length
        len=(byte)((Integer.parseInt(parts[1],16) & 0x0f)+3);



        // Check if the src/dst is present
        if (parts.length==len+1) {
            label=parts[parts.length-1];
            parts=Arrays.copyOfRange(parts, 0, parts.length-1);
        }
        // Allocate the byte array
        message=new byte[len];
        for(int i=0;i<len;i++) {
            message[i]=(byte)(Integer.parseInt(parts[i],16) & 0xff);
        }

        if (message[0]==id && message.length == ((message[1] & 0x0f) + 3) && message.length ==len) {
            vaild=true;
        }
        Log.d(LOG_TAG,"sentence:"+sentence+" valid:"+vaild+" (id:"+String.format("%02X",id & 0xff)+" len:"+len+" message:"+message.length+")");
    }


    public StalkParser(byte id, byte len) {
        init();
        this.id=id;
        this.len=(byte)(len+3);
        message=new byte[this.len];
    }

    private void init() {
        now = Util.getIsoTimeString();
        sentenceFactory=SentenceFactory.getInstance();
    }

    public void fill(byte[] message) {
        this.message=new byte[message.length];
        for(int i=0;i<message.length;i++) {
            this.message[i]=message[i];
        }
        this.id=message[0];
        this.len=(byte) (message[1]&0xf);
    }

    public byte getLowNibble(int index) { return (byte)(message[index] & 0xf); }

    public byte getByte(int index) {
        return (byte)(message[index] & 0xff);
    }

    public  byte getHighNibble(int index) {
        return (byte)(message[index] >> 8);
    }

    public int get2BytesInteger(int index) {
        return (((message[index+1] & 0xff) << 8) | (message[index] & 0xff)) ;
    }
}
