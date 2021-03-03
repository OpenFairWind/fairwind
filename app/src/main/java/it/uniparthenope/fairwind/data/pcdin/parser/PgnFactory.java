package it.uniparthenope.fairwind.data.pcdin.parser;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


/**
 * Created by raffaelemontella on 10/06/16.
 */
public class PgnFactory {
    public static final String LOG_TAG="PGNFACTORY";
    private static PgnFactory instance=null;
    private HashMap<String,Class> parsers;

    public static PgnFactory getInstance() {
        if (instance==null) {
            instance=new PgnFactory();
        }
        return instance;
    }

    public void registerParser(String pgnId, Class aClass) {
        parsers.put(pgnId,aClass);
    }
    public Class getParser(String pgnId) {
        return parsers.get(pgnId);
    }

    private PgnFactory() {
        parsers=new HashMap<String,Class>();
    }

    public static PgnParser createParser(String sSentence) {
        PgnParser pgnParser=null;
        String pgnId=sSentence.split(",")[1];
        Class pgnParserClass=PgnFactory.getInstance().getParser(pgnId);

        if (pgnParserClass!=null) {
            Class[] argClass=new Class[] { String.class };
            Object[] args=new Object[]{sSentence};
            try {
                Constructor<PgnParser> constructor=pgnParserClass.getConstructor(argClass);
                pgnParser=constructor.newInstance(args);

            } catch (NoSuchMethodException ex2) {
                Log.e(LOG_TAG,ex2.getMessage());
                throw new RuntimeException(ex2);
            } catch (InvocationTargetException ex3) {
                Log.e(LOG_TAG,ex3.getMessage());
                throw new RuntimeException(ex3);
            } catch (InstantiationException ex4) {
                Log.e(LOG_TAG,ex4.getMessage());
                throw new RuntimeException(ex4);
            } catch (IllegalAccessException ex5) {
                Log.e(LOG_TAG,ex5.getMessage());
                throw new RuntimeException(ex5);
            }
        }
        return pgnParser;

    }
}
