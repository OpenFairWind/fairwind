package it.uniparthenope.fairwind.captain.setup.preferences.map;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 01/08/16.
 */
public class MapPreferencesContent {
    public static final String LOG_TAG="CUSTOMTILE...CONTENT";

    /**
     * An array of sample (dummy) items.
     */
    private List<MapPreferences> items = new ArrayList<MapPreferences>();

    /**\
     * A map of sample (dummy) items, by ID.
     */
    private Map<String, MapPreferences> item_map = new LinkedHashMap<String, MapPreferences>();

    public void addItem(MapPreferences item) {
        items.add(item);
        item_map.put(item.getName(), item);
    }

    public List<MapPreferences> getItems() { return items; }

    public MapPreferencesContent(Json jsonMapPreferences){
        Map<String,Json> jsonMapPreferencesMap=jsonMapPreferences.asJsonMap();
        Collection<Json> jsonMapPreferencesCollection= jsonMapPreferencesMap.values();
        List<Json> jsonMapPreferencesList=new ArrayList(jsonMapPreferencesCollection);

        Collections.sort(jsonMapPreferencesList, new Comparator<Json>(){
            public int compare(Json o1, Json o2){
                int i1=MapPreferences.DEFAULT_ORDER,i2=MapPreferences.DEFAULT_ORDER;
                try {
                    i1=o1.at("order").asInteger();
                } catch (UnsupportedOperationException ex) {
                } catch (NumberFormatException ex) {
                }

                try {
                    i2=o2.at("order").asInteger();
                } catch (UnsupportedOperationException ex) {
                } catch (NumberFormatException ex) {
                }
                return i1 - i2 ;
            }
        });

        for(Json jsonMapPreferencesItem:jsonMapPreferencesList) {
            Json jsonClassName=null;
            try {
                jsonClassName = jsonMapPreferencesItem.at("className", null);
            } catch (NullPointerException ex) {

            }
            if (jsonClassName!=null) {
                String className =jsonClassName.asString().replace("\"", "")+ "Preferences";
                try {
                    Class mapPreferencesClass = Class.forName(className );
                    MapPreferences mapPreferences = (MapPreferences) mapPreferencesClass.newInstance();
                    mapPreferences.byJson(jsonMapPreferencesItem);
                    addItem(mapPreferences);
                    Log.d(LOG_TAG, mapPreferences.getName() + ":" + mapPreferences.getDesc());
                } catch (ClassNotFoundException ex) {
                    Log.e(LOG_TAG,"Class Not Found className:"+className);
                    //throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                Log.e(LOG_TAG,"No className field");
            }

        }
    }
}
