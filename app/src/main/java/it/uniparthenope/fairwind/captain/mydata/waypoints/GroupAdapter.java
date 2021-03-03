package it.uniparthenope.fairwind.captain.mydata.waypoints;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import it.uniparthenope.fairwind.R;

/**
 * Created by raffaelemontella on 28/10/2017.
 */

public class GroupAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    public GroupAdapter(Activity activity) {
        super();
        inflater= activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return 26;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_waypoint_group_item, null);
        }
        return convertView;
    }

}
