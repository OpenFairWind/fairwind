package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk23;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk23Mapper extends StalkSignalKAbstractMapper {
    public final static String LOG_TAG="STALK_23_MAPPER";
    public Stalk23Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk23);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk23 stalk=(Stalk23)evt.getStalk();
        Log.d(LOG_TAG,"Water temperature:"+stalk.getWaterTemperature());
        if (!Double.isNaN(stalk.getWaterTemperature())) {
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_water_temperature,stalk.getWaterTemperature()+273.15);
        }
    }
}
