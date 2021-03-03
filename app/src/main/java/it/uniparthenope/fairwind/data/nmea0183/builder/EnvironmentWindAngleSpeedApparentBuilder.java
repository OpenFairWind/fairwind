package it.uniparthenope.fairwind.data.nmea0183.builder;

import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VWRSentence;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.Units;

import java.util.Vector;

import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public class EnvironmentWindAngleSpeedApparentBuilder extends SignalKSentenceAbstractBuilder {
    public static final String LOG_TAG="NMEA0183 WIND BUILDER";

    public EnvironmentWindAngleSpeedApparentBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double windAngleApparent=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_angleApparent+".value");
        Double windSpeedApparent=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_speedApparent+".value");
        if (windAngleApparent!=null && windSpeedApparent!=null) {
            double windAngleApparentDegrees=Math.toDegrees(windAngleApparent);

            SentenceFactory sentenceFactory= SentenceFactory.getInstance();


            VWRSentence vwrSentence=(VWRSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.VWR);

            Direction direction=Direction.RIGHT;
            if (windAngleApparentDegrees<0) {
                direction=Direction.LEFT;
            }

            vwrSentence.setDirectionLeftRight(direction);
            vwrSentence.setSpeedKmh(windSpeedApparent*3.6);
            vwrSentence.setSpeedKnots(windSpeedApparent*1.94384);
            vwrSentence.setWindAngle(Math.abs(windAngleApparentDegrees));
            Log.d(LOG_TAG,vwrSentence.toSentence());
            result.add(vwrSentence.toSentence());


            MWVSentence mwvSentence=(MWVSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.MWV);
            mwvSentence.setStatus(DataStatus.ACTIVE);
            mwvSentence.setSpeedUnit(Units.KMH);
            mwvSentence.setSpeed(windSpeedApparent*1.94384);

            if (windAngleApparentDegrees<0) {
                windAngleApparentDegrees=360+windAngleApparentDegrees;
            }

            mwvSentence.setAngle(windAngleApparentDegrees);
            mwvSentence.setTrue(false);
            Log.d(LOG_TAG,mwvSentence.toSentence());
            result.add(mwvSentence.toSentence());



        }
        return result;
    }
}
