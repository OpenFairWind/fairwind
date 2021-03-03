package it.uniparthenope.fairwind.captain.mydata.tracks;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.TracksFragment;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.tracks.Track;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class TracksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="TRACKS..ADAPTER";


    private TracksFragment tracksFragment;
    private List<Resource> mValues;
    //private final KeysDialogFragment.OnListFragmentInteractionListener mListener;



    public TracksRecyclerViewAdapter(TracksFragment tracksFragment, List<Resource> items/*, KeysDialogFragment.OnListFragmentInteractionListener listener*/) {
        this.tracksFragment = tracksFragment;
        mValues = items;
        //mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tracks_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = (Track)mValues.get(position);

        int points=itemViewHolder.mItem.getPoints();
        double distance=itemViewHolder.mItem.getDistance();


        int imageId=R.drawable.icon_track;
        itemViewHolder.mImage.setImageResource(imageId);

        itemViewHolder.mName.setText(itemViewHolder.mItem .getName());

        itemViewHolder.mPoints.setText(Formatter.formatInteger(points,"n/a"));
        itemViewHolder.mLength.setText(Formatter.formatRange(Formatter.UNIT_RANGE_NM,distance,"n/a"));



        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                tracksFragment.onListFragmentInteraction(itemViewHolder.mItem);
                //}
            }
        });

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }


    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final ImageView mImage;
        public final TextView mName;
        public final TextView mPoints;
        public final TextView mLength;


        public Track mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;

            mImage=(ImageView)view.findViewById(R.id.item_image);
            mName = (TextView) view.findViewById(R.id.item_name);
            mPoints = (TextView) view.findViewById(R.id.item_points);
            mLength = (TextView) view.findViewById(R.id.item_length);


        }


        @Override
        public String toString() {
            return super.toString() + " '" +mName.getText() + "'";
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
