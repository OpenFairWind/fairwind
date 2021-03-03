package it.uniparthenope.fairwind.sdk.captain.setup.datalistener;

import android.content.res.Resources;

import java.util.UUID;

import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class DataListenerPreferences {
    private UUID uuid=UUID.randomUUID();
    private String name="New service";
    private String desc="This is a new service";
    private int timeout=1000;
    private boolean isInput=true;
    private boolean isOutput=false;
    private boolean enabled=false;
    private String className=getClass().getName().replace("Preferences","");
    private Class implClass=null;

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public int getTimeout() { return timeout; }
    public boolean isInput() { return isInput; }
    public boolean isOutput() { return isOutput; }
    public boolean isEnabled() { return enabled; }
    public String getClassName() { return className; }
    public Class getImplClass() {
        return implClass;
    }
    public void setName(String name) { this.name=name;}
    public void setDesc(String desc) { this.desc=desc; }
    public void setTimeout(int timeout) {this.timeout=timeout; }
    public void setInput(boolean isInput) { this.isInput=isInput; }
    public void setOutput(boolean isOutput) { this.isOutput=isOutput; }
    public void setEnabled(boolean enabled) { this.enabled=enabled; }


    public DataListenerPreferences(String name, String desc) {
        this.name=name;
        this.desc=desc;
        init();
    }

    public DataListenerPreferences() {
        init();
    }

    private void init() {
        try {
            this.implClass=Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void byJson(Json json, Resources resources) {
        try {
            uuid = UUID.fromString(json.at("uuid", UUID.randomUUID().toString()).asString().replace("\"", ""));
        } catch (IllegalArgumentException ex) {

        }
        name=json.at("name","").asString().replace("\"","");
        desc=json.at("desc","").asString().replace("\"","");
        this.timeout=json.at("timeout",1000).asInteger();
        isInput=json.at("isInput",false).asBoolean();
        isOutput=json.at("isOutput",false).asBoolean();
        enabled=json.at("enabled",false).asBoolean();
        //className=json.at("className","").asString().replace("\"","");
    }

    public Json asJson() {
        Json json=Json.object();
        json.set("uuid",uuid.toString());
        json.set("name",name);
        json.set("desc",desc);
        json.set("timeout",timeout);
        json.set("isInput",isInput);
        json.set("isOutput",isOutput);
        json.set("enabled",enabled);
        json.set("className",className);
        return json;
    }
}
