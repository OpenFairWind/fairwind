package it.uniparthenope.fairwind.sdk.captain.setup.maps;

import java.util.UUID;

import mjson.Json;

/**
 * Created by raffaelemontella on 06/08/16.
 */
public class MapPreferences {
    public static final int DEFAULT_ORDER=999;
    private boolean visible=false;
    private UUID uuid= UUID.randomUUID();
    private String name="New map";
    private String desc="This is a new map";
    private String copyright="";
    private String sourceUrl="http://";
    private int order=MapPreferences.DEFAULT_ORDER;

    private String className=getClass().getName().replace("Preferences","");

    public MapPreferences() {

    }
    public MapPreferences(String name) {

        this.name=name;
    }

    public MapPreferences(Json json) {

        byJson(json);
    }


    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public int getOrder() { return order; }
    public String getDesc() { return desc; }
    public String getCopyright() { return copyright; }
    public String getSourceUrl() { return sourceUrl; }
    public String getClassName() { return className; }
    public void setName(String name){this.name=name;}
    public void setOrder(int order) { this.order=order; }
    public void setDesc(String desc) { this.desc=desc; }
    public void setCopyright(String copyright) { this.copyright=copyright; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl=sourceUrl; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible=visible; }

    public void setClassName(String className) { this.className=className; }

    public void byJson(Json json) {
        try {
            uuid = UUID.fromString(json.at("uuid", UUID.randomUUID().toString()).asString().replace("\"", ""));
        } catch (IllegalArgumentException ex) {

        }
        this.name=json.at("name","").toString().replace("\"","");
        this.visible=json.at("visible",0).asBoolean();
        this.desc=json.at("desc","").toString().replace("\"","");
        this.order=json.at("order",MapPreferences.DEFAULT_ORDER).asInteger();
        this.copyright=json.at("copyright","").toString().replace("\"","");
        this.sourceUrl=json.at("sourceUrl","").toString().replace("\"","");
        className=json.at("className","").asString().replace("\"","");
    }

    public Json asJson() {
        Json json = Json.object();
        json.set("uuid", uuid.toString());
        json.set("visible", visible);
        json.set("name", name);
        json.set("order", order);
        json.set("visible", visible);
        json.set("desc", desc);
        json.set("copyright", copyright);
        json.set("sourceUrl", sourceUrl);
        json.set("className",className);
        return json;
    }
}
