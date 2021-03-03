package it.uniparthenope.fairwind.data.stalk.parser;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import it.uniparthenope.fairwind.data.pcdin.parser.PgnFactory;

/**
 * Created by raffaelemontella on 19/08/15.
 */
public class StalkFactory {

    public static final String LOG_TAG="STALKFACTORY";
    private static StalkFactory instance=null;
    private HashMap<String,Class> parsers;

    public static StalkFactory getInstance() {
        if (instance==null) {
            instance=new StalkFactory();
        }
        return instance;
    }

    public void registerParser(String stalkId, Class aClass) {
        parsers.put(stalkId,aClass);
    }
    public Class getParser(String stalkId) {
        return parsers.get(stalkId);
    }

    private StalkFactory() {
        parsers=new HashMap<String,Class>();
    }

    public static StalkParser createParser(String sSentence) {
        StalkParser stalkParser=null;
        String stalkId=sSentence.split(",")[1];
        Class stalkParserClass=StalkFactory.getInstance().getParser(stalkId);

        if (stalkParserClass!=null) {
            Class[] argClass=new Class[] { String.class };
            Object[] args=new Object[]{sSentence};
            try {
                Constructor<StalkParser> constructor=stalkParserClass.getConstructor(argClass);
                stalkParser=constructor.newInstance(args);

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
        return stalkParser;

    }
}
