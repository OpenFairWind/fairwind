package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk57;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk57Mapper extends StalkSignalKAbstractMapper {
    public static final String LOG_TAG="Stalk57Mapper";
    public Stalk57Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk57);
    }
    @Override
    public void map() {
        Log.d(LOG_TAG,"map");

    }
}
