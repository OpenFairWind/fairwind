package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import org.joda.time.DateTime;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk53;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.handler.DeclinationHandler;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk53Mapper extends StalkSignalKAbstractMapper {
    public Stalk53Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk53);
    }
    @Override
    public void map() {
        Stalk53 stalk=(Stalk53)evt.getStalk();
        Double magneticCourse=stalk.getMagneticCourse();
        if (Double.isNaN(magneticCourse)==false) {
            Log.d(LOG_TAG,"magneticCourse:"+magneticCourse);
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseOverGroundMagnetic, Math.toRadians(magneticCourse));
            DeclinationHandler declinationHandler=new DeclinationHandler();
            Position position= FairWindApplication.getFairWindModel().getNavPosition("self");
            if (position!=null && position.isValid()) {
                DateTime dateTime=DateTime.parse(evt.getNow());
                Double magneticVariation = declinationHandler.handle(position.getLatitude(), position.getLongitude(), dateTime.getYear());
                if (magneticVariation != null && Double.isNaN(magneticVariation) == false) {
                    put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_courseOverGroundTrue, Math.toRadians(magneticCourse + magneticVariation));
                }
            }
        }

    }
}
