package it.uniparthenope.fairwind.data.nmea0183.builder;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VTGSentence;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/05/2017.
 */

public class NavigationSpeedOverGroundBuilder extends SignalKSentenceAbstractBuilder {

    public static final String LOG_TAG="NMEA0183 SPEED BUILDER";

    public NavigationSpeedOverGroundBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double speedOverGround=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_speedOverGround+".value");

        if (speedOverGround!=null) {
            SentenceFactory sentenceFactory=SentenceFactory.getInstance();
            VTGSentence vtgSentence=(VTGSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.VTG);
            Double courseOverGroundMagnetic=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_courseOverGroundMagnetic+".value");
            if (courseOverGroundMagnetic!=null) {
                vtgSentence.setMagneticCourse(Math.toDegrees(courseOverGroundMagnetic));
            }
            vtgSentence.setSpeedKnots(speedOverGround*1.94384);
            vtgSentence.setSpeedKmh(speedOverGround*3.6);
            result.add(vtgSentence.toSentence());
        }

        return result;
    }
}
