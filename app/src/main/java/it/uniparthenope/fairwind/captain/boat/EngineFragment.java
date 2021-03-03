package it.uniparthenope.fairwind.captain.boat;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.ui.EngineView;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EngineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EngineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EngineFragment extends FairWindFragment {
    private static final String LOG_TAG = "GAUGE_ENGINE";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    private View mView;



    public EngineFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EngineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EngineFragment newInstance(String param1, String param2) {
        EngineFragment fragment = new EngineFragment();
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
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_boat_engine, container, false);

        FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();

        LinearLayoutCompat llcEngines= (LinearLayoutCompat) mView.findViewById(R.id.llc_engines);
        Resources r = getResources();
        int nEngines=fairWindModel.getNumberOfEngines();
        for (int nEngine=0;nEngine<nEngines;nEngine++) {
            String propulsion_id="one";
            EngineView engineView=new EngineView(getContext());
            engineView.setPropulsionId(propulsion_id);
            engineView.setVisibility(View.VISIBLE);
            llcEngines.addView(engineView);

        }

        int nTrimTabs=fairWindModel.getNumberOfTrimTabs();
        //CustomView cvTrimTab0=(CustomView) mView.findViewById(R.id.cv_trimtab0);



        return mView;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        // FairWindApplication.getFairWindModel().getUpdateListeners().remove(this);
    }

/*
    @Override
    public void update() throws UpdateException {
        Log.d(LOG_TAG, "update");
        handler.post(this);
    }

    @Override
    public void run() {
        FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();

        if(fairWindModel.getApparentWindAngle()!=null &&fairWindModel.getSpeed()!=null) {

        }
    }
*/


}
