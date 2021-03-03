package it.uniparthenope.fairwind.captain.apps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.AppDetail;
import it.uniparthenope.fairwind.model.impl.AppDetails;

/**
 * Created by raffaelemontella on 19/09/15.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllAppsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAppsFragment extends BaseAppsFragment {



    public HomeAppsFragment() {
        // Required empty public constructor
    }

    @Override
    public AppDetails getAppDetails() {
        return FairWindApplication.getFairWindModel().getAppDetails(getActivity(),AppDetails.HOME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= super.onCreateView(inflater,container,savedInstanceState);

        registerForContextMenu(v.findViewById(R.id.apps_list));

        return v;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //  super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        menu.setHeaderTitle(getAppDetails().get(position).getPackageName());
        menu.add(Menu.NONE, R.id.remove_item, Menu.NONE, R.string.remove);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(getUserVisibleHint()) {

            SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getContext());

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;

            AppDetails appDetails=getAppDetails();
            Collections.sort(appDetails, new Comparator<AppDetail>() {
                @Override
                public int compare(AppDetail appDetail, AppDetail t1) {
                    return appDetail.getLabel().toString().compareToIgnoreCase(t1.getLabel().toString());
                }
            });


            switch (item.getItemId()) {

                case R.id.remove_item:
                    Set<String> set=sp.getStringSet("home_apps", new HashSet<String>());
                    set.remove(appDetails.get(position).getPackageName().toString());
                    sp.edit().putStringSet("home_apps",set).apply();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
                    return true;


            }


        }
        return false;
    }


}
