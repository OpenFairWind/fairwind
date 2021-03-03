package it.uniparthenope.fairwind.data.stalk.mapper;

import net.sf.marineapi.nmea.util.Time;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk56;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk56Mapper extends StalkSignalKAbstractMapper {

    private static Time time=null;

    public Stalk56Mapper(StalkEvent evt) {
        super(evt);
    }

    public static void setTime(Time time) {
        Stalk56Mapper.time = time;
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk56);
    }

    @Override
    public void map() {

    }

}
