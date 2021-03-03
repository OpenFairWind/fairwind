package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import org.joda.time.DateTime;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk9C;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.handler.DeclinationHandler;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk9CMapper extends StalkSignalKAbstractMapper {

    public final static String LOG_TAG="STALK_9C_MAPPER";
    public Stalk9CMapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk9C);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk9C stalk=(Stalk9C)evt.getStalk();

        if (!Double.isNaN(stalk.getCompassHeading())) {
            Double compassHeading=stalk.getCompassHeading();
            Log.d(LOG_TAG,"Compass Heading:"+compassHeading);
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingMagnetic,Math.toRadians(compassHeading));
            DeclinationHandler declinationHandler=new DeclinationHandler();
            Position position= FairWindApplication.getFairWindModel().getNavPosition("self");
            if (position!=null && position.isValid()) {
                DateTime dateTime=DateTime.parse(evt.getNow());
                Double magneticVariation = declinationHandler.handle(position.getLatitude(), position.getLongitude(), dateTime.getYear());
                if (magneticVariation != null && Double.isNaN(magneticVariation) == false) {
                    put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_headingTrue, Math.toRadians(compassHeading + magneticVariation));
                }
            }
        }
    }
}
