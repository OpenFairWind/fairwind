package it.uniparthenope.fairwind.data.nmea0183.builder;

import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.DBTSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public class EnvironmentDepthBelowTransducerBuilder extends SignalKSentenceAbstractBuilder {
    public static final String LOG_TAG="NMEA0183 DEPTH BUILDER";


    public EnvironmentDepthBelowTransducerBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {

        Vector<String> result=new Vector<String>();
        Double depthBelowTransducer=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_depth_belowTransducer+".value");
        if (depthBelowTransducer!=null ) {
            SentenceFactory sentenceFactory=SentenceFactory.getInstance();
            DBTSentence dbtSentence=(DBTSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.DBT);
            dbtSentence.setDepth(depthBelowTransducer);
            dbtSentence.setFeet(depthBelowTransducer*3.28084);
            dbtSentence.setFathoms(depthBelowTransducer*0.546807);
            String sSentence=dbtSentence.toSentence();

            Log.d(LOG_TAG,sSentence);
            result.add(sSentence);
        }
        return result;
    }
}
