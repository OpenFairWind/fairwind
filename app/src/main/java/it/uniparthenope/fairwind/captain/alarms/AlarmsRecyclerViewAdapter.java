package it.uniparthenope.fairwind.captain.alarms;

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
import it.uniparthenope.fairwind.sdk.captain.setup.meta.State;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 23/02/2017.
 */

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="AIS..ADAPTER";


    private AlarmsListFragment alarmsListFragment;
    private List<AlarmsItem> mValues;
    //private final KeysDialogFragment.OnListFragmentInteractionListener mListener;

    public AlarmsRecyclerViewAdapter(AlarmsListFragment alarmsListFragment, List<AlarmsItem> items/*, KeysDialogFragment.OnListFragmentInteractionListener listener*/) {
        this.alarmsListFragment = alarmsListFragment;
        mValues = items;
        //mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_alarms_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = mValues.get(position);

        itemViewHolder.mState.setText(mValues.get(position).getState().toString());
        itemViewHolder.mDescription.setText(mValues.get(position).getDescription());
        itemViewHolder.mMessage.setText(mValues.get(position).getMessage());

        switch (mValues.get(position).getState().ordinal()) {
            case 0:
                itemViewHolder.mImage.setImageResource(R.drawable.alarms_state_normal);
                break;
            case 1:
                itemViewHolder.mImage.setImageResource(R.drawable.alarms_state_alert);
                break;
            case 2:
                itemViewHolder.mImage.setImageResource(R.drawable.alarms_state_warn);
                break;
            case 3:
                itemViewHolder.mImage.setImageResource(R.drawable.alarms_state_alarm);
                break;
            case 4:
                itemViewHolder.mImage.setImageResource(R.drawable.alarms_state_emergency);
                break;
            default:
                itemViewHolder.mImage.setImageResource(R.drawable.alarms_state_unknown);
        }


        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                alarmsListFragment.onListFragmentInteraction(itemViewHolder.mItem);
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
        public final TextView mState;
        public final TextView mDescription;
        public final TextView mMessage;




        public AlarmsItem mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;

            mImage = (ImageView) view.findViewById(R.id.item_image);
            mState = (TextView) view.findViewById(R.id.item_state);
            mDescription = (TextView) view.findViewById(R.id.item_description);
            mMessage = (TextView) view.findViewById(R.id.item_message);


        }


        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText() + "'";
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
