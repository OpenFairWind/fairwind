package it.uniparthenope.fairwind.sdk.captain.setup.ui;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mjson.Json;

/**
 * Created by raffaelemontella on 14/09/2017.
 */

public class Screen extends ArrayList<Page> {
    public static final String LOG_TAG="SCREEN";

    private UUID uuid=UUID.randomUUID();
    private String name;
    private String desc;
    private Button home;
    private Button info;
    private Ribbon topRibbon;
    private Ribbon bottomRibbon;
    private boolean isDefault;

    private int currentPage=0;
    public void setCurrentPage(int currentPage) {
        Log.d(LOG_TAG,"setCurrentPage:"+currentPage);
        this.currentPage = currentPage;
    }
    public int getCurrentPage() { return currentPage; }


    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getDesc() { return desc; }

    public boolean isDefault() { return isDefault; }

    public Screen(Json json) {
        try {
            uuid = UUID.fromString(json.at("uuid", UUID.randomUUID().toString()).asString().replace("\"", ""));
        } catch (IllegalArgumentException ex) {

        }
        name=json.at("name","").asString().replace("\"","");
        desc=json.at("desc","").asString().replace("\"","");

        isDefault=json.at("default",false).asBoolean();

        home=new Button(json.at("home"));
        topRibbon=new Ribbon(json.at("top"));
        info=new Button(json.at("info"));
        bottomRibbon=new Ribbon(json.at("bottom"));

        List<Json> list=json.at("pages").asJsonList();
        for(Json item:list) {
            if (item.at("enabled",false).asBoolean()==true) {
                add(new Page(item));
            }
        }
    }

    public Button getHome() { return home; }
    public Button getInfo() { return info; }
    public Ribbon getTopRibbon() { return topRibbon; }
    public Ribbon getBottomRibbon() { return bottomRibbon; }


}
