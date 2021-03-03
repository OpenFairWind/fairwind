package it.uniparthenope.fairwind.sdk.captain.setup.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mjson.Json;

/**
 * Created by raffaelemontella on 07/02/2017.
 */

public class MetaPreferences {
    private String path="";
    private String units="";
    private String displayName="Display Name";
    private String shortName="Short Name";
    private Method warnMethod=Method.NONE;
    private String warnMessage="";
    private Method alarmMethod=Method.NONE;
    private String alarmMessage="";
    private ArrayList<Zone> zones=new ArrayList<Zone>();

    public String getUnits() { return units; }
    public String getPath() { return path; }
    public String getDisplayName() { return displayName; }
    public String getShortName() { return shortName; }

    public Method getWarnMethod() { return warnMethod; }
    public String getWarnMessage() { return warnMessage; }

    public Method getAlarmMethod() { return alarmMethod; }
    public String getAlarmMessage() { return alarmMessage; }

    public ArrayList<Zone> getZones() { return zones; }

    public void setUnits(String units) { this.units=units;}
    public void setPath(String path) { this.path=path;}
    public void setDisplayName(String displayName) { this.displayName=displayName;}
    public void setShortName(String shortName) { this.shortName=shortName; }

    public void setWarnMethod(Method warnMethod) { this.warnMethod=warnMethod; }
    public void setWarnMessage(String warnMessage) { this.warnMessage=warnMessage; }

    public void setAlarmMethod(Method alertMethod) { this.alarmMethod=alertMethod; }
    public void setAlarmMessage(String alarmMessage) { this.alarmMessage=alarmMessage; }



    public MetaPreferences(String path, String units, String displayName, String shortName) {
        this.path=path;
        this.units=units;
        this.displayName=displayName;
        this.shortName=shortName;
        init();
    }

    public MetaPreferences() {
        init();
    }

    public MetaPreferences(Json json) {
        init();
        byJson(json);
    }

    private void init() {
    }



    public void byJson(Json json) {
        units=json.at("units","").asString().replace("\"","");
        path=json.at("path","").asString().replace("\"","");
        displayName=json.at("displayName","").asString().replace("\"","");
        shortName=json.at("shortName","").asString().replace("\"","");
        warnMethod=Method.valueOf(json.at("warnMethod","").asString().replace("\"","").toUpperCase());
        warnMessage=json.at("warnMessage","").asString().replace("\"","");
        alarmMethod=Method.valueOf(json.at("alarmMethod","").asString().replace("\"","").toUpperCase());
        alarmMessage=json.at("alarmMessage","").asString().replace("\"","");

        Json jsonZones=json.at("zones");
        if (jsonZones!=null) {
            List<Json> jsonList=jsonZones.asJsonList();
            for (Json jsonItem:jsonList) {
                Zone zone=new Zone(jsonItem);
                zones.add(zone);
            }
        }
    }

    public Json asJson() {
        Json json=Json.object();
        json.set("units",units);
        json.set("path",path);
        json.set("displayName",displayName);
        json.set("shortName",shortName);
        json.set("warnMethod",warnMethod.toString().toLowerCase());
        json.set("warnMessage",warnMessage);
        json.set("alarmMethod",alarmMethod.toString().toLowerCase());
        json.set("alarmMessage",alarmMessage);
        Json jsonZones=Json.array();
        for (Zone zone:zones) {
            jsonZones.add(zone.asJson());
        }
        json.set("zones",jsonZones);
        return json;
    }

    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }
}
