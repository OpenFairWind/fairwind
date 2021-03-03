package it.uniparthenope.fairwind.captain.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapView;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.ui.FairWindActivity;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;
import it.uniparthenope.fairwind.sdk.ui.MapView;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;


public class MapsFragment extends FairWindFragment {

    public static final String LOG_TAG="MAPS_FRAGMENT";

    private LegendAdapter adapter;

    private View mView;

    public MapsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView");



        /*
        try {
        */
        mView = inflater.inflate(R.layout.fragment_maps, container, false);

        /*
        } catch (InflateException ex) {
            ex.printStackTrace();
            Log.e(LOG_TAG,ex.getMessage());
            mView = inflater.inflate(R.layout.fragment_maps_nomap, container, false);;
            Toast.makeText(getContext(),"Trouble with maps!",Toast.LENGTH_SHORT).show();
        }
        */

        adapter=new LegendAdapter(getActivity());

        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        Context context=FairWindApplication.getInstance().getApplicationContext();

        adapter.addSectionHeaderItem(getString(R.string.drawer_legend));

        adapter.addItemTextView(fairWindModel.getBoatName(),R.drawable.boat_200x200,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_BOAT));

        adapter.addItemTextView(getString(R.string.drawer_awa), R.drawable.yellow,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_APPARENT_WIND));

        adapter.addItemTextView(getString(R.string.drawer_twa), R.drawable.cyan,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_TRUE_WIND));

        adapter.addItemTextView(getString(R.string.drawer_wind_rose), R.drawable.rose_icon,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_WINDROSE));

        adapter.addItemTextView("Speed Label", R.drawable.speed_icon,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_SPEED));

        adapter.addItemTextView("Heading Label", R.drawable.heading_icon,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_HEADING));

        adapter.addItemTextView("Boat track", R.drawable.icon_track,
                fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_TRACK));

        adapter.addItemEditText("Predictor:");

        adapter.addSectionHeaderItem("Maps");


        Json jsonMapPreferences= FairWindMapView.getMapsConfigOverlaysAsJson(fairWindModel,context);
        Map<String, Json> jsonMapPreferencesMap=jsonMapPreferences.asJsonMap();
        Collection<Json> jsonMapPreferencesCollection= jsonMapPreferencesMap.values();
        List<Json> jsonMapPreferencesList=new ArrayList(jsonMapPreferencesCollection);

        Collections.sort(jsonMapPreferencesList, new Comparator<Json>(){
            public int compare(Json o1, Json o2){
                int i1= MapPreferences.DEFAULT_ORDER,i2=MapPreferences.DEFAULT_ORDER;
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
            try {
                if (jsonMapPreferencesItem.has("className")) {


                    Json json = jsonMapPreferencesItem.at("className", null);
                    if (json != null) {
                        String className = json.asString().replace("\"", "");
                        Log.d(LOG_TAG, "className:" + className + " json:" + json);
                        Class mapPreferencesClass = Class.forName(className + "Preferences");
                        MapPreferences mapPreferences = (MapPreferences) mapPreferencesClass.newInstance();
                        mapPreferences.byJson(jsonMapPreferencesItem);

                        //adapter.addItemTextView(mapPreferences.getName(), R.drawable.icon_layers,
                        //        mapPreferences.isVisible());
                        adapter.addItemTextView(mapPreferences);
                    }
                }
            } catch (UnsupportedOperationException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (java.lang.InstantiationException ex) {
                throw new RuntimeException(ex);
            }
        }

        return mView;

    }

    public void findVessel() {
        FairWindModel fairWindModel= Utils.getFairWindModel();
        MapView mapView=(MapView)mView.findViewById(R.id.map_view);
        if (mapView!=null) {
            mapView.findVessel();
        }

    }



    public static void onFindVessel(View view, Fragment fragment, FairWindActivity activityBase) {
        MapsFragment mapsFragment=(MapsFragment)fragment;
        mapsFragment.findVessel();
    }





/*
    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"onStart");
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");

    }
*/
    /*
    @Override
    public void onEvent(final FairWindEvent event) {
        if(mapView!=null) {

            if (isAdded()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();

                        if (eventSpeedThroughWater.isMatching(event)) {

                            if (fairWindModel.getSpeed() != null && fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_SPEED)) {
                                txt_speed.setVisibility(VISIBLE);
                                lbl_speed.setVisibility(VISIBLE);
                                txt_speed.setText(Formatter.formatSpeed(fairWindModel.getPreferences().getUnit(FairWindModel.UNIT_BOAT_SPEED),fairWindModel.getSpeed(), "n/a"));
                            } else {
                                txt_speed.setVisibility(INVISIBLE);
                                lbl_speed.setVisibility(INVISIBLE);
                            }

                            Double speed=fairWindModel.getDoubleByPath(pathSpeedThroughWater);
                            if (speed!=null) {
                                txt_speed.setText(Formatter.formatSpeed(fairWindModel.getPreferences().getUnit(FairWindModel.UNIT_BOAT_SPEED),speed, "n/a"));
                            }
                        } else if (eventHeadingTrue.isMatching(event)) {

                            if (fairWindModel.getHeading() != null && fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_MAPS_HEADING)) {
                                txt_heading.setVisibility(VISIBLE);
                                lbl_heading.setVisibility(VISIBLE);
                                txt_heading.setText(Formatter.formatDirection(fairWindModel.getHeading(), "n/a"));

                            } else {
                                txt_heading.setVisibility(INVISIBLE);
                                lbl_heading.setVisibility(INVISIBLE);
                            }

                            Double heading=fairWindModel.getDoubleByPath(pathtHeadingTrue);
                            if (heading!=null) {
                                txt_heading.setText(Formatter.formatDirection(heading, "n/a"));
                            }
                        }
                    }
                });

            }
        }

    }
    */


    private void setBooleanValue(View view, String keyString) {

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxLegend);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(view.getContext());
        boolean value=FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyBoolean(keyString);
        //EDIT BY LOVIG90 ho aggiunto la negazione al valore di value altrimenti non veniva modificato nulla..
        preferences.edit().putBoolean(keyString, !value).apply();
        checkBox.setChecked(!value);
    }

    @Override
    public void onDrawer(FairWindActivity activityBase) {
        DrawerLayout drawerLayout = (DrawerLayout) activityBase.findViewById(it.uniparthenope.fairwind.sdk.R.id.drawer_layout);
        ListView mDrawerList = (ListView) drawerLayout.findViewById(R.id.drawer);
        mDrawerList.setAdapter(adapter);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MapView mapView=(MapView)mView.findViewById(R.id.map_view);
                if (mapView!=null) {


                    //boolean booleanValue=false;
                    LegendItem legendItem = adapter.getItems().get(i);
                    if (legendItem.getUuid() == null) {
                        // Is a feature
                        switch (i) {
                            case 0:
                                break;

                            case 1:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_BOAT);
                                break;
                            case 2:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_APPARENT_WIND);
                                break;
                            case 3:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_TRUE_WIND);
                                break;

                            case 4:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_WINDROSE);
                                break;

                            case 5:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_SPEED);
                                break;

                            case 6:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_HEADING);
                                break;

                            case 7:
                                setBooleanValue(view, Constants.PREF_KEY_MAPS_TRACK);
                                break;

                            case 8:
                                break;
                        }
                        mapView.notifyLayersUpdate();

                    } else {
                        Context context = FairWindApplication.getInstance().getApplicationContext();

                        // Is a map
                        FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
                        Json jsonMapPreferences = FairWindMapView.getMapsConfigOverlaysAsJson(fairWindModel, context);
                        Map<String, Json> jsonMapPreferencesMap = jsonMapPreferences.asJsonMap();
                        String key = legendItem.getUuid().toString();
                        Json jsonMapPreferencesItem = jsonMapPreferencesMap.get(key);
                        if (jsonMapPreferencesItem != null) {

                            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxLegend);
                            boolean value = !jsonMapPreferencesItem.at("visible").asBoolean();
                            jsonMapPreferencesItem.set("visible", value);
                            checkBox.setChecked(value);
                            // Save
                            String jsonAsString = jsonMapPreferences.toString();
                            Log.d(LOG_TAG, "Json:" + jsonAsString);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.PREF_KEY_MAPS_CONFIG_OVERLAYS, jsonAsString);
                            editor.apply();

                            mapView.notifyLayersUpdate();
                        }
                    }
                }
            }
        });


        drawerLayout.openDrawer(Gravity.LEFT);
    }
}