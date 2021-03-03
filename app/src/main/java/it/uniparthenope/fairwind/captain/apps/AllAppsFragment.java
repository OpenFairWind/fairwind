package it.uniparthenope.fairwind.captain.apps;

//import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.AppDetails;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllAppsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllAppsFragment extends BaseAppsFragment {



    public AllAppsFragment() {
        // Required empty public constructor
    }

    @Override
    public AppDetails getAppDetails() {
        return FairWindApplication.getFairWindModel().getAppDetails(getActivity(),AppDetails.ALL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= super.onCreateView(inflater,container,savedInstanceState);

        registerForContextMenu(v.findViewById(R.id.apps_list));

        return v;

    }




}
