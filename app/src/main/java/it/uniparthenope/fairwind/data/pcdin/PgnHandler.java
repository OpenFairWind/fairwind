package it.uniparthenope.fairwind.data.pcdin;

import android.util.Log;

import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01F211Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01F214Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01F801Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01F802Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01F805Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01FA03Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01FA04Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.Pgn01FD07Parser;
import it.uniparthenope.fairwind.data.pcdin.parser.PgnFactory;
import it.uniparthenope.fairwind.data.pcdin.parser.PgnParser;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

/**
 * Created by raffaelemontella on 10/06/16.
 */
public class PgnHandler {
    public final static String LOG_TAG="PGN_HANDLER";

    /*

$PCDIN,01F214,0001C35C,0F,0200000000FFFFB8*29
$PCDIN,01F214,0001C35F,0F,0300000000FFFFB9*2C
$PCDIN,01F214,0001C361,0F,0400000000FFFFBA*27
$PCDIN,01F214,0001C363,0F,0100000000FFFFBB*23
$PCDIN,01FD07,0001C366,0F,6E4176790429E003*53
$PCDIN,01F214,0001C74E,0F,0200000000FFFFBC*51
$PCDIN,01F214,0001C750,0F,0300000000FFFFBD*23
$PCDIN,01F214,0001C752,0F,0400000000FFFFBE*27
$PCDIN,01F214,0001C754,0F,0100000000FFFFBF*27
$PCDIN,01FD07,0001C757,0F,6F4175790429E003*55
 */



    public static boolean sniff(String sSentence) {
        boolean result=false;
        if (sSentence.startsWith("$PCDIN")) {
            result=true;
        }
        return result;
    }

    public PgnHandler(){
        PgnFactory.getInstance().registerParser("01F211", Pgn01F211Parser.class);
        PgnFactory.getInstance().registerParser("01F214", Pgn01F214Parser.class);
        PgnFactory.getInstance().registerParser("01F801", Pgn01F801Parser.class);
        PgnFactory.getInstance().registerParser("01F802", Pgn01F802Parser.class);
        //PgnFactory.getInstance().registerParser("01F805", Pgn01F805Parser.class);
        //PgnFactory.getInstance().registerParser("01FA03", Pgn01FA03Parser.class);
        //PgnFactory.getInstance().registerParser("01FA04", Pgn01FA04Parser.class);
        PgnFactory.getInstance().registerParser("01FD07", Pgn01FD07Parser.class);
    }

    public SignalKModel handle(String sSentence, String src) {
        Log.d(LOG_TAG,"PGN handle: "+sSentence+" src:"+src);
        SignalKModel signalKModel=null;
        PgnParser pgnParser= PgnFactory.createParser(sSentence);
        if (pgnParser!=null) {
            signalKModel=SignalKModelFactory.getCleanInstance();
            pgnParser.parse(signalKModel,src);
        }
        Log.d(LOG_TAG,"PGN handle done");
        return signalKModel;
    }
}
