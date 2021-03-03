package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.SimpleItemTouchHelperCallback;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.model.impl.PreferencesImpl;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.MetaPreferences;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;

/**
 * Created by raffaelemontella on 07/02/2017.
 */

public class MetaPreferencesFragment extends PreferenceFragment {
    public static final String LOG_TAG="META...FRAGMENT";



    // TODO: Customize parameter argument names
    //private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    //private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private MetaPreferencesContent metaPreferencesContent;
    private MetaPreferencesDialog metaPreferencesDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MetaPreferencesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static it.uniparthenope.fairwind.captain.setup.preferences.meta.MetaPreferencesFragment newInstance(int columnCount) {
        MetaPreferencesFragment fragment = new MetaPreferencesFragment();
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


        try {
            String defaultString= Utils.readTextFromResource(getResources(), R.raw.default_metapreferences);
            Json defaultJson=Json.read(defaultString);
            Json json= FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyJson(Constants.PREF_KEY_META_CONFIG_META,defaultJson);
            //Json json=defaultJson;
            metaPreferencesContent =new MetaPreferencesContent(json);
        } catch (IOException e) {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG,e.getMessage());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"onPause");
    }

    public void save() {
        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        PreferencesImpl preferences=(PreferencesImpl)fairWindModel.getPreferences();
        Json json=Json.object();
        for(MetaPreferences metaPreferences : metaPreferencesContent.getItems()) {
            //metaPreferences.setPath(metaPreferences.getPath()+"/meta");
            json.set(metaPreferences.getPath(), metaPreferences.asJson());
        }
        preferences.setConfigPropertyJson(Constants.PREF_KEY_META_CONFIG_META,json);
        preferences.setMeta();
    }

    private MetaPreferencesRecyclerViewAdapter metaPreferencesRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_metapreferences_list, container, false);

        // Set the adapter
        Context context = view.getContext();

        metaPreferencesRecyclerViewAdapter =
                new MetaPreferencesRecyclerViewAdapter(this,
                        metaPreferencesContent.getItems(),
                        mListener);



        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.list_metapreferences);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));



        recyclerView.setAdapter(metaPreferencesRecyclerViewAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(metaPreferencesRecyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


        FloatingActionButton addButton=(FloatingActionButton)view.findViewById(R.id.add_metapreferences_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MetaPreferences metaPreferences=new MetaPreferences();
                metaPreferencesContent.addItem(metaPreferences);
                metaPreferencesRecyclerViewAdapter.notifyDataSetChanged();
                save();
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

    public void setMetaPreferencesDialog(MetaPreferencesDialog metaPreferencesDialog) {
        this.metaPreferencesDialog = metaPreferencesDialog;
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
        void onListFragmentInteraction(MetaPreferences item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (metaPreferencesDialog !=null) {
            metaPreferencesDialog.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addItem(MetaPreferences metaPreferences) {
        metaPreferencesContent.addItem(metaPreferences);
        metaPreferencesRecyclerViewAdapter.notifyDataSetChanged();
        save();
    }
}
