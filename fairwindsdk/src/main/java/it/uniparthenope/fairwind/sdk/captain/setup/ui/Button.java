package it.uniparthenope.fairwind.sdk.captain.setup.ui;

import android.util.Log;

import mjson.Json;

/**
 * Created by raffaelemontella on 14/09/2017.
 */

public class Button {
    public static final String LOG_TAG="BUTTON";

    private String name;
    private String desc;
    private String action;
    private String drawableName;

    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getAction() { return action; }
    public String getDrawableName() { return drawableName; }


    public Button(Json json) {
        Log.d(LOG_TAG,json.toString());
        name=json.at("name","").asString().replace("\"","");
        desc=json.at("desc","").asString().replace("\"","");
        action=json.at("action").asString().replace("\"","");
        drawableName=json.at("drawableName").asString().replace("\"","");
    }
}
