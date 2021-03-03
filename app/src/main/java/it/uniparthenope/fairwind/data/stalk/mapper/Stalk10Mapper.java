package it.uniparthenope.fairwind.data.stalk.mapper;

import android.util.Log;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.stalk.StalkEvent;
import it.uniparthenope.fairwind.data.stalk.StalkSignalKAbstractMapper;
import it.uniparthenope.fairwind.data.stalk.datagram.Stalk10;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import nz.co.fortytwo.signalk.handler.TrueWindHandler;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 16/03/2017.
 */

public class Stalk10Mapper extends StalkSignalKAbstractMapper {
    public final static String LOG_TAG="STALK_10_MAPPER";
    public Stalk10Mapper(StalkEvent evt) {
        super(evt);
    }

    public static boolean is(StalkEvent evt) {
        return (evt.getStalk() instanceof Stalk10);
    }

    @Override
    public void map() {
        Stalk10 stalk=(Stalk10)evt.getStalk();
        if (!Double.isNaN(stalk.getApparentWindAngle())) {
            Double apparentWindAngle=stalk.getApparentWindAngle();
            if (apparentWindAngle>180 && apparentWindAngle<=360) {
                apparentWindAngle=-(360-apparentWindAngle);
            }
            Log.d(LOG_TAG,"angleApparent:"+apparentWindAngle);
            apparentWindAngle=Math.toRadians(apparentWindAngle);
            put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_wind_angleApparent,apparentWindAngle);
            TrueWindHandler trueWindHandler=new TrueWindHandler();
            FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();
            Double vesselSpeed =  (Double) fairWindModel.getValue(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.nav_speedOverGround);
            Double apparentWindSpeed =(Double) fairWindModel.getValue(SignalKConstants.vessels_dot_self_dot+SignalKConstants.env_wind_speedApparent);
            if (apparentWindSpeed !=null && vesselSpeed!=null) {
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
