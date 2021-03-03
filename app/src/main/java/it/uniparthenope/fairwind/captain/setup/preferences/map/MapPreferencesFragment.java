package it.uniparthenope.fairwind.captain.setup.preferences.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.SimpleItemTouchHelperCallback;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapView;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;

/**
 * Created by raffaelemontella on 01/08/16.
 */
public class MapPreferencesFragment extends PreferenceFragment{
    public static final String LOG_TAG="DATALISTENER...FRAGMENT";

    private OnListFragmentInteractionListener mListener;

    private MapPreferencesContent mapPreferencesContent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MapPreferencesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MapPreferencesFragment newInstance(int columnCount) {
        MapPreferencesFragment fragment = new MapPreferencesFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        FairWindModel fairWindModel= FairWindApplication.getFairWindModel();
        Json json= FairWindMapView.getMapsConfigOverlaysAsJson(fairWindModel,getActivity());
        mapPreferencesContent =new MapPreferencesContent(json);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"onPause");
        save();
    }

    private void save() {
        Json json=Json.object();
        int order=0;
        for(MapPreferences mapPreferences: mapPreferencesContent.getItems()) {
            Json jsonMapPreference=mapPreferences.asJson();
            jsonMapPreference.set("order",order);
            json.set(mapPreferences.getUuid().toString(),jsonMapPreference);
            order++;
        }
        String jsonAsString=json.toString();
        Log.d(LOG_TAG,"Json:"+jsonAsString);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PREF_KEY_MAPS_CONFIG_OVERLAYS, jsonAsString);
        editor.commit();
    }

    private MapPreferencesRecyclerViewAdapter mapPreferencesRecyclerViewAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mappreferences_list, container, false);

        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.list_mappreferences);


        // Set the adapter
        final Context context = view.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        mapPreferencesRecyclerViewAdapter =
                new MapPreferencesRecyclerViewAdapter(this,
                        mapPreferencesContent.getItems(),
                        mListener);

        recyclerView.setAdapter(mapPreferencesRecyclerViewAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mapPreferencesRecyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


        final FloatingActionButton addButton=(FloatingActionButton)view.findViewById(R.id.add_mappreferences_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context,"Hello",Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG,"exc: "+context);

                MapPreferencesAddNewDialogFragment mapPreferencesAddNewDialogFragment=new MapPreferencesAddNewDialogFragment();
                mapPreferencesAddNewDialogFragment.setMapPreferencesFragment(MapPreferencesFragment.this);
                mapPreferencesAddNewDialogFragment.show(getFragmentManager(),"mapPreferencesAddNewDialogFragment");
            }

        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void addItem(MapPreferences mapPreferences) {
        mapPreferencesContent.addItem(mapPreferences);
        mapPreferencesRecyclerViewAdapter.notifyDataSetChanged();
        save();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MapPreferences item);
    }

}


