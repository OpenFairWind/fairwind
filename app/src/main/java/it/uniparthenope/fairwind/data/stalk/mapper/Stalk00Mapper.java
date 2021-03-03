package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk00;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk00Mapper extends StalkSignalKAbstractMapper {
    public static final String LOG_TAG="STALK_00_MAPPER";

    public Stalk00Mapper(StalkEvent evt) {
            super(evt);
        }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk00);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk00 stalk=(Stalk00)evt.getStalk();
        if (!Double.isNaN(stalk.getDepth())) {
            Log.d(LOG_TAG,"depth:"+stalk.getDepth());
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_belowTransducer, stalk.getDepth());
        }
    }
}
