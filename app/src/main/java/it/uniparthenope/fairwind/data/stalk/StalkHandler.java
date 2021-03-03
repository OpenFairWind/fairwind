package it.uniparthenope.fairwind.data.stalk;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.datagram.Stalk;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk9C;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk00Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk10Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk11Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk20Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk23Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk26Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk27Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk50Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk51Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk52Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk53Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk54Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk56Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk57Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk58Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk84Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk99Mapper;
import it.uniparthenope.fairwind.data.stalk.mapper.Stalk9CMapper;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk00Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk10Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk11Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk20Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk23Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk26Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk27Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk50Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk51Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk52Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk53Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk54Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk56Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk57Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk58Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk84Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk99Parser;
import it.uniparthenope.fairwind.data.stalk.parser.Stalk9CParser;
import it.uniparthenope.fairwind.data.stalk.parser.StalkFactory;
import it.uniparthenope.fairwind.data.stalk.parser.StalkParser;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

/**
 * Created by raffaelemontella on 12/02/2017.
 */

public class StalkHandler {
    public final static String LOG_TAG="STALK_HANDLER";

    /*

// these are Seatalk message command identifiers
    public static final byte SEATALK_ID_DEPTH=0x00;
    public static final byte SEATALK_ID_BOATSPEED=0x20;
    public static final byte SEATALK_ID_COMP_RUDD_AUTO=(byte)0x84;
    public static final byte SEATALK_ID_COMP_RUDD=(byte)0x9c;
    public static final byte SEATALK_ID_VARIATION=(byte)0x99;
    public static final byte SEATALK_ID_TEMPERATURE=0x27;
    public static final byte SEATALK_ID_TRIP=0x21;
    public static final byte SEATALK_ID_LOG=0x22;
    public static final byte SEATALK_ID_TRIPLOG=0x25;
    public static final byte SEATALK_ID_APPARENT_WIND_ANGLE=0x10;
    public static final byte SEATALK_ID_APPARENT_WIND_SPEED=0x11;
    public static final byte SEATALK_ID_SOG=0x52;
    public static final byte SEATALK_ID_COG=0x53;
    public static final byte SEATALK_ID_LATITUDE=0x50;
    public static final byte SEATALK_ID_LONGITUDE=0x51;
    public static final byte SEATALK_ID_GMT=0x54;
    public static final byte SEATALK_ID_DATE=0x56;
    public static final byte SEATALK_ID_SATINFO=0x57;
    public static final byte SEATALK_ID_UNKNOWN=(byte)0xff;
 */



    public static boolean sniff(String sSentence) {
        boolean result=false;
        if (sSentence.startsWith("$STALK")) {
            result=true;
        }
        return result;
    }

    public StalkHandler(){
        StalkFactory.getInstance().registerParser("00", Stalk00Parser.class);
        StalkFactory.getInstance().registerParser("9C", Stalk9CParser.class);

        StalkFactory.getInstance().registerParser("10", Stalk10Parser.class);
        StalkFactory.getInstance().registerParser("11", Stalk11Parser.class);

        StalkFactory.getInstance().registerParser("20", Stalk20Parser.class);
        StalkFactory.getInstance().registerParser("23", Stalk23Parser.class);
        StalkFactory.getInstance().registerParser("26", Stalk26Parser.class);
        StalkFactory.getInstance().registerParser("27", Stalk27Parser.class);

        StalkFactory.getInstance().registerParser("50", Stalk50Parser.class);
        StalkFactory.getInstance().registerParser("51", Stalk51Parser.class);
        StalkFactory.getInstance().registerParser("52", Stalk52Parser.class);
        StalkFactory.getInstance().registerParser("53", Stalk53Parser.class);
        StalkFactory.getInstance().registerParser("54", Stalk54Parser.class);
        StalkFactory.getInstance().registerParser("56", Stalk56Parser.class);
        StalkFactory.getInstance().registerParser("57", Stalk57Parser.class);
        StalkFactory.getInstance().registerParser("58", Stalk58Parser.class);

        StalkFactory.getInstance().registerParser("84", Stalk84Parser.class);
        StalkFactory.getInstance().registerParser("99", Stalk99Parser.class);
    }

    public SignalKModel handle(String sSentence, String src) {
        Log.d(LOG_TAG,"STALK handle: "+sSentence+" src:"+src);
        SignalKModel signalKModel=null;
        StalkParser stalkParser= StalkFactory.createParser(sSentence);
        if (stalkParser!=null) {
            signalKModel= SignalKModelFactory.getCleanInstance();
            StalkEvent evt=new StalkEvent(signalKModel,(Stalk)stalkParser);
            evt.setSourceRef(src);
            if (Stalk00Mapper.is(evt)) new Stalk00Mapper(evt).map();
            else if (Stalk9CMapper.is(evt)) new Stalk9CMapper(evt).map();

            else if (Stalk10Mapper.is(evt)) new Stalk10Mapper(evt).map();
            else if (Stalk11Mapper.is(evt)) new Stalk11Mapper(evt).map();

            else if (Stalk20Mapper.is(evt)) new Stalk20Mapper(evt).map();
            else if (Stalk23Mapper.is(evt)) new Stalk23Mapper(evt).map();
            else if (Stalk26Mapper.is(evt)) new Stalk26Mapper(evt).map();
            else if (Stalk27Mapper.is(evt)) new Stalk27Mapper(evt).map();

            else if (Stalk50Mapper.is(evt)) new Stalk50Mapper(evt).map();
            else if (Stalk51Mapper.is(evt)) new Stalk51Mapper(evt).map();
            else if (Stalk52Mapper.is(evt)) new Stalk52Mapper(evt).map();
            else if (Stalk53Mapper.is(evt)) new Stalk53Mapper(evt).map();
            else if (Stalk54Mapper.is(evt)) new Stalk54Mapper(evt).map();
            else if (Stalk56Mapper.is(evt)) new Stalk56Mapper(evt).map();
            else if (Stalk57Mapper.is(evt)) new Stalk57Mapper(evt).map();
            else if (Stalk58Mapper.is(evt)) new Stalk58Mapper(evt).map();

            else if (Stalk84Mapper.is(evt)) new Stalk84Mapper(evt).map();
            else if (Stalk99Mapper.is(evt)) new Stalk99Mapper(evt).map();

        }
        Log.d(LOG_TAG,"STALK handle done");
        return signalKModel;
    }
}
