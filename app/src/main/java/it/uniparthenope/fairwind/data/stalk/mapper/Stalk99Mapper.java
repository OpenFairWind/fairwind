package it.uniparthenope.fairwind.data.stalk.mapper;

import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk99;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk99Mapper extends StalkSignalKAbstractMapper {
    public Stalk99Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk99);
    }

    @Override
    public void map() {

    }
}
