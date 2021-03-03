package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk50;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/02/2017.
 */

public class Stalk50Mapper extends StalkSignalKAbstractMapper{



    public Stalk50Mapper(StalkEvent evt) {
        super(evt);
    }


    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk50);
    }

    public void map() {
        Stalk50 stalk=(Stalk50)evt.getStalk();
        Double latitude=stalk.getLatitude();
        if (latitude!=null && Double.isNaN(latitude)==false) {
            Log.d(LOG_TAG, "longitude:" + latitude);
            evt.getSignalKModel().getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position_latitude,latitude);
            evt.getSignalKModel().getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position+".timestamp",evt.getNow());
            evt.getSignalKModel().getFullData().put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.nav_position+".$source",evt.getSourceRef());

        }
    }
}

/*
"longitude": 23.53885,
          "latitude": 60.0844,
          "$source": "nmea0183-2.GP",
          "timestamp": "2014-03-24T00:15:42Z",
          "sentence": "GLL"
 */
