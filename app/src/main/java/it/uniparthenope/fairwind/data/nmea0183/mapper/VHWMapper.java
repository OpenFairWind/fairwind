package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VHWSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class VHWMapper extends SentenceSignalKAbstractMapper {
    /*
    VHW - Water speed and heading
        1   2 3   4 5   6 7   8 9
        |   | |   | |   | |   | |
 $--VHW,x.x,T,x.x,M,x.x,N,x.x,K*hh<CR><LF>
Field Number:

Degress True

T = True

Degrees Magnetic

M = Magnetic

Knots (speed of vessel relative to the water)

N = Knots

Kilometers (speed of vessel relative to the water)

K = Kilometers

Checksum
     */


    public static final String LOG_TAG="VHWMapper";

    public VHWMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VHWSentence);
    }

    @Override
    public void map() {
        VHWSentence sen = (VHWSentence) evt.getSentence();

        Double spd=sen.getSpeedKnots();
        Double hdt=sen.getHeading();
        Double hdm=sen.getMagneticHeading();

        Log.d(LOG_TAG,"hdt:"+hdt+" hdm:"+hdm+" spd:"+spd);
        if (hdt.isNaN()) hdt=null; else hdt=Math.toRadians(hdt);
        if (hdm.isNaN()) hdm=null; else hdm=Math.toRadians(hdm);


        if (spd.isNaN()) spd=null; else spd=spd*0.51444444444;

        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_speedThroughWater , spd);
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic , hdm);
        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue , hdt);

    }
}
