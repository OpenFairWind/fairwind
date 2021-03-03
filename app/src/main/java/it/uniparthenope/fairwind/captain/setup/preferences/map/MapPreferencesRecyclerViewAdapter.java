package it.uniparthenope.fairwind.captain.setup.preferences.map;

import android.content.DialogInterface;
import android.graphics.Color;
import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferencesDialog;


/**
 * Created by raffaelemontella on 01/08/16.
 */
public class MapPreferencesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="MYDATALISTENER..ADAPTER";

    private MapPreferencesFragment preferenceFragment;
    private List<MapPreferences> mValues;
    private final MapPreferencesFragment.OnListFragmentInteractionListener mListener;

    public MapPreferencesRecyclerViewAdapter(MapPreferencesFragment preferenceFragment, List<MapPreferences> items, MapPreferencesFragment.OnListFragmentInteractionListener listener) {
        this.preferenceFragment=preferenceFragment;
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mappreferences_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = mValues.get(position);
        itemViewHolder.mVisible.setChecked(mValues.get(position).isVisible());
        itemViewHolder.mName.setText(mValues.get(position).getName());
        itemViewHolder.setName(mValues.get(position).getName());

        itemViewHolder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className=itemViewHolder.mItem.getClassName()+"Dialog";
                try {
                    Class dialogClass = Class.forName(className);
                    MapPreferencesDialog dialog = (MapPreferencesDialog)dialogClass.newInstance();
                    dialog.setMapPreferences(itemViewHolder.mAdapter,itemViewHolder.mItem);
                    dialog.show(preferenceFragment.getFragmentManager(),"MapPreferencesDialog");

                } catch (ClassNotFoundException ex) {
                    Log.d(LOG_TAG,ex.getMessage());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(itemViewHolder.mItem);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d(LOG_TAG,fromPosition+"->"+toPosition);
        MapPreferences temp=mValues.get(toPosition);
        mValues.set(toPosition,mValues.get(fromPosition));
        mValues.set(fromPosition,temp);
        notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemDismiss(final  int position) {
        new AlertDialog.Builder(preferenceFragment.getActivity())
                .setTitle("Delete Map")
                .setMessage("Do you really want to delete "+mValues.get(position).getName()+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        mValues.remove(position);
                        notifyDataSetChanged();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        notifyDataSetChanged();
                    }}).show();

    }



    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final CheckBox mVisible;
        public final TextView mName;

        private String name;
        public void setName(String name) { this.name=name; }

        public MapPreferences mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mVisible = (CheckBox) view.findViewById(R.id.visible);
            mName = (TextView) view.findViewById(R.id.name);


            mVisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox)v;
                    mItem.setVisible(checkBox.isChecked());
                }
            });
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
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
