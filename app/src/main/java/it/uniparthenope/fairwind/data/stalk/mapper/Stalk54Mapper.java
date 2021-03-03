package it.uniparthenope.fairwind.data.stalk.mapper;

import net.sf.marineapi.nmea.util.Date;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk54;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk54Mapper extends StalkSignalKAbstractMapper {

    private static Date date=null;

    public Stalk54Mapper(StalkEvent evt) {
        super(evt);
    }

    public static void setDate(Date date) {
        Stalk54Mapper.date = date;
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk54);
    }

    @Override
    public void map() {

    }
}
