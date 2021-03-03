package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.util.Units;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class MWVMapper extends SentenceSignalKAbstractMapper {

    public static final String LOG_TAG="MWVMapper";

    public MWVMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof MWVSentence);
    }

    @Override
    public void map() {


        /*
        MWV Wind speed and angle
        This telegram provides the direction from which the wind blowsrelative to the moving vessel's centre line.
         */
        MWVSentence sen = (MWVSentence)evt.getSentence();
        Double wA=sen.getAngle();
        Double wS=sen.getSpeed();

        Log.d(LOG_TAG,"wA:"+wA+" wS:"+wS);

        Double f=1.0;

        if (wA.isNaN()) wA=null; else wA=Math.toRadians(wA);
        if (wS.isNaN()) wS=null; else wS=wS*f;

        if (sen.getSpeedUnit()== Units.KNOT) {
            f=0.51444444444;
        }

        if (sen.isTrue()) {
            put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_speedTrue,wS);
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_wind_angleTrueGround, wA);
        } else {
            put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_speedApparent,wS);
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_wind_angleApparent, wA);
        }
    }
}
