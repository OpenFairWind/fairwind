package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.MetaPreferences;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Method;

/**
 * Created by raffaelemontella on 07/02/2017.
 */

public class MetaPreferencesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="META..ADAPTER";

    private MetaPreferencesFragment metaPreferencesFragment;
    private List<MetaPreferences> mValues;
    private final MetaPreferencesFragment.OnListFragmentInteractionListener mListener;

    public MetaPreferencesRecyclerViewAdapter(MetaPreferencesFragment metaPreferencesFragment, List<MetaPreferences> items, MetaPreferencesFragment.OnListFragmentInteractionListener listener) {
        this.metaPreferencesFragment=metaPreferencesFragment;
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_metapreferences_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = mValues.get(position);
        itemViewHolder.mDisplayName.setText(mValues.get(position).getDisplayName());
        itemViewHolder.mShortName.setText(mValues.get(position).getShortName());

        itemViewHolder.mWarnMethod.setSelection(mValues.get(position).getWarnMethod().ordinal());
        itemViewHolder.mAlarmMethod.setSelection(mValues.get(position).getAlarmMethod().ordinal());

        itemViewHolder.mDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetaPreferencesDialog dialog=new MetaPreferencesDialog();
                if (dialog!=null) {
                    dialog.setMetaPreferences(metaPreferencesFragment, itemViewHolder.mAdapter, itemViewHolder.mItem);
                    dialog.show(metaPreferencesFragment.getFragmentManager(), "metapreferencedialog");
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
        MetaPreferences temp=mValues.get(toPosition);
        mValues.set(toPosition,mValues.get(fromPosition));
        mValues.set(fromPosition,temp);
        metaPreferencesFragment.save();
        notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemDismiss(final  int position) {
        mValues.remove(position);
        metaPreferencesFragment.save();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final TextView mDisplayName;
        public final TextView mShortName;

        public final Spinner mWarnMethod;
        public final Spinner mAlarmMethod;



        public MetaPreferences mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mDisplayName = (TextView) view.findViewById(R.id.display_name);
            mShortName = (TextView) view.findViewById(R.id.short_name);

            mWarnMethod = (Spinner) view.findViewById(R.id.warn_method);
            mAlarmMethod = (Spinner) view.findViewById(R.id.alarm_method);

            mWarnMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    mItem.setWarnMethod(Method.values()[position]);
                    metaPreferencesFragment.save();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            mAlarmMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    mItem.setAlarmMethod(Method.values()[position]);
                    metaPreferencesFragment.save();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

        }


        @Override
        public String toString() {
            return super.toString() + " '" + mDisplayName.getText() + "'";
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
