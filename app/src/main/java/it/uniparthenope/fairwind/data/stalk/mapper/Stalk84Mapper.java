package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import org.joda.time.DateTime;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk84;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.handler.DeclinationHandler;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk84Mapper extends StalkSignalKAbstractMapper {

    public static final String LOG_TAG="STALK_84_MAPPER";
    public Stalk84Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk84);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");

        DeclinationHandler declinationHandler=new DeclinationHandler();
        Position position= FairWindApplication.getFairWindModel().getNavPosition("self");
        if (position!=null && position.isValid()) {
            DateTime dateTime = DateTime.parse(evt.getNow());
            Double magneticVariation = declinationHandler.handle(position.getLatitude(), position.getLongitude(), dateTime.getYear());
            if (magneticVariation != null && Double.isNaN(magneticVariation) == false) {
                Log.d(LOG_TAG,"magneticVariation:"+magneticVariation);
                Stalk84 stalk = (Stalk84) evt.getStalk();
                if (stalk!=null) {
                    Double compassHeading = stalk.getCompassHeading();
                    Log.d(LOG_TAG, "Compass Heading:" + compassHeading);
                    if (1 == 0 && compassHeading != null && Double.isNaN(compassHeading) == false) {
                        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic, Math.toRadians(compassHeading));
                        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue, Math.toRadians(compassHeading + magneticVariation));
                    }

                    Double autopilotCourse = stalk.getAutopilotCourse();
                    Log.d(LOG_TAG, "Autopilot target Heading:" + autopilotCourse);
                    if (!Double.isNaN(autopilotCourse)) {
                        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.steering_autopilot_target_headingMagnetic, Math.toRadians(autopilotCourse));
                        put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.steering_autopilot_target_headingTrue, Math.toRadians(autopilotCourse + magneticVariation));
                    }
                }
            }
        }

    }
}
