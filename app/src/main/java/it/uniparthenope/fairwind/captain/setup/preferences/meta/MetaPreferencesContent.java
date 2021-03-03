package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.uniparthenope.fairwind.sdk.captain.setup.meta.MetaPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 07/02/2017.
 */

public class MetaPreferencesContent {
    public static final String LOG_TAG="META...CONTENT";

    /**
     * An array of sample (dummy) items.
     */
    private List<MetaPreferences> items = new ArrayList<MetaPreferences>();

    /**\
     * A map of sample (dummy) items, by ID.
     */
    private Map<String, MetaPreferences> item_map = new LinkedHashMap<String, MetaPreferences>();

    public void addItem(MetaPreferences item) {
        items.add(item);
        item_map.put(item.getPath(), item);
    }

    public List<MetaPreferences> getItems() { return items; }

    public MetaPreferencesContent(Json jsonMetaPreferences){
        Map<String,Json> jsonMetaPreferencesMap=jsonMetaPreferences.asJsonMap();
        Collection<Json> jsonMetaPreferencesCollection= jsonMetaPreferencesMap.values();
        List<Json> jsonMetaPreferencesList=new ArrayList(jsonMetaPreferencesCollection);
        Collections.sort(jsonMetaPreferencesList, new Comparator<Json>(){
            public int compare(Json o1, Json o2){
                String s1="",s2="";
                try {
                    s1=o1.at("displayName").asString();
                } catch (UnsupportedOperationException ex) {
                }

                try {
                    s2=o2.at("displayName").asString();
                } catch (UnsupportedOperationException ex) {
                }
                return s1.compareTo(s2) ;
            }
        });


        for(Json jsonMetaPreferencesItem:jsonMetaPreferencesList) {
            MetaPreferences metaPreferences=new MetaPreferences(jsonMetaPreferencesItem);
            addItem(metaPreferences);
            Log.d(LOG_TAG, metaPreferences.getPath()+":"+ metaPreferences.getDisplayName());
        }
    }
}
