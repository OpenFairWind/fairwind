package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.HDTSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class HDTMapper extends SentenceSignalKAbstractMapper {
/*
    HDT - Heading - True
    Actual vessel heading in degrees true produced by any device or system producing true heading.

    1   2 3
            |   | |
    $--HDT,x.x,T*hh<CR><LF>
    Field Number:

    Heading Degrees, true

    T = True

            Checksum
    */

    public static final String LOG_TAG="HDTMapper";

    public HDTMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof HDTSentence);
    }

    @Override
    public void map() {
        HeadingSentence sen=(HeadingSentence)evt.getSentence();
        Double hdg=sen.getHeading();
        Log.d(LOG_TAG,"hdg:"+hdg+" hdg:"+hdg);
        if (hdg.isNaN()) hdg=null; else hdg=Math.toRadians(hdg);
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue , hdg);


    }
}
