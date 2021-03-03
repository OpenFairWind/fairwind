package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Zone;
import mjson.Json;

/**
 * Created by raffaelemontella on 10/02/2017.
 */

public class ZonesContent {
    public static final String LOG_TAG="ZONES...CONTENT";

    /**
     * An array of sample (dummy) items.
     */
    private List<Zone> items = new ArrayList<Zone>();

    /**\
     * A map of sample (dummy) items, by ID.
     */
    //private Map<String, Zone> item_map = new LinkedHashMap<String, Zone>();

    public void addItem(Zone item) {
        items.add(item);
        //item_map.put(item.getPath(), item);
    }

    public List<Zone> getItems() { return items; }

    public ZonesContent(ArrayList<Zone> zones){
        for (Zone zone:zones) {
            addItem(new Zone(zone));
        }
    }
}
