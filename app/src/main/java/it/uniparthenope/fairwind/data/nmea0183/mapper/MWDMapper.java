package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.MWDSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class MWDMapper extends SentenceSignalKAbstractMapper {

    public static final String LOG_TAG="MWDMapper";

    public MWDMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof MWDSentence);
    }

    @Override
    public void map() {


            /*
            MWD Wind direction and speed
            This telegram provides the direction from which the wind blows
            across the earth's surface, with respect to north, as well as thespeed of the wind.
             */

        MWDSentence sen = (MWDSentence)evt.getSentence();



        Double mWD=sen.getMagneticWindDirection();
        Double tWD=sen.getTrueWindDirection();
        Double sT=sen.getWindSpeed();

        Log.d(LOG_TAG,"mWD:"+mWD+" tWD:"+tWD+" sT:"+sT);

        if (mWD.isNaN()) mWD=null; else mWD=Math.toRadians(mWD);


        if (tWD.isNaN()) tWD=null; else tWD=Math.toRadians(tWD);


        if (sT.isNaN()) sT=null; else sT=sT*0.51444444444;


        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_wind_directionMagnetic,mWD);
        put(SignalKConstants.vessels_dot_self_dot +SignalKConstants.env_wind_speedTrue,sT);
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_wind_directionTrue, tWD);

    }
}
