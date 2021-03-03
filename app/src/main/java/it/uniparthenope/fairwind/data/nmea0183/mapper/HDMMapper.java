package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.HDMSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class HDMMapper extends SentenceSignalKAbstractMapper {

    /*

    HDM - Heading - Magnetic
Vessel heading in degrees with respect to magnetic north produced by any device or system producing magnetic heading.

        1   2 3
        |   | |
 $--HDM,x.x,M*hh<CR><LF>
Field Number:

Heading Degrees, magnetic

M = magnetic

Checksum

     */

    public static final String LOG_TAG="HDMMapper";

    public HDMMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof HDMSentence);
    }

    @Override
    public void map() {
        HeadingSentence sen=(HeadingSentence)evt.getSentence();
        Double hdg=sen.getHeading();
        Log.d(LOG_TAG,"hdg:"+hdg+" hdg:"+hdg);
        if (hdg.isNaN()) hdg=null; else hdg=Math.toRadians(hdg);
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic , hdg);
    }

}
