package it.uniparthenope.fairwind.data.nmea0183.builder;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.HDTSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public class NavigationHeadingTrueBuilder extends SignalKSentenceAbstractBuilder {
    public static final String LOG_TAG="NMEA0183 HEADING BUILDER";
    public NavigationHeadingTrueBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double headingTrue=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_headingTrue+".value");

        if (headingTrue!=null) {
            SentenceFactory sentenceFactory=SentenceFactory.getInstance();
            HDTSentence hdtSentence=(HDTSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.HDT);
            hdtSentence.setHeading(Math.toDegrees(headingTrue));
            result.add(hdtSentence.toSentence());
        }

        return result;
    }
}
