package it.uniparthenope.fairwind.data.nmea0183.builder;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.HDMSentence;
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

public class NavigationHeadingMagneticrBuilder extends SignalKSentenceAbstractBuilder {

    public static final String LOG_TAG="NMEA0183 HEADING BUILDER";

    public NavigationHeadingMagneticrBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double headingMagnetic=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_headingMagnetic+".value");

        if (headingMagnetic!=null) {
            SentenceFactory sentenceFactory=SentenceFactory.getInstance();
            HDMSentence hdmSentence=(HDMSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.HDM);
            hdmSentence.setHeading(Math.toDegrees(headingMagnetic));
            result.add(hdmSentence.toSentence());
        }

        return result;
    }
}
