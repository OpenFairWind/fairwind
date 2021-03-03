package it.uniparthenope.fairwind.captain.setup.preferences.datalistener;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.captain.AlarmDialog;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import mjson.Json;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DataListenerPreferencesContent {
    public static final String LOG_TAG="DATA_LISTENER...CONTENT";

    /**
     * An array of sample (dummy) items.
     */
    private List<DataListenerPreferences> items = new ArrayList<DataListenerPreferences>();

    /**\
     * A map of sample (dummy) items, by ID.
     */
    private Map<String, DataListenerPreferences> item_map = new LinkedHashMap<String, DataListenerPreferences>();

    public void addItem(DataListenerPreferences item) {
        items.add(item);
        item_map.put(item.getUuid().toString(), item);
    }

    public List<DataListenerPreferences> getItems() { return items; }

    public DataListenerPreferencesContent(Json jsonDataListenerPreferences, Resources resources){
        Map<String,Json> jsonDataListenerPreferencesMap=jsonDataListenerPreferences.asJsonMap();
        Collection<Json> jsonDataListenerPreferencesList= jsonDataListenerPreferencesMap.values();
        for(Json jsonDataListenerPreferencesItem:jsonDataListenerPreferencesList) {
            String className=jsonDataListenerPreferencesItem.at("className",null).asString().replace("\"","");
            if (className!=null) {
                try {
                    Class dataListenerPreferencesClass = Class.forName(className + "Preferences");
                    DataListenerPreferences dataListenerPreferences = (DataListenerPreferences) dataListenerPreferencesClass.newInstance();
                    dataListenerPreferences.byJson(jsonDataListenerPreferencesItem, resources);
                    addItem(dataListenerPreferences);
                    Log.d(LOG_TAG, dataListenerPreferences.getUuid() + ":" + dataListenerPreferences.getName());

                } catch (ClassNotFoundException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                    String message="The service plug-in "+className+" is not availabile. To avoid this message, reset the service configuration.";
                    AlarmDialog.showAlert(FairWindApplication.getInstance(), UUID.fromString("f3d621d4-ede1-11e6-bc64-92361f002671"), 30000, 15000, "Service not found!", message,null,null);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
