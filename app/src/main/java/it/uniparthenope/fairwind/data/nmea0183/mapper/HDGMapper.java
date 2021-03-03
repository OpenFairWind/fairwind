package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class HDGMapper extends SentenceSignalKAbstractMapper {

    /*
    HDG - Heading - Deviation & Variation
        1   2   3 4   5 6
        |   |   | |   | |
 $--HDG,x.x,x.x,a,x.x,a*hh<CR><LF>
Field Number:

Magnetic Sensor heading in degrees

Magnetic Deviation, degrees

Magnetic Deviation direction, E = Easterly, W = Westerly

Magnetic Variation degrees

Magnetic Variation direction, E = Easterly, W = Westerly

Checksum
     */

    public static final String LOG_TAG="HDGMapper";

    public HDGMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof HDGSentence);
    }

    @Override
    public void map() {
        HDGSentence sen = (HDGSentence) evt.getSentence();


        Double hdg=sen.getHeading();
        Double var=sen.getVariation();
        Double dev=sen.getDeviation();

        Log.d(LOG_TAG,"hdg:"+hdg+" hdg:"+hdg+"var:"+var+" var:"+var);
        if (hdg.isNaN()) hdg=null; else hdg=Math.toRadians(hdg);
        if (var.isNaN()) var=null; else var=Math.toRadians(var);
        if (dev.isNaN()) dev=null; else dev=Math.toRadians(dev);

        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_magneticVariation,var);


        if (!Double.isNaN(sen.getHeading())) {
            if (sen.isTrue()) {
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue , hdg);
            } else {
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic , hdg);
            }
        }

    }
}
