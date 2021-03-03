package it.uniparthenope.fairwind.sdk.captain.setup.ui;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;
import mjson.Json;

/**
 * Created by raffaelemontella on 13/09/2017.
 */

public class Page {

    public static final String LOG_TAG="PAGE";

    private FairWindFragment fragment=null;
    private String name;
    private String desc;
    private String className;
    private Class implClass=null;
    private boolean enabled=false;

    public String getName() { return name; }
    public String getDesc() { return desc; }
    public boolean isEnabled() { return enabled; }
    public String getClassName() { return className; }

    public Page(String name, String desc) {
        this.name=name;
        this.desc=desc;
    }

    public Page(Json json) {
        Log.d(LOG_TAG,json.toString());
        name=json.at("name","").asString().replace("\"","");
        desc=json.at("desc","").asString().replace("\"","");
        enabled=json.at("enabled",false).asBoolean();
        className=json.at("className","").asString().replace("\"","");
    }

    public FairWindFragment getFragment() {
        Log.d(LOG_TAG,"getFragment:"+name+" "+className);
        if (fragment==null) {
            try {
                this.implClass = Class.forName(className);
                fragment = (FairWindFragment)implClass.newInstance();
                Log.d(LOG_TAG,"getFragment:"+name+" [NEW] "+className);

            } catch (InstantiationException ex1) {
                throw new RuntimeException(ex1);
            } catch (IllegalAccessException ex2) {
                throw new RuntimeException(ex2);
            } catch (ClassNotFoundException ex3) {
                throw new RuntimeException(ex3);
            }
        }
        return fragment;
    }

}
