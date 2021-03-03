package it.uniparthenope.fairwind.services.rules;

import android.util.Log;


import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.sdk.ruleengine.Engine;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import mjson.Json;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 09/12/15.
 */
public class RulesListener extends DataListener implements IDataListenerPreferences {
    private static final String LOG_TAG = "ALERTS_LISTENER";

    private Engine engine;
    private FairWindModelFactAdapter fairWindModelFactAdapter;


    private int timeout=1000;
    private boolean running=false;

    public RulesListener() {
    }

    // The constructor
    public RulesListener(String name) {
        super(name);

        init();
    }

    public RulesListener(RulesListenerPreferences prefs) {
        super(prefs.getName());
        timeout=prefs.getTimeout();
        init();
    }

    private void init() {
        Log.d(LOG_TAG,"init()");
        fairWindModelFactAdapter=new FairWindModelFactAdapter();
        Json jsonRulesPreferences = fairWindModelFactAdapter.getRulesConfigAsJson();
        if (jsonRulesPreferences!=null) {
            engine=new Engine(fairWindModelFactAdapter,jsonRulesPreferences);
        }


    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void onStart() throws DataListenerException {
        running=true;
        Log.d(LOG_TAG,"start()");
    }

    @Override
    public void onStop() {
        running=false;
        Log.d(LOG_TAG,"stop()");
    }

    @Override
    public boolean onIsAlive() {
        return running;
    }

    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {
        Log.i(LOG_TAG, "Rulez...");

        if (engine!=null) {
            engine.run().then();
        }

    }

    @Override
    public boolean mayIUpdate() {
        return true;
    }

    @Override
    public  boolean isOutput() {
        return true;
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new RulesListener((RulesListenerPreferences)prefs);
    }

    @Override
    public boolean isLicensed() {
        return true;
    }
}
