package it.uniparthenope.fairwind.handler;

import android.util.Log;

import java.util.SortedMap;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.DeltaToMapConverter;
import nz.co.fortytwo.signalk.model.SignalKModel;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public abstract class HandlerBase {

    public static final String LOG_TAG="HANLDE_BASE";

    private DeltaToMapConverter deltaToMapConverter=new DeltaToMapConverter();

    public abstract void handle(SignalKModel signalKModel);

    public void parse(Json msg) {
        try {
            FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
            Log.d(LOG_TAG,"parse:"+msg.toString());
            SignalKModel signalKObject = deltaToMapConverter.handle(msg);
            SortedMap<String, Object> sortedMap=signalKObject.getFullData();
            fairWindModel.putAll(sortedMap);
            Log.d(LOG_TAG,fairWindModel.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
