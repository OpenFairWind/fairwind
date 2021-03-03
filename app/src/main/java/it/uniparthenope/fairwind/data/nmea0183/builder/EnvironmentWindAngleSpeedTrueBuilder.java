package it.uniparthenope.fairwind.data.nmea0183.builder;

import android.util.Log;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VWTSentence;
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

public class EnvironmentWindAngleSpeedTrueBuilder extends SignalKSentenceAbstractBuilder {
    public static final String LOG_TAG="NMEA0183 WIND BUILDER";

    public EnvironmentWindAngleSpeedTrueBuilder(SignalKModel signalKModel) {
        super(signalKModel);
    }

    @Override
    public Vector<String> build(PathEvent pathEvent) {
        Vector<String> result=new Vector<String>();
        Double windAngleTrue=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_angleTrueGround+".value");
        if (windAngleTrue==null) {
            windAngleTrue=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_angleTrueWater+".value");
        }
        Double windSpeedTrue=(Double) getSignalKModel().get(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_speedTrue+".value");
        if (windAngleTrue!=null && windSpeedTrue!=null) {
            SentenceFactory sentenceFactory= SentenceFactory.getInstance();

            double windAngleTrueDegrees=Math.toDegrees(windAngleTrue);

            VWTSentence vwtSentence=(VWTSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.VWT);

            Direction direction=Direction.RIGHT;
            if (windAngleTrueDegrees<0) {
                direction=Direction.LEFT;
            }

            vwtSentence.setDirectionLeftRight(direction);
            vwtSentence.setSpeedKmh(windSpeedTrue*3.6);
            vwtSentence.setSpeedKnots(windSpeedTrue*1.94384);
            vwtSentence.setWindAngle(Math.abs(windAngleTrueDegrees));
            Log.d(LOG_TAG,vwtSentence.toSentence());
            result.add(vwtSentence.toSentence());

            MWVSentence mwvSentence=(MWVSentence)sentenceFactory.createParser(TalkerId.II, SentenceId.MWV);
            mwvSentence.setStatus(DataStatus.ACTIVE);
            mwvSentence.setSpeedUnit(Units.KNOT);
            mwvSentence.setSpeed(windSpeedTrue*1.94384);

            if (windAngleTrueDegrees<0) {
                windAngleTrueDegrees=360+windAngleTrueDegrees;
            }

            mwvSentence.setAngle(windAngleTrueDegrees);
            mwvSentence.setTrue(true);
            Log.d(LOG_TAG,mwvSentence.toSentence());
            result.add(mwvSentence.toSentence());
        }
        return result;
    }
}
