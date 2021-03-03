package it.uniparthenope.fairwind.captain.mydata;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.signalk.SignalKAdapter;
import it.uniparthenope.fairwind.captain.mydata.signalk.SignalKContent;
import it.uniparthenope.fairwind.captain.mydata.signalk.SignalKItem;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class SignalKFragment extends FairWindFragment implements AbsListView.OnItemClickListener, Runnable , LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "SIGNALK_FRAGMENT";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static final int LIST_ID = 1;
    public static final int REFRESH_MILLS=500;

    private View mView;
    private Handler handler;
    private SignalKContent signalKContent;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SignalKAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static SignalKFragment newInstance(String param1, String param2) {
        SignalKFragment fragment = new SignalKFragment();
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
    public SignalKFragment() {
        handler = new Handler();
        mAdapter = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        //mAdapter = new ArrayAdapter<SignalKContent.DummyItem>(getActivity(),
        //        android.R.layout.simple_list_item_1, android.R.id.text1, SignalKContent.ITEMS);
        signalKContent=new SignalKContent();

        //getLoaderManager().initLoader(LIST_ID, null, this);
        handler.postDelayed(this, REFRESH_MILLS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_mydata_signalk_grid, container, false);

        // Set the adapter
        mListView = (AbsListView) mView.findViewById(R.id.signalk_item_list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);


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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(signalKContent.getItems().get(position).getSId());
        }
    }





    @Override
    public void run() {
        /* do what you need to do */

        // Check if the fragment is attached to its activity
        if(isAdded()) {
            // Restart the loader
            getLoaderManager().restartLoader(LIST_ID, null, this);
        }

        // Set the next call
        handler.postDelayed(this, REFRESH_MILLS);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG,"onCreateLoader");
        CursorLoader loader = new CursorLoader(
                this.getActivity(),
                Uri.parse(FairWindModel.SignalK.CONTENT_URI + ""),
                null,
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"onLoadFinished");
        if (data != null && data.getCount() > 0) {
            signalKContent.clear();
            long id=1;
            int idIndex=data.getColumnIndex(FairWindModel.SignalK._ID);
            int itemIndex = data.getColumnIndex(FairWindModel.SignalK.ITEM);

            data.moveToFirst();
            do {

                String sId = data.getString(idIndex);
                String sData = data.getString(itemIndex);



                signalKContent.addItem(new SignalKItem(id, sId,sData));
                id++;

            } while(data.moveToNext());


            if (mAdapter==null) {
                mAdapter = new SignalKAdapter(getActivity(), signalKContent);

                // Set the adapter
                ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }


        }



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG,"onLoaderReset");
    }

}
