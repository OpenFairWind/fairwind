package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk58;

/**
 * Created by raffaelemontella on 17/03/2017.
 */

public class Stalk58Mapper extends StalkSignalKAbstractMapper {
    public final static String LOG_TAG="STALK_58_MAPPER";
    public Stalk58Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk58);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk58 stalk=(Stalk58)evt.getStalk();


    }
}
