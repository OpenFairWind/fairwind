package it.uniparthenope.fairwind.data.nmea0183.builder;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public class NavigationSpeedThroughWaterBuilder extends SignalKSentenceAbstractBuilder {

    public static final String LOG_TAG="NMEA0183 SPEED BUILDER";

    public NavigationSpeedThroughWaterBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double speedThroughWater=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_speedThroughWater+".value");
        Double headingMagnetic=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_headingMagnetic+".value");

        if (speedThroughWater!=null && headingMagnetic!=null) {
            SentenceFactory sentenceFactory=SentenceFactory.getInstance();
            VHWSentence vhwSentence=(VHWSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.VHW);
            vhwSentence.setMagneticHeading(Math.toDegrees(headingMagnetic));
            vhwSentence.setSpeedKnots(speedThroughWater*1.94384);
            vhwSentence.setSpeedKmh(speedThroughWater*3.6);
            result.add(vhwSentence.toSentence());
        }

        return result;
    }
}
