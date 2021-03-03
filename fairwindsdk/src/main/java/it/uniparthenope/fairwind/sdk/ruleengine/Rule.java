package it.uniparthenope.fairwind.sdk.ruleengine;

import android.content.res.Resources;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import mjson.Json;

/**
 * Created by raffaelemontella on 23/01/2017.
 */

public class Rule {
    public static final String LOG_TAG="RULE";
    private Engine engine;
    private UUID uuid= UUID.randomUUID();
    private String name;
    private String desc;
    private Json conditions;
    private Vector<Event> events=new Vector<Event>();
    private boolean enabled;

    public Rule() {

    }
    public Rule(String name, String desc) {
        this.name=name;
        this.desc=desc;
        init();
    }

    public Rule(Json json) {
        init();
        byJson(json);
        Log.d(LOG_TAG,"name:"+name);
    }

    private void init() {

    }

    public void byJson(Json json) {
        try {
            uuid = UUID.fromString(json.at("uuid", UUID.randomUUID().toString()).asString().replace("\"", ""));
        } catch (IllegalArgumentException ex) {

        }
        name=json.at("name","").asString().replace("\"","");
        desc=json.at("desc","").asString().replace("\"","");
        conditions=json.at("conditions");
        Json jsonEvents=json.at("events");
        if (jsonEvents!=null) {
            List<Json> jsonEventList=jsonEvents.asJsonList();
            for(Json jsonEventItem:jsonEventList) {
                Event event = new Event(jsonEventItem);
                events.add(event);
            }
        }
        enabled=json.at("enabled",false).asBoolean();
    }

    public Json asJson() {
        Json json=Json.object();
        json.set("uuid",uuid.toString());
        json.set("name",name);
        json.set("desc",desc);
        json.set("conditions",conditions);
        Json jsonEventList=Json.array();
        for(Event event:events) {
            jsonEventList.add(event.asJson());
        }
        json.set("events",jsonEventList);
        json.set("enabled",enabled);
        return json;
    }

    public UUID getUuid() {return uuid;}
    public void setEngine(Engine engine) {
        this.engine=engine;
    }
    public void setConditions(Json conditions) {
        this.conditions=conditions;
    }
    public Vector<Event> getEvents() { return events; }

    public String getName() { return  name; }
    public String getDesc() { return  desc; }

    public boolean isCondition(Json json) {
        Map<String,Json> map=json.asJsonMap();
        Set<String> keySet=map.keySet();
        return keySet.contains("fact") && keySet.contains("operator") && keySet.contains("value");
    }

    public Vector<Event> eval() {
        Log.d(LOG_TAG,"eval:"+name);
        if (eval(conditions)==true) {
            Log.d(LOG_TAG,"eval:"+name + " -> true");
            return events;
        } else {
            Log.d(LOG_TAG,"eval:"+name + " -> false");
            return null;
        }
    }

    public static final String OPERATOR_EQUAL="equal";
    public static final String OPERATOR_NOT_EQUAL="notEqual";
    public static final String OPERATOR_GREATER_THAN="greaterThan";
    public static final String OPERATOR_GREATER_THAN_INCLUSIVE="greaterThanInclusive";
    public static final String OPERATOR_LESS_THAN="lessThan";
    public static final String OPERATOR_LESS_THAN_INCLUSIVE="lessThanInclusive";

    private boolean eval(Json current) {
        boolean result=false;
        Log.d(LOG_TAG,"Current:"+current.toString());
        Map<String,Json> map=current.asJsonMap();
        if (isCondition(current)) {
            Log.d(LOG_TAG,"Condition");
            String key=current.at("fact").asString().replace("\"","");
            String sFact=engine.getFact(key);
            String operator=current.at("operator").asString().replace("\"","");
            Log.d(LOG_TAG,"fact:"+sFact+" operator:"+operator);
            if (sFact!=null && !sFact.isEmpty()) {
                try {

                    double fact=Double.parseDouble(sFact);
                    Log.d(LOG_TAG,fact+" is Double");
                    double value=current.at("value").asDouble();
                    double scaleRate=1;

                    if (current.at("scale")!=null) {
                        scaleRate=current.at("scale").asDouble();
                        value=value*scaleRate;
                    }

                    // Check if units change is needed
                    if (current.at("units")!=null) {
                        String units=current.at("units").asString().replace("\"","");
                        value=engine.getUnitsConvertedValue(value,key,units);
                    }

                    if (operator.equalsIgnoreCase(Rule.OPERATOR_EQUAL)) {
                        if (fact==value) return true;
                    } else if (operator.equalsIgnoreCase(Rule.OPERATOR_NOT_EQUAL)) {
                        if (fact!=value) return true;
                    } else if (operator.equalsIgnoreCase(Rule.OPERATOR_GREATER_THAN)) {
                        if (fact>value) return true;
                    } else if (operator.equalsIgnoreCase(Rule.OPERATOR_GREATER_THAN_INCLUSIVE)) {
                        if (fact>=value) return true;
                    } else if (operator.equalsIgnoreCase(Rule.OPERATOR_LESS_THAN)) {
                        if (fact<value) return true;
                    } else if (operator.equalsIgnoreCase(Rule.OPERATOR_LESS_THAN_INCLUSIVE)) {
                        if (fact<=value) return true;
                    }
                } catch (UnsupportedOperationException ex1 ) {
                    try {
                        String fact = sFact;
                        Log.d(LOG_TAG, fact + " is String");
                        String value = current.at("value").asString().replace("\"", "");
                        int r = sFact.compareTo(value);
                        if (operator.equalsIgnoreCase(Rule.OPERATOR_EQUAL)) {
                            if (r == 0) return true;
                        } else if (operator.equalsIgnoreCase(Rule.OPERATOR_NOT_EQUAL)) {
                            if (r != 0) return true;
                        } else if (operator.equalsIgnoreCase(Rule.OPERATOR_GREATER_THAN)) {
                            if (r > 0) return true;
                        } else if (operator.equalsIgnoreCase(Rule.OPERATOR_GREATER_THAN_INCLUSIVE)) {
                            if (r >= 0) return true;
                        } else if (operator.equalsIgnoreCase(Rule.OPERATOR_LESS_THAN)) {
                            if (r < 0) return true;
                        } else if (operator.equalsIgnoreCase(Rule.OPERATOR_LESS_THAN_INCLUSIVE)) {
                            if (r <= 0) return true;
                        }

                    } catch (UnsupportedOperationException ex2) {

                    }
                }
            }
        } else if (map.keySet().contains("all")) {
            Log.d(LOG_TAG,"All");
            List<Json> list=current.at("all").asJsonList();
            if (list!=null) {
                result=true;
                for(Json json:list) {
                    if (eval(json)==false) {
                        result=false;
                        break;
                    }
                }

            }
        } else if (map.keySet().contains("any")) {
            Log.d(LOG_TAG,"Any");
            List<Json> list=current.at("any").asJsonList();

            if (list!=null) {
                for(Json json:list) {
                    if (eval(json)==true) {
                        result=true;
                        break;
                    }
                }
            }
        } else {
            Log.d(LOG_TAG,"Unknown");
        }
        return result;
    }
}
