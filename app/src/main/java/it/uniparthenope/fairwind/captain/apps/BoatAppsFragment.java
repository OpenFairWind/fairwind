package it.uniparthenope.fairwind.captain.apps;

/**
 * Created by raffaelemontella on 19/09/15.
 */
import android.net.Uri;
import android.support.v4.app.Fragment;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.AppDetails;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllAppsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoatAppsFragment extends BaseAppsFragment   {



    public BoatAppsFragment() {
        // Required empty public constructor
    }

    @Override
    public AppDetails getAppDetails() {
        return FairWindApplication.getFairWindModel().getAppDetails(getActivity(),AppDetails.BOAT);
    }





}


