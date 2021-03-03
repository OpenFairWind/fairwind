package it.uniparthenope.fairwind.data.pcdin.parser;

import android.util.Log;

import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.ArrayList;

import it.uniparthenope.fairwind.data.pcdin.pgn.Pgn01F801;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 12/02/2017.
 */

public class Pgn01F801Parser extends PgnParser implements Pgn01F801 {

    public static final String LOG_TAG="PGN01F801";

    private Double latitude;
    private Double longitude;

    /*
    private boolean startLat = true;
    private boolean startLon = true;
    double previousLat = 0;
    double previousLon = 0;
    static final double ALPHA = 1 - 1.0 / 6;
    */

    public static Pgn01F801Parser newParser() {
        return new Pgn01F801Parser("$PCDIN,01F801,000C8286,00,A2C9190A2C81B603*7A");
    }

    public Pgn01F801Parser(String sentence) {
        super(sentence);
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        // Degrees = (X HB * 65536 + X LB ) * .0000001
        double latitude=get4ByteInt()* .0000001;
        double longitude=get4ByteInt()* .0000001;
        if (latitude>=-90 && latitude<=90 && longitude>=-180 && longitude<=180) {
            this.latitude=latitude;
            this.longitude=longitude;
        }
    }


    @Override
    public void parse(SignalKModel signalKModel, String src) {
        Log.d(LOG_TAG,"latitude:"+latitude+" longitude:"+longitude);
        if (longitude!=null && latitude!=null) {
            signalKModel.putPosition(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_position, latitude, longitude, 0.0, src, now);
        }
/*
        if (startLat) {
            previousLat = latitude;
            startLat = false;
        }
        previousLat = Util.movingAverage(ALPHA, previousLat, latitude);

        if (startLon) {
            previousLon = longitude;
            startLon = false;
        }
        previousLon = Util.movingAverage(ALPHA, previousLon, longitude);

        signalKModel.putPosition(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_position, previousLat, previousLon, 0.0, src, now);
*/

    }

    @Override
    public ArrayList<Sentence> asSentences() {
        return null;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
