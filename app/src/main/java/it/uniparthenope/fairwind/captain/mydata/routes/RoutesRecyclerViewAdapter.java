package it.uniparthenope.fairwind.captain.mydata.routes;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.RoutesFragment;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.routes.Route;
import it.uniparthenope.fairwind.sdk.model.resources.routes.Routes;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 30/09/2017.
 */

public class RoutesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="ROUTES..ADAPTER";


    private RoutesFragment routesFragment;
    private Routes content;
    //private final KeysDialogFragment.OnListFragmentInteractionListener mListener;



    public RoutesRecyclerViewAdapter(RoutesFragment routesFragment, Routes content/*, KeysDialogFragment.OnListFragmentInteractionListener listener*/) {
        this.routesFragment = routesFragment;
        this.content = content;
        //mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_routes_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = (Route) content.asList().get(position);

        int points=itemViewHolder.mItem.getPoints();
        double distance=itemViewHolder.mItem.getDistance();


        int imageId=R.drawable.icon_route;
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
                routesFragment.onListFragmentInteraction(itemViewHolder.mItem);
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


    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder,  View.OnCreateContextMenuListener  {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final ImageView mImage;
        public final TextView mName;
        public final TextView mPoints;
        public final TextView mLength;


        public Route mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;

            mImage=(ImageView)view.findViewById(R.id.item_image);
            mName = (TextView) view.findViewById(R.id.item_name);
            mPoints = (TextView) view.findViewById(R.id.item_points);
            mLength = (TextView) view.findViewById(R.id.item_length);

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
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Actions");
            menu.add(Menu.NONE, R.id.open_item, Menu.NONE, R.string.open);
            menu.add(Menu.NONE, R.id.share_item, Menu.NONE, R.string.share);
        }
    }
}
