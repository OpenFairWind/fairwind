package it.uniparthenope.fairwind.sdk.ruleengine;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import mjson.Json;

/**
 * Created by raffaelemontella on 23/01/2017.
 */

/*
https://github.com/CacheControl/json-rules-engine
 */
public class Engine {
    public static final String LOG_TAG="RULE_ENGINE";
    private FactAdapter factAdapter;
    private HashMap<UUID,Rule> rules=new HashMap<UUID,Rule>();
    private Vector<Event> fired=new Vector<Event>();


    public Engine(FactAdapter factAdapter, Json jsonRulesPreferences) {
        Log.d(LOG_TAG,"Engine");
        this.factAdapter=factAdapter;
        Map<String, Json> jsonRulesPreferencesMap = jsonRulesPreferences.asJsonMap();
        Collection<Json> jsonRulesPreferencesList = jsonRulesPreferencesMap.values();
        for (Json jsonRulesPreferencesItem : jsonRulesPreferencesList) {
            Rule rule=new Rule(jsonRulesPreferencesItem);
            addRule(rule);
        }
    }

    public String getFact(String key) {
        return factAdapter.onGetFact(key);
    }

    public void addRule(Rule rule) {
        rule.setEngine(this);
        rules.put(rule.getUuid(),rule);
    }

    public double getUnitsConvertedValue(double value, String key, String srcUnits) {
            return factAdapter.onGetUnitsConvertedValue(value, key,srcUnits);
    }

    public Engine run() {
        Log.d(LOG_TAG,"run()");
        fired.clear();
        for(Rule rule:rules.values()) {

            Vector<Event> events=rule.eval();
            if (events!=null) {
                for(Event event:events) {
                    fired.add(event);
                }
            }
        }
        return this;
    }

    public void then() {
        Log.d(LOG_TAG,"then()...");
        if (fired.isEmpty()==false) {
            factAdapter.onEvents(fired);
        }Log.d(LOG_TAG,"...then()");
    }
}
