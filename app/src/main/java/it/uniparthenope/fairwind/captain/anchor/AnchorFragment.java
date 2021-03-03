package it.uniparthenope.fairwind.captain.anchor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.UpdateListener;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 19/04/16.
 */
public class AnchorFragment extends FairWindFragment implements UpdateListener, Runnable {

    public static final String LOG_TAG="ANCHOR_FRAGMENT";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mView;
    private Handler handler;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RollingRoadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnchorFragment newInstance(String param1, String param2) {
        AnchorFragment fragment = new AnchorFragment();
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
        //FairWindApplication.getFairWindModel().getUpdateListeners().add(this);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView");

        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        mView = inflater.inflate(R.layout.fragment_anchor_main, container, false);

        return mView;
    }




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

        //FairWindApplication.getFairWindModel().getUpdateListeners().remove(this);
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

    public void onUpdate(PathEvent pathEvent) throws UpdateException {
        Log.d(LOG_TAG, "update");
        if (isAdded()) {
            handler.post(this);
        }
    }


    @Override
    public void run() {
        Log.d(LOG_TAG, "run");
        if(mView!=null) {

            if (isAdded()) {
                FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
            }
        }
    }


}
