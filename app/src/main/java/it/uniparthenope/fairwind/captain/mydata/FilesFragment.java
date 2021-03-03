package it.uniparthenope.fairwind.captain.mydata;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.io.File;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.alarms.AlarmsListFragment;
import it.uniparthenope.fairwind.captain.mydata.files.FilesContent;
import it.uniparthenope.fairwind.captain.mydata.files.FilesItem;
import it.uniparthenope.fairwind.captain.mydata.files.FilesRecyclerViewAdapter;
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
public class FilesFragment extends FairWindFragment /*implements AbsListView.OnItemClickListener*/ {

    public static final String LOG_TAG="ALARMS_FRAGMENT";

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
    private FilesContent filesContent;
    private FilesRecyclerViewAdapter filesRecyclerViewAdapter;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FilesFragment() {


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance(String param1, String param2) {
        FilesFragment fragment = new FilesFragment();
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

        filesContent =new FilesContent();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_mydata_files_list, container, false);


        mRecyclerView=(RecyclerView)mView.findViewById(R.id.list_mydata_signalk);
        filesRecyclerViewAdapter = new FilesRecyclerViewAdapter(this, filesContent);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(filesRecyclerViewAdapter);

        registerForContextMenu(mRecyclerView);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlarmsListFragment.OnFragmentInteractionListener) {
            mListener = (FilesFragment.OnFragmentInteractionListener) context;
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
    public void onListFragmentInteraction(FilesItem mItem) {

        String fileName=mItem.getFileName();
        if (fileName.endsWith("/")) {
            String current = filesContent.getCurrent();
            if (fileName.equals("../")) {
                String[] parts = current.split(File.separator);
                current = "/";
                for (int i = 1; i < parts.length - 1; i++) {
                    current += parts[i] + File.separator;
                }
            } else {
                if (!current.endsWith("/")) {
                    current += File.separator;
                }
                current += fileName;
            }
            if (!current.startsWith(filesContent.getBase()) ) {
                current = filesContent.getBase();
            }
            filesContent.setCurrent(current);
            filesRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = filesRecyclerViewAdapter.getPosition();
        if (position!=-1) {

            File currentFile=new File(filesContent.getCurrent()+File.separator+filesContent.getItems().get(position).getFileName());

            if (getUserVisibleHint() && currentFile != null && currentFile.exists()) {

                Intent i = new Intent();

                switch (item.getItemId()) {
                    case R.id.open_item:

                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(currentFile), "text/plain");
                        startActivity(Intent.createChooser(i, "Show File"));
                        break;

                    case R.id.share_item:

                        i.setAction(android.content.Intent.ACTION_SEND);

                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(currentFile));
                        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.intent_share_file));
                        i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.intent_share_file));
                        startActivity(Intent.createChooser(i, getResources().getString(R.string.intent_share_file)));
                        break;
                }
            }
        }
        return super.onContextItemSelected(item);
    }

}

