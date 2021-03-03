package it.uniparthenope.fairwind.sdk.captain.setup.ui;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.uniparthenope.fairwind.sdk.captain.setup.ui.Screen;
import mjson.Json;

/**
 * Created by raffaelemontella on 13/09/2017.
 */

public class Screens extends HashMap<UUID,Screen> {

    private Screen defaultScreen=null;
    private Screen current=null;

    public Screens(Json json) {
        Map<String,Json> jsonMap=json.asJsonMap();
        Collection<Json> jsonCollection= jsonMap.values();
        List<Json> jsonList=new ArrayList(jsonCollection);

        for(Json jsonItem:jsonList) {
            Screen screen=new Screen(jsonItem);
            if (screen.isDefault()) {
                defaultScreen=screen;
            }
            put(screen.getUuid(),screen);
        }

        current=defaultScreen;
    }
    public Screen getDefault() { return defaultScreen; }
    public Screen getCurrent() { return current; }
    public void setCurrent(Screen screen) {
        this.current=screen;
    }
    public int getCount() {
        return size();
    }
}
