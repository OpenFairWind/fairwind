package it.uniparthenope.fairwind.data.nmea0183.builder;

import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public class NavigationPositionBuilder extends SignalKSentenceAbstractBuilder {
    public static final String LOG_TAG="NMEA0183 POSI BUILDER";

    public NavigationPositionBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double latitude=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position_latitude);
        Double longitude=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position_longitude);
        if (latitude!=null && longitude!=null) {
            Position position = new Position(latitude, longitude);

            SentenceFactory sentenceFactory=SentenceFactory.getInstance();
            GLLSentence gllSentence=(GLLSentence)sentenceFactory.createParser(TalkerId.II,SentenceId.GLL);
            gllSentence.setPosition(position);
            gllSentence.setStatus(DataStatus.ACTIVE);
            gllSentence.setTime(getTime());

            Log.d(LOG_TAG,gllSentence.toSentence());
            result.add(gllSentence.toSentence());

            Double speed=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_speedOverGround+".value");
            Double course=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_courseOverGroundTrue+".value");

            if (speed!=null && course!=null) {
                RMCSentence rmcSentence = (RMCSentence) sentenceFactory.createParser(TalkerId.II, SentenceId.RMC);
                rmcSentence.setPosition(position);
                rmcSentence.setCourse(Math.toDegrees(course));
                rmcSentence.setSpeed(speed*1.94384);
                rmcSentence.setStatus(DataStatus.ACTIVE);

                Double variation=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_magneticVariation+".value");
                if (variation!=null) {
                    double variationDegrees=Math.toDegrees(variation);
                    if (variationDegrees<0) {
                        variationDegrees=variationDegrees+360;
                    }
                    rmcSentence.setVariation(variationDegrees);
                }

                rmcSentence.setTime(getTime());
                Log.d(LOG_TAG,rmcSentence.toSentence());
                result.add(rmcSentence.toSentence());
            }
        }
        return result;
    }
}
