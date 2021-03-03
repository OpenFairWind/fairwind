package it.uniparthenope.fairwind.services.signalkclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import javax.jmdns.ServiceInfo;

import it.uniparthenope.fairwind.R;

/**
 * Created by raffaelemontella on 14/12/2016.
 */

public class ServiceInfoAdapter extends ArrayAdapter<ServiceInfo> {
    public static final String LOG_TAG="SERVICEINFOADAPTER";

    public ServiceInfoAdapter(Context context, int resource, ArrayList<ServiceInfo> serviceInfos) {
        super(context, resource, serviceInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ServiceInfo serviceInfo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_signalkwebsocketclientdiscovery_item, parent, false);
        }
        // Lookup view for data population
        TextView txtAddress = (TextView) convertView.findViewById(R.id.item_address);
        TextView txtName = (TextView) convertView.findViewById(R.id.item_name);
        TextView txtText = (TextView) convertView.findViewById(R.id.item_text);
        // Populate the data into the template view using the data object

        String name=serviceInfo.getName();
        String[] addresses=serviceInfo.getHostAddresses();
        String address="n/a";
        if (addresses!=null && addresses.length>0) {
            address=addresses[0];
        }
        String text=serviceInfo.getNiceTextString();
        text=text.replace("\n\t",";").replace("\\025",";").replace("\\013",";").replace("\\015",";").replace("\\016",";");
        if (text.startsWith(";")) {
            text=text.substring(1);
        }
        txtAddress.setText(address);
        txtName.setText(name);
        txtText.setText(text);
        // Return the completed view to render on screen
        Log.d(LOG_TAG,"pos:"+position+" serviceInfo:"+serviceInfo);
        return convertView;
    }
}
