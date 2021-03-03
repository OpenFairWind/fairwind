package it.uniparthenope.fairwind.captain.setup.preferences.datalistener;

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
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DataListenerPreferencesFragment extends PreferenceFragment implements it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesFragment {
    public static final String LOG_TAG="DATALISTENER...FRAGMENT";



    // TODO: Customize parameter argument names
    //private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    //private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private DataListenerPreferencesContent dataListenerPreferencesContent;
    private DataListenerPreferencesDialog dataListenerPreferencesDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DataListenerPreferencesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DataListenerPreferencesFragment newInstance(int columnCount) {
        DataListenerPreferencesFragment fragment = new DataListenerPreferencesFragment();
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
            String defaultString=Utils.readTextFromResource(getResources(),R.raw.default_datalistenerpreferences);
            Json defaultJson=Json.read(defaultString);
            Json json=FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyJson(Constants.PREF_KEY_SERVICES_CONFIG_DATALISTENERS,defaultJson);
            dataListenerPreferencesContent=new DataListenerPreferencesContent(json,getResources());
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
        Json json=Json.object();
        for(DataListenerPreferences dataListenerPreferences:dataListenerPreferencesContent.getItems()) {
            json.set(dataListenerPreferences.getName(),dataListenerPreferences.asJson());
        }
        fairWindModel.getPreferences().setConfigPropertyJson(Constants.PREF_KEY_SERVICES_CONFIG_DATALISTENERS,json);
    }

    private DataListenerPreferencesRecyclerViewAdapter dataListenerPreferencesRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datalistenerpreferences_list, container, false);

        // Set the adapter
        Context context = view.getContext();

        dataListenerPreferencesRecyclerViewAdapter =
                new DataListenerPreferencesRecyclerViewAdapter(this,
                        dataListenerPreferencesContent.getItems(),
                        mListener);



        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.list_datalistenerpreferences);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));



        recyclerView.setAdapter(dataListenerPreferencesRecyclerViewAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(dataListenerPreferencesRecyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


        FloatingActionButton addButton=(FloatingActionButton)view.findViewById(R.id.add_datalistenerpreferences_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataListenerPreferencesAddNewDialogFragment dataListenerPreferencesAddNewDialogFragment=new DataListenerPreferencesAddNewDialogFragment();
                dataListenerPreferencesAddNewDialogFragment.setDataListenerPreferencesFragment(DataListenerPreferencesFragment.this);
                dataListenerPreferencesAddNewDialogFragment.show(getFragmentManager(),"dataListenerPreferencesAddNewDialogFragment");
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

    @Override
    public void setDataListenerPreferencesDialog(DataListenerPreferencesDialog dataListenerPreferencesDialog) {
        this.dataListenerPreferencesDialog=dataListenerPreferencesDialog;
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
        void onListFragmentInteraction(DataListenerPreferences item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (dataListenerPreferencesDialog!=null) {
            dataListenerPreferencesDialog.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addItem(DataListenerPreferences dataListenerPreferences) {
        dataListenerPreferencesContent.addItem(dataListenerPreferences);
        dataListenerPreferencesRecyclerViewAdapter.notifyDataSetChanged();
        save();
    }
}
