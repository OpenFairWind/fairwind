package it.uniparthenope.fairwind.captain.alarms;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import it.uniparthenope.fairwind.FairWindApplication;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 23/02/2017.
 */

public class AlarmsContent {
    public static final String LOG_TAG="AlarmsContent";

    /**
     * An array of sample (dummy) items.
     */
    private List<AlarmsItem> items = new ArrayList<AlarmsItem>();
    public List<AlarmsItem> getItems() {
        return items;
    }


    public AlarmsContent() {

    }



    public void addItem(AlarmsItem item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }


    public void sortItems() {
    }
}
