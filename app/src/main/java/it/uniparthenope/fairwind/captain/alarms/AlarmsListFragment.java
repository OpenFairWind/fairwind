package it.uniparthenope.fairwind.captain.alarms;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.NavigableMap;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.State;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmsListFragment extends FairWindFragment implements Runnable{

    public static final String LOG_TAG="ALARMS_FRAGMENT";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler handler;
    private OnFragmentInteractionListener mListener;

    public static final int REFRESH_MILLS=5000;

    private View mView;

    private RecyclerView mRecyclerView;
    private AlarmsContent alarmsContent;
    private AlarmsRecyclerViewAdapter alarmsRecyclerViewAdapter;



    public AlarmsListFragment() {
        // Required empty public constructor
        handler = new Handler();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmsListFragment newInstance(String param1, String param2) {
        AlarmsListFragment fragment = new AlarmsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        alarmsContent=new AlarmsContent();
        handler.postDelayed(this, REFRESH_MILLS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_alarms, container, false);


        mRecyclerView=(RecyclerView)mView.findViewById(R.id.list_alarms);
        alarmsRecyclerViewAdapter = new AlarmsRecyclerViewAdapter(this, alarmsContent.getItems());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(alarmsRecyclerViewAdapter);


        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onListFragmentInteraction(AlarmsItem mItem) {
    }



    @Override
    public void run() {
        Log.d(LOG_TAG, "run");
        if(mView!=null) {

            if (isAdded()) {
                FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();


                String path=Util.fixSelfKey(SignalKConstants.vessels_dot_self_dot+ SignalKConstants.notifications);

                NavigableMap<String,Object> navigableMap= fairWindModel.getSubMap(path);


                HashSet<String> keys=new HashSet<String>();
                for(String key:navigableMap.keySet()) {
                    String k=key.substring(0,key.lastIndexOf(SignalKConstants.dot));
                    keys.add(k);

                }
                alarmsContent.clear();

                for (String key:keys) {
                    String stateKey=key+SignalKConstants.dot+SignalKConstants.alarmState;
                    String messageKey=key+SignalKConstants.dot+SignalKConstants.message;
                    String descriptionKey=key.replace(SignalKConstants.dot+SignalKConstants.notifications,"")+SignalKConstants.dot+SignalKConstants.meta+SignalKConstants.dot+"displayName";



                    String stateString=(String)(fairWindModel.getFullData().get(stateKey));
                    State state=State.UNKNOWN;
                    if (stateString!=null && stateString.isEmpty()==false) {
                        state=State.valueOf(stateString.replace("\"","").toUpperCase());
                    }
                    String message=(String)(fairWindModel.getFullData().get(messageKey));
                    String description=(String)(fairWindModel.getFullData().get(descriptionKey));
                    AlarmsItem alarmsItem=new AlarmsItem(key,description,state,message);
                    alarmsContent.addItem(alarmsItem);
                }


                if (alarmsRecyclerViewAdapter!=null) {
                    alarmsContent.sortItems();
                    alarmsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

        }

        // Set the next call
        handler.postDelayed(this, REFRESH_MILLS);
    }
}
