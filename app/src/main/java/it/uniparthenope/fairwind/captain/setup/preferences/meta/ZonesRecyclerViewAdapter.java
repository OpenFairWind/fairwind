package it.uniparthenope.fairwind.captain.setup.preferences.meta;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.State;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Zone;

/**
 * Created by raffaelemontella on 09/02/2017.
 */

public class ZonesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="ZONES..ADAPTER";

    private ZonesDialogFragment zonesDialogFragment;
    private List<Zone> mValues;
    //private ZonesContent zonesContent;


    public ZonesRecyclerViewAdapter(ZonesDialogFragment zonesDialogFragment,  List<Zone> items) {
        this.zonesDialogFragment = zonesDialogFragment;
        mValues = items;
        //this.zonesContent=zonesContent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_zone_item, parent, false);
        return new ZonesRecyclerViewAdapter.ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ZonesRecyclerViewAdapter.ItemViewHolder itemViewHolder=(ZonesRecyclerViewAdapter.ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = mValues.get(position);
        itemViewHolder.mLower.setText(Double.toString(mValues.get(position).getLower()));
        itemViewHolder.mUpper.setText(Double.toString(mValues.get(position).getUpper()));
        itemViewHolder.mState.setSelection(State.valueOf(mValues.get(position).getState().toString().toUpperCase()).ordinal());
        itemViewHolder.mMessage.setText(mValues.get(position).getMessage());

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zonesDialogFragment.onListFragmentInteraction(itemViewHolder.mItem);
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
        Zone temp=mValues.get(toPosition);
        mValues.set(toPosition,mValues.get(fromPosition));
        mValues.set(fromPosition,temp);
        notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mValues.remove(position);
        notifyDataSetChanged();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final EditText mLower;
        public final TextView mLowerUnits;
        public final EditText mUpper;
        public final TextView mUpperUnits;
        public final Spinner mState;
        public final EditText mMessage;


        public Zone mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mLower = (EditText) view.findViewById(R.id.item_lower);
            mLowerUnits = (TextView) view.findViewById(R.id.item_lower_units);
            mUpper = (EditText) view.findViewById(R.id.item_upper);
            mUpperUnits = (TextView) view.findViewById(R.id.item_upper_units);
            mState = (Spinner) view.findViewById(R.id.item_state);
            mMessage = (EditText) view.findViewById(R.id.item_message);



            mLower.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        mItem.setLower(Double.parseDouble(charSequence.toString()));
                    } catch (NumberFormatException ex) {
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mUpper.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        mItem.setUpper(Double.parseDouble(charSequence.toString()));
                    } catch (NumberFormatException ex) {
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            mState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    mItem.setState(State.values()[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });



            mMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mItem.setMessage(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
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
