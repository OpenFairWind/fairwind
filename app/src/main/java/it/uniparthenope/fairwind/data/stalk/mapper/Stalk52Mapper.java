package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk52;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk52Mapper extends StalkSignalKAbstractMapper {
    public Stalk52Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk52);
    }


    @Override
    public void map() {
        Stalk52 stalk52=(Stalk52)evt.getStalk();
        Double sog=stalk52.getSOG();
        Log.d(LOG_TAG,"sog:"+sog);
        if (Double.isNaN(sog)==false) {
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_speedOverGround, 0.514444*sog);
        }

    }
}
