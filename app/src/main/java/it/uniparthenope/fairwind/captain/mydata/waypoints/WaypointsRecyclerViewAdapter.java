package it.uniparthenope.fairwind.captain.mydata.waypoints;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.WaypointsFragment;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoints;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 27/09/2017.
 */

public class WaypointsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="WAYPOINTS..ADAPTER";


    private WaypointsFragment waypointsFragment;
    private Waypoints content;


    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public WaypointsRecyclerViewAdapter(WaypointsFragment waypointsFragment, Waypoints content) {
        this.waypointsFragment = waypointsFragment;
        this.content = content;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_waypoints_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = (Waypoint) content.asOrderedList().get(position);

        Position currentPosition= FairWindApplication.getFairWindModel().getPosition();

        int imageId=R.drawable.cross_50x50;
        itemViewHolder.mImage.setImageResource(imageId);

        itemViewHolder.mName.setText(itemViewHolder.mItem.getId());

        itemViewHolder.mRange.setText(Formatter.formatRange(Formatter.UNIT_RANGE_NM,itemViewHolder.mItem.getRange(currentPosition),"n/a"));
        itemViewHolder.mBearing.setText(Formatter.formatDirection(itemViewHolder.mItem.getBearing(currentPosition),"n/a"));

        itemViewHolder.mLatitude.setText(Formatter.formatLatitude(Formatter.COORDS_STYLE_DDMMSS,itemViewHolder.mItem.getLatitude(),"n/a"));
        itemViewHolder.mLongitude.setText(Formatter.formatLongitude(Formatter.COORDS_STYLE_DDMMSS,itemViewHolder.mItem.getLongitude(),"n/a"));
        itemViewHolder.mDate.setText(Formatter.formatDate(Formatter.DATE_STYLE_DDMMYYYY,itemViewHolder.mItem.getTimeStamp(),""));
        itemViewHolder.mTime.setText(Formatter.formatTime(Formatter.TIME_STYLE_HHMM,itemViewHolder.mItem.getTimeStamp(),""));

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                waypointsFragment.onListFragmentInteraction(itemViewHolder.mItem);
                //}
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return content.asList().size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    private int position;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder, View.OnCreateContextMenuListener {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final ImageView mImage;
        public final TextView mName;
        public final TextView mBearing;
        public final TextView mRange;
        public final TextView mLatitude;
        public final TextView mLongitude;
        public final TextView mTime;
        public final TextView mDate;

        public Waypoint mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;

            mImage=(ImageView)view.findViewById(R.id.item_image);
            mName = (TextView) view.findViewById(R.id.item_name);
            mBearing = (TextView) view.findViewById(R.id.item_bearing);
            mRange = (TextView) view.findViewById(R.id.item_range);
            mLatitude = (TextView) view.findViewById(R.id.item_latitude);
            mLongitude = (TextView) view.findViewById(R.id.item_longitude);
            mDate = (TextView) view.findViewById(R.id.item_date);
            mTime = (TextView) view.findViewById(R.id.item_time);

            view.setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle(mItem.getId());
            menu.add(Menu.NONE, R.id.goto_item, Menu.NONE, "Goto");
            menu.add(Menu.NONE, R.id.share_item, Menu.NONE, R.string.share);
            menu.add(Menu.NONE, R.id.edit_item, Menu.NONE, "Edit");
            menu.add(Menu.NONE, R.id.remove_item,Menu.NONE, R.string.remove);
        }
    }
}
