package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk50;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk51;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk51Mapper extends StalkSignalKAbstractMapper {


    public Stalk51Mapper(StalkEvent evt) {
        super(evt);
    }


    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk51);
    }

    public void map() {
        Stalk51 stalk=(Stalk51)evt.getStalk();
        Double longitude=stalk.getLongitude();
        if (longitude!=null && Double.isNaN(longitude)==false) {
            Log.d(LOG_TAG, "longitude:" + longitude);
            evt.getSignalKModel().getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position_longitude,longitude);
            evt.getSignalKModel().getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position+".timestamp",evt.getNow());
            evt.getSignalKModel().getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position+".$source",evt.getSourceRef());
        }
    }
}
