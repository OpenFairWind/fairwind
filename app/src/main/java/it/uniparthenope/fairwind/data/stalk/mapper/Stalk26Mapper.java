package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk23;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk26;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk26Mapper extends StalkSignalKAbstractMapper {
    public final static String LOG_TAG="STALK_26_MAPPER";
    public Stalk26Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk23);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk26 stalk=(Stalk26)evt.getStalk();
        if (!Double.isNaN(stalk.getSpeedThroughWater())) {
            Log.d(LOG_TAG,"Speed through water:"+stalk.getSpeedThroughWater());
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_speedThroughWater,stalk.getSpeedThroughWater()*0.514444);
        }
    }
}
