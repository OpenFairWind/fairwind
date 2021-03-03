package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk11;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import nz.co.fortytwo.signalk.handler.TrueWindHandler;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk11Mapper extends StalkSignalKAbstractMapper {

    public final static String LOG_TAG="STALK_11_MAPPER";
    public Stalk11Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk11);
    }

    @Override
    public void map() {
        Log.d(LOG_TAG,"map");
        Stalk11 stalk = (Stalk11) evt.getStalk();
        Double apparentWindSpeed=stalk.getApparentWindSpeed();
        if (!Double.isNaN(apparentWindSpeed)) {
            Log.d(LOG_TAG, "Apparent Wind Speed:" + apparentWindSpeed);
            apparentWindSpeed=apparentWindSpeed* 0.514444;
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_wind_speedApparent, apparentWindSpeed );
            TrueWindHandler trueWindHandler=new TrueWindHandler();
            FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();
            Double vesselSpeed =  (Double) fairWindModel.getValue(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_speedOverGround);
            Double apparentWindAngle =(Double) fairWindModel.getValue(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_angleApparent);
            if (apparentWindAngle !=null && vesselSpeed!=null) {
                // now calc and add to body
                // 0-360 from bow clockwise

                double[] windCalc = trueWindHandler.calcTrueWindDirection(apparentWindSpeed, apparentWindAngle, vesselSpeed);
                if (windCalc != null) {
                    if (!Double.isNaN(windCalc[1])) {
                        put(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.env_wind_directionTrue, windCalc[1]);
                    }
                    if (!Double.isNaN(windCalc[0])) {
                        put(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_speedTrue, windCalc[0]);
                    }
                }
            }
        }

    }
}
