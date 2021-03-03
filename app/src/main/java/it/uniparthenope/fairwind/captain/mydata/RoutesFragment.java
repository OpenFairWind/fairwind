package it.uniparthenope.fairwind.captain.mydata;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.routes.RoutesRecyclerViewAdapter;
import it.uniparthenope.fairwind.sdk.model.resources.routes.Route;
import it.uniparthenope.fairwind.sdk.model.resources.routes.Routes;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;

/**
 * Created by raffaelemontella on 22/09/2017.
 */

public class RoutesFragment extends FairWindFragment {

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
    private Routes routes;
    private RoutesRecyclerViewAdapter routesRecyclerViewAdapter;


    // TODO: Rename and change types of parameters
    public static RoutesFragment newInstance(String param1, String param2) {
        RoutesFragment fragment = new RoutesFragment();
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
    public RoutesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        routes =new Routes(FairWindApplication.getFairWindModel());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_routes_list, container, false);

        mRecyclerView=(RecyclerView)mView.findViewById(R.id.list_tracks);
        routesRecyclerViewAdapter = new RoutesRecyclerViewAdapter(this, routes);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(routesRecyclerViewAdapter);

        registerForContextMenu(mRecyclerView);

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


    public void onListFragmentInteraction(Route mItem) {
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = routesRecyclerViewAdapter.getPosition();
        if (position!=-1) {

        }
        return super.onContextItemSelected(item);
    }
}
