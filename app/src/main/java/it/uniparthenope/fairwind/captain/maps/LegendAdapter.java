package it.uniparthenope.fairwind.captain.maps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;

/**
 * Created by Lovig90 on 07/10/15.
 */
public class LegendAdapter extends BaseAdapter {

    private static final int TYPE_ITEM_TEXT_VIEW = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM_EDIT_TEXT = 2;

    private ArrayList<LegendItem> mItems = new ArrayList<LegendItem>();

    public ArrayList<LegendItem> getItems() { return mItems; }

    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private TreeSet<Integer> sectionEditText = new TreeSet<Integer>();
    private LayoutInflater mInflater;
    private SharedPreferences preferences;


    public LegendAdapter(Activity context) {
        // TODO Auto-generated constructor stub
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences=PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void addItemTextView(MapPreferences mapPreferences) {
        mItems.add(new LegendItem(mapPreferences));
    }
    public void addItemTextView(final String item, int icon, boolean checked) {
        mItems.add(new LegendItem(item,icon,checked));
        notifyDataSetChanged();
    }

    public void addItemEditText(final String item) {
        mItems.add(new LegendItem(item,-1,false));
        sectionEditText.add(mItems.size() - 1);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mItems.add(new LegendItem(item,-1,false));
        sectionHeader.add(mItems.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(sectionHeader.contains(position)){

            return TYPE_HEADER;
        }
        else if(sectionEditText.contains(position)){

            return TYPE_ITEM_EDIT_TEXT;

        }else{

            return TYPE_ITEM_TEXT_VIEW;
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position).getName();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position,View view,ViewGroup parent) {

        final ViewHolder holder=new ViewHolder();
        int rowType = getItemViewType(position);
        LegendItem legendItem=mItems.get(position);

        switch (rowType) {
            case TYPE_ITEM_TEXT_VIEW:
                view = mInflater.inflate(R.layout.legend_adapter, null);

                holder.textView = (TextView) view.findViewById(R.id.legendTextView);
                holder.textView.setText(legendItem.getName());
                holder.icon=(ImageView) view.findViewById(R.id.iconLegend);
                holder.icon.setImageResource(legendItem.getIcon());
                holder.checkBox=(CheckBox)view.findViewById(R.id.checkboxLegend);
                holder.checkBox.setChecked(legendItem.isVisible());
                break;

            case TYPE_HEADER:
                view = mInflater.inflate(R.layout.legend_header, null);
                holder.textView = (TextView) view.findViewById(R.id.textHeader);
                holder.textView.setText(legendItem.getName());
                break;

            case TYPE_ITEM_EDIT_TEXT:
                view = mInflater.inflate(R.layout.legend_edit_text, null);
                holder.textView = (TextView) view.findViewById(R.id.legendTextView);
                holder.textView.setText(legendItem.getName());
                holder.editText = (EditText) view.findViewById(R.id.legendEditText);
                holder.editText.setText(preferences.getString("pref_key_maps_predictor", "0"));



                holder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        preferences.edit().putString("pref_key_maps_predictor", editable.toString()).apply();

                    }
                });

                break;
        }


        return view;
    }

    public static class ViewHolder {
        public TextView textView;
        public EditText editText;
        public ImageView icon;
        public CheckBox checkBox;
    }
}
