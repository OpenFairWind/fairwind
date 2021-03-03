package it.uniparthenope.fairwind.captain.apps;

/**
 * Created by raffaelemontella on 19/09/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.AppDetail;
import it.uniparthenope.fairwind.model.impl.AppDetails;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllAppsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseAppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class BaseAppsFragment extends FairWindFragment {

    private GridView gridView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppDetails appDetails;
    private PackageManager manager;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllAppsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseAppsFragment newInstance(Class theClass, String param1, String param2) {
        BaseAppsFragment fragment=null;
        try {
            fragment = (BaseAppsFragment)theClass.newInstance();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }

    public BaseAppsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        manager= getActivity().getPackageManager();
        
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_appslist, container, false);

        View view = inflater.inflate(R.layout.fragment_appslist, container, false);
        appDetails=getAppDetails();
        loadGridView(view, container);
        return view;
    }

    public abstract AppDetails getAppDetails();

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /*******
     *
     */


    private void loadGridView(View view, ViewGroup container){

        gridView = (GridView) view.findViewById(R.id.apps_list);


        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (30*scale + 0.5f);
        gridView.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);



        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(getActivity().getApplicationContext(),
                R.layout.list_item,
                appDetails) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.row_grid, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_image);
                appIcon.setImageDrawable(appDetails.get(position).getIcon());
               // android.view.ViewGroup.LayoutParams layoutParams = appIcon.getLayoutParams();
                //layoutParams.width = 110;
                //layoutParams.height = 110;
                //appIcon.setLayoutParams(layoutParams);

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_text);
                appLabel.setText(appDetails.get(position).getLabel());
                appLabel.setTextColor(Color.WHITE);


                //TextView appName = (TextView)convertView.findViewById(R.id.item_app_name);
                //appName.setText(apps.get(position).name);

                return convertView;
            }
        };

        adapter.sort(new Comparator<AppDetail>() {
            @Override
            public int compare(AppDetail appDetail, AppDetail t1) {
                return appDetail.getLabel().toString().compareToIgnoreCase(t1.getLabel().toString());
            }
        });

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = manager.getLaunchIntentForPackage(appDetails.get(pos).getPackageName().toString());
                BaseAppsFragment.this.startActivity(i);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //  super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        menu.setHeaderTitle("Salva "+appDetails.get(position).getPackageName()+" come:");
        menu.add(Menu.NONE, R.id.home_item, Menu.NONE, R.string.home_app);
        menu.add(Menu.NONE, R.id.marine_item, Menu.NONE, R.string.marine_app);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(getUserVisibleHint())
        {

            SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getContext());

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;


            switch (item.getItemId()) {

                case R.id.home_item:
                    Set<String> set=sp.getStringSet("home_apps", new HashSet<String>());
                    set.add(appDetails.get(position).getPackageName().toString());
                    sp.edit().putStringSet("home_apps",set).apply();
                    return true;

                case R.id.marine_item:
                    Set<String> set1=sp.getStringSet("marine_apps", new HashSet<String>());
                    set1.add(appDetails.get(position).getPackageName().toString());
                    sp.edit().putStringSet("marine_apps",set1).apply();
                    return true;

            }

        }

        return false;
    }



}

