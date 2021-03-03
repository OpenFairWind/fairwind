package it.uniparthenope.fairwind.sdk.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by raffaelemontella on 18/09/2017.
 */

public class FairWindFragment extends Fragment {

    public void onDrawer(FairWindActivity activityBase) {

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
        public void onFragmentInteraction(Uri uri);
    }
}
