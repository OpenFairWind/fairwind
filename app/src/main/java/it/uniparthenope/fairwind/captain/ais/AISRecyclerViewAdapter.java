package it.uniparthenope.fairwind.captain.ais;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 22/02/2017.
 */

public class AISRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="AIS..ADAPTER";


    private AISListFragment aisListFragment;
    private List<AISItem> mValues;
    //private final KeysDialogFragment.OnListFragmentInteractionListener mListener;

    public AISRecyclerViewAdapter(AISListFragment aisListFragment, List<AISItem> items/*, KeysDialogFragment.OnListFragmentInteractionListener listener*/) {
        this.aisListFragment = aisListFragment;
        mValues = items;
        //mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ais_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = mValues.get(position);
        itemViewHolder.mMmsi.setText(mValues.get(position).getMmsi());
        itemViewHolder.mName.setText(mValues.get(position).getName());
        itemViewHolder.mRange.setText(Formatter.formatRange(Formatter.UNIT_RANGE_NM,mValues.get(position).getRange(),"n/a"));
        itemViewHolder.mSpeed.setText(Formatter.formatSpeed(Formatter.UNIT_SPEED_KNT,mValues.get(position).getSpeed(),"n/a"));
        itemViewHolder.mCourse.setText(Formatter.formatDirection(mValues.get(position).getCourse(),"n/a"));
        itemViewHolder.mBearing.setText(Formatter.formatDirection(mValues.get(position).getBearing(),"n/a"));

        switch (mValues.get(position).getType()) {
            case 4:
            case 6:
                itemViewHolder.mImage.setImageResource(R.drawable.ais_commercial);
                break;
            case 36:
                itemViewHolder.mImage.setImageResource(R.drawable.ais_yacht);
                break;
            case 37:
                itemViewHolder.mImage.setImageResource(R.drawable.ais_highspeed);
                break;
            default:
                itemViewHolder.mImage.setImageResource(R.drawable.ais_unknown);
        }

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                aisListFragment.onListFragmentInteraction(itemViewHolder.mItem);
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
        public final TextView mMmsi;
        public final TextView mName;
        public final TextView mRange;
        public final TextView mSpeed;
        public final TextView mCourse;
        public final TextView mBearing;
        public final ImageView mImage;


        public AISItem mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;

            mMmsi = (TextView) view.findViewById(R.id.item_mmsi);
            mName = (TextView) view.findViewById(R.id.item_name);
            mRange = (TextView) view.findViewById(R.id.item_range);
            mSpeed = (TextView) view.findViewById(R.id.item_speed);
            mCourse = (TextView) view.findViewById(R.id.item_course);
            mBearing = (TextView) view.findViewById(R.id.item_bearing);
            mImage = (ImageView) view.findViewById(R.id.item_image);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mMmsi.getText() + "'";
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
