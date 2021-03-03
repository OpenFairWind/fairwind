package it.uniparthenope.fairwind.captain.setup.preferences.datalistener;

import android.graphics.Color;

import android.preference.PreferenceFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.preferences.datalistener.DataListenerPreferencesFragment.OnListFragmentInteractionListener;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DataListenerPreferences} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DataListenerPreferencesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="MYDATALISTENER..ADAPTER";

    private PreferenceFragment preferenceFragment;
    private List<DataListenerPreferences> mValues;
    private final OnListFragmentInteractionListener mListener;

    public DataListenerPreferencesRecyclerViewAdapter(PreferenceFragment preferenceFragment, List<DataListenerPreferences> items, OnListFragmentInteractionListener listener) {
        this.preferenceFragment=preferenceFragment;
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_datalistenerpreferences_item, parent, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
            itemViewHolder.mAdapter=this;
            itemViewHolder.mItem = mValues.get(position);
            itemViewHolder.mEnabled.setChecked(mValues.get(position).isEnabled());
            itemViewHolder.mName.setText(mValues.get(position).getName());
            itemViewHolder.mIsInput.setChecked(mValues.get(position).isInput());
            itemViewHolder.mIsOutput.setChecked(mValues.get(position).isOutput());
            itemViewHolder.setName(mValues.get(position).getName());

            itemViewHolder.mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String className=itemViewHolder.mItem.getClassName()+"Dialog";
                    try {
                        Class dialogClass = Class.forName(className);
                        DataListenerPreferencesDialog dialog = (DataListenerPreferencesDialog)dialogClass.getConstructor().newInstance();
                        if (dialog!=null) {
                            dialog.setDataListenerPreferences(preferenceFragment, itemViewHolder.mAdapter, itemViewHolder.mItem);
                            dialog.show(preferenceFragment.getFragmentManager(), "datalistenerpreferencedialog");
                        }
                    } catch (ClassNotFoundException ex) {
                        Log.d(LOG_TAG,ex.getMessage());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
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
        DataListenerPreferences temp=mValues.get(toPosition);
        mValues.set(toPosition,mValues.get(fromPosition));
        mValues.set(fromPosition,temp);
        ((DataListenerPreferencesFragment)preferenceFragment).save();
        notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemDismiss(final  int position) {
        /*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preferenceFragment.getActivity());
        alertDialogBuilder.setTitle("Delete Service");

        alertDialogBuilder.setMessage("Do you really want to delete " + mValues.get(position).getName() + "?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                mValues.remove(position);
                ((DataListenerPreferencesFragment) preferenceFragment).save();
                notifyDataSetChanged();
            }
        });
        alertDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                notifyDataSetChanged();
            }
        });
        alertDialogBuilder.show();
        */
        mValues.remove(position);
        ((DataListenerPreferencesFragment) preferenceFragment).save();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final CheckBox mEnabled;
        public final TextView mName;
        public final CheckBox mIsInput;
        public final CheckBox mIsOutput;

        private String name;
        public void setName(String name) { this.name=name; }

        public DataListenerPreferences mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mEnabled = (CheckBox) view.findViewById(R.id.enabled);
            mName = (TextView) view.findViewById(R.id.name);
            mIsInput = (CheckBox) view.findViewById(R.id.isinput);
            mIsOutput = (CheckBox) view.findViewById(R.id.isoutput);

            mEnabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox)v;
                    mItem.setEnabled(checkBox.isChecked());
                    ((DataListenerPreferencesFragment)preferenceFragment).save();
                }
            });



            mIsInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox)v;
                    mItem.setInput(checkBox.isChecked());
                    ((DataListenerPreferencesFragment)preferenceFragment).save();

                }
            });
            mIsOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox)v;
                    mItem.setOutput(checkBox.isChecked());
                    ((DataListenerPreferencesFragment)preferenceFragment).save();
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
