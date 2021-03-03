package it.uniparthenope.fairwind.data.nmea0183.mapper;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.sentence.XDRSentence;
import net.sf.marineapi.nmea.util.Measurement;

import java.util.List;

import it.uniparthenope.fairwind.data.nmea0183.SentenceSignalKAbstractMapper;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 28/09/16.
 */

public class XDRMapper extends SentenceSignalKAbstractMapper {
    public static final String LOG_TAG="XDRMapper";

    public XDRMapper(SentenceEvent evt) {
        super(evt);
    }

    public static boolean is(SentenceEvent evt) {
        return  (evt.getSentence() instanceof XDRSentence);
    }

    @Override
    public void map() {

        XDRSentence sen = (XDRSentence) evt.getSentence();
        Log.d(LOG_TAG,"map:"+sen.toString());

        List<Measurement> measurements=sen.getMeasurements();
        Double value=null;
        for (Measurement measurement:measurements) {
            if (measurement.getName().equals("ENV_OUTAIR_T")) {
                if (measurement.getUnits().equals("C")) {
                    value=measurement.getValue()+273.15;
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_outside_temperature ,value);
            } else
            if (measurement.getName().equals("ENV_ATMOS_P")) {
                if (measurement.getUnits().equals("P")) {
                    value=measurement.getValue();
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_outside_pressure , value);
            } else
            if (measurement.getName().equals("ENV_OUTAIR_H")) {
                if (measurement.getUnits().equals("H")) {
                    value=measurement.getValue();
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_outside_humidity , value);
            } else
            if (measurement.getName().equals("ENV_IN_AIR_T")) {
                if (measurement.getUnits().equals("C")) {
                    value=measurement.getValue()+273.15;
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_inside_temperature ,value);
            } else
            if (measurement.getName().equals("ENV_IN_AIR_H")) {
                if (measurement.getUnits().equals("H")) {
                    value=measurement.getValue();
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_inside_humidity , value);
            } else
            if (measurement.getName().equals("ROLL_D")) {
                if (measurement.getUnits().equals("D")) {
                    value=Math.toRadians(measurement.getValue());
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_attitude_roll ,value);
            } else
            if (measurement.getName().equals("PITCH_D")) {
                if (measurement.getUnits().equals("D")) {
                    value=Math.toRadians(measurement.getValue());
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_attitude_pitch ,value);
            } else
            if (measurement.getName().equals("YAW_D")) {
                if (measurement.getUnits().equals("D")) {
                    value=Math.toRadians(measurement.getValue());
                }
                put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_attitude_yaw ,value);
            }
        }
    }
}
