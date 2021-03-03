package it.uniparthenope.fairwind.data.nmea0183.mapper;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.VTGSentence;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;

/**
 * Created by raffaelemontella on 29/09/16.
 */

public class VTGMapper extends SentenceSignalKAbstractMapper {
    public VTGMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return (evt.getSentence() instanceof VTGSentence);
    }

    @Override
    public void map() {
        VTGSentence sen=(VTGSentence) evt.getSentence();
        double cogMagnetic=Math.toRadians(sen.getMagneticCourse());
        double cogTrue=Math.toRadians(sen.getTrueCourse());
        double sog=sen.getSpeedKnots()*0.51;

        put(SignalKConstants.vessels_dot_self_dot +nav_courseOverGroundMagnetic,cogMagnetic);
        put(SignalKConstants.vessels_dot_self_dot +nav_courseOverGroundTrue,cogTrue);
        put(SignalKConstants.vessels_dot_self_dot +nav_speedOverGround,sog);

    }
}
