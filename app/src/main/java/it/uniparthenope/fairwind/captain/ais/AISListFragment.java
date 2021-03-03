package it.uniparthenope.fairwind.captain.ais;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.ui.FairWindFragment;
import it.uniparthenope.fairwind.sdk.util.Position;

import nz.co.fortytwo.signalk.handler.JsonGetHandler;
import nz.co.fortytwo.signalk.util.SignalKConstants;


/**
 * Created by raffaelemontella on 19/04/16.
 */
public class AISListFragment extends FairWindFragment implements AbsListView.OnItemClickListener, Runnable, SeekBar.OnSeekBarChangeListener {

    public static final String LOG_TAG="AISLIST_FRAGMENT";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final int LIST_ID = 1;
    public static final int REFRESH_MILLS=5000;

    private View mView;
    private TextView mAISRangeTextView;
    private SeekBar mAISRangeSeekBar;
    private Button mType;
    private Button mMmsi;
    private Button mName;
    private Button mRange;
    private Button mBearing;
    private Button mSpeed;
    private Button mCourse;
    private Handler handler;
    private OnFragmentInteractionListener mListener;

    private AISField aisSortField=AISField.RANGE;
    private AISOrder aisOrder=AISOrder.ASCENDING;

    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView mRecyclerView;
    private AISContent aisContent;
    private AISRecyclerViewAdapter aisRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AISListFragment() {
        handler = new Handler();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RollingRoadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AISListFragment newInstance(String param1, String param2) {
        AISListFragment fragment = new AISListFragment();
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
        aisContent=new AISContent();




        //getLoaderManager().initLoader(LIST_ID, null, this);
        handler.postDelayed(this, REFRESH_MILLS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreateView");

        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        mView = inflater.inflate(R.layout.fragment_ais_list, container, false);


        mRecyclerView=(RecyclerView)mView.findViewById(R.id.list_ais);
        aisRecyclerViewAdapter = new AISRecyclerViewAdapter(this, aisContent.getItems());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(aisRecyclerViewAdapter);



        // Set OnItemClickListener so we can be notified on item clicks
        //mRecyclerView.setOnItemClickListener(this);

        mAISRangeSeekBar=(SeekBar)mView.findViewById(R.id.ais_range_seekBar);
        mAISRangeSeekBar.setOnSeekBarChangeListener(this);

        mAISRangeTextView=(TextView) mView.findViewById(R.id.ais_range_textView);

        mType=(Button)mView.findViewById(R.id.ais_type_button);
        mType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.TYPE);
            }
        });


        mMmsi=(Button)mView.findViewById(R.id.ais_mmsi_button);
        mMmsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.MMSI);
            }
        });

        mName=(Button)mView.findViewById(R.id.ais_name_button);
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.NAME);
            }
        });

        mRange=(Button)mView.findViewById(R.id.ais_range_button);
        mRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.RANGE);
            }
        });

        mBearing=(Button)mView.findViewById(R.id.ais_bearing_button);
        mBearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.BEARING);
            }
        });

        mSpeed=(Button)mView.findViewById(R.id.ais_sog_button);
        mSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.SPEED);
            }
        });

        mCourse=(Button)mView.findViewById(R.id.ais_cog_button);
        mCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortField(AISField.COURSE);
            }
        });


        return mView;
    }


    private void setSortField(AISField aisField) {
        if (aisSortField==aisField) {
            if (aisOrder==AISOrder.ASCENDING) {
                aisOrder=AISOrder.DESCENDING;
            } else {
                aisOrder=AISOrder.ASCENDING;
            }
        }
        aisSortField=aisField;
        aisContent.sortItems(aisSortField,aisOrder);
        aisRecyclerViewAdapter.notifyDataSetChanged();
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser==true) {
            if (progress==101) {
                mAISRangeTextView.setText("100+");
            } else {
                String sProgress = String.format("%d", progress);
                mAISRangeTextView.setText(sProgress);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(aisContent.getItems().get(position).getMmsi());
        }
    }

    public void onListFragmentInteraction(AISItem mItem) {
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


    @Override
    public void run() {
        Log.d(LOG_TAG, "run");
        if(mView!=null) {

            if (isAdded()) {
                FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();

                JsonGetHandler jsonGetHandler=new JsonGetHandler();

                List<String> vesselsList = jsonGetHandler.getMatchingPaths(fairWindModel,"vessels.*");

                if (vesselsList.size()>0) {

                    aisContent.clear();
                    long id=1;
                    for (String key:vesselsList) {
                        String uuid = key.split("\\.")[1];
                        if (aisContent.getByUuid(uuid)==null && uuid.equals(SignalKConstants.self)==false && uuid.equals("self")==false && uuid.equals("*")==false) {
                            Position position = fairWindModel.getNavPosition(uuid);
                            if (position!=null && (fairWindModel.getPosition().distanceTo(position)<mAISRangeSeekBar.getProgress() || mAISRangeSeekBar.getProgress()==101))  {
                                aisContent.addItem(new AISItem(id,uuid));
                                id++;
                            }
                        }

                    }

                    if (aisRecyclerViewAdapter!=null) {
                        aisContent.sortItems(aisSortField,aisOrder);
                        aisRecyclerViewAdapter.notifyDataSetChanged();
                    }

                }
            }
        }

        // Set the next call
        handler.postDelayed(this, REFRESH_MILLS);
    }
}
