package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk27;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk27Mapper extends StalkSignalKAbstractMapper {
    public final static String LOG_TAG="STALK_27_MAPPER";
    public Stalk27Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk27);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk27 stalk=(Stalk27)evt.getStalk();
        if (!Double.isNaN(stalk.getWaterTemperature())) {
            Log.d(LOG_TAG,"Water temperature:"+stalk.getWaterTemperature());
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_water_temperature,stalk.getWaterTemperature()+273.15);
        }
    }
}
