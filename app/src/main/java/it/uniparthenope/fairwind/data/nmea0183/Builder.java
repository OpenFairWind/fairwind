package it.uniparthenope.fairwind.data.nmea0183;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.nmea0183.SignalKSentenceAbstractBuilder;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 27/05/2017.
 */

public class Builder {

    public static final String LOG_TAG="NMEA0183 BUILDER";

    private SignalKModel signalKModel;
    private HashMap<String, Class<?>> builders;

    public Builder(SignalKModel signalKModel) {
        this.signalKModel=signalKModel;
        builders=new HashMap<String,Class<?>>();

    }

    public void register(String path, Class<?> theClass)  {
        builders.put(path,theClass);
    }

    public Vector<String> build(PathEvent pathEvent) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Vector<String> result=null;
        String prefix= SignalKConstants.vessels_dot_self_dot;
        if (pathEvent.getPath().startsWith(prefix)) {
            String path=pathEvent.getPath().replace(prefix,"");
            Class theClass=builders.get(path);
            if (theClass!=null) {
                Constructor<?> cons = theClass.getConstructor(SignalKModel.class);
                if (cons!=null) {
                    SignalKSentenceAbstractBuilder builder = (SignalKSentenceAbstractBuilder) cons.newInstance(signalKModel);
                    result = builder.build(pathEvent);
                }
            }
        }
        return result;
    }
}
