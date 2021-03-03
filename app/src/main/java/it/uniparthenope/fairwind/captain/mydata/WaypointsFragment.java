package it.uniparthenope.fairwind.captain.mydata;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import java.io.File;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.waypoints.WaypointDialog;
import it.uniparthenope.fairwind.captain.mydata.waypoints.WaypointsRecyclerViewAdapter;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapView;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoints;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;
import it.uniparthenope.fairwind.sdk.ui.MapView;
import it.uniparthenope.fairwind.sdk.ui.base.PositionView;

/**
 * Created by raffaelemontella on 22/09/2017.
 */

public class WaypointsFragment extends FairWindFragment {

    public static final String LOG_TAG="WAYPOINTS_FRAGMENT";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private View mView;

    private RecyclerView mRecyclerView;
    private Waypoints waypoints;
    private WaypointsRecyclerViewAdapter waypointsRecyclerViewAdapter;

    // TODO: Rename and change types of parameters
    public static WaypointsFragment newInstance(String param1, String param2) {
        WaypointsFragment fragment = new WaypointsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WaypointsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        waypoints =new Waypoints(FairWindApplication.getFairWindModel());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_waypoints_list, container, false);

        mRecyclerView=(RecyclerView)mView.findViewById(R.id.list_waypoints);
        waypointsRecyclerViewAdapter = new WaypointsRecyclerViewAdapter(this, waypoints);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(waypointsRecyclerViewAdapter);

        registerForContextMenu(mRecyclerView);

        Button btnNewWaypoint=(Button)mView.findViewById(R.id.btn_new_waypoint);
        btnNewWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
                fairWindModel.createWaypoint(getContext(),view);
                waypointsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        Button btnNewGroup=(Button)mView.findViewById(R.id.btn_new_group);
        btnNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open dialog
            }
        });

        Spinner spinnerSort=(Spinner)mView.findViewById(R.id.spinner_sort);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                waypoints.orderBy(pos);
                waypointsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinnerOrder=(Spinner)mView.findViewById(R.id.spinner_order);
        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                waypoints.sortBy(pos);
                waypointsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button btnSelectAll=(Button)mView.findViewById(R.id.btn_import);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waypointsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        Button btnUnselectAll=(Button)mView.findViewById(R.id.btn_export);
        btnUnselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waypointsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
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
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"onStart");
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


    public void onListFragmentInteraction(Waypoint mItem) {
        MapView mapView=(MapView)mView.findViewById(R.id.map_view);
        if (mapView!=null) {
            mapView.setCenter(mItem.getPosition());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = waypointsRecyclerViewAdapter.getPosition();
        if (position!=-1) {


            if (getUserVisibleHint()) {
                FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
                Waypoint waypoint=(Waypoint) waypoints.asList().get(position);
                Intent i = new Intent();

                switch (item.getItemId()) {
                    case R.id.edit_item:
                        WaypointDialog waypointDialog=new WaypointDialog();
                        waypointDialog.setAdapter(waypointsRecyclerViewAdapter);
                        waypointDialog.setWaypoint(waypoint);
                        waypointDialog.show(getFragmentManager(),"WAYPOINT");
                        break;

                    case R.id.goto_item:
                        fairWindModel.getWaypoints().goTo(waypoint);

                    case R.id.share_item:
                        break;

                    case R.id.remove_item:
                        fairWindModel.getWaypoints().remove(waypoint);
                        waypointsRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }
        return super.onContextItemSelected(item);
    }
}
