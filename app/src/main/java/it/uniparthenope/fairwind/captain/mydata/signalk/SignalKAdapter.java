package it.uniparthenope.fairwind.captain.mydata.signalk;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniparthenope.fairwind.R;

/**
 * Created by raffaelemontella on 04/09/15.
 */
public class SignalKAdapter extends BaseAdapter {
    private Context mContext;
    private SignalKContent signalKContent;

    @Override
    public int getCount() {
        return signalKContent.getItems().size();
    }

    @Override
    public Object getItem(int position) {
        return signalKContent.getItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return signalKContent.getItems().get(position).getId();
    }

    static class ViewHolderItem {
        TextView idView;
        TextView textView;
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.fragment_mydata_signalk_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.idView = (TextView) convertView.findViewById(R.id.item_id);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_text);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_image);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // object item based on the position
        SignalKItem signalKItem = signalKContent.getItems().get(position);

        // assign values if the object is not null
        if(signalKItem != null) {

            // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
            viewHolder.idView.setText(signalKItem.getSId());
            viewHolder.idView.setTag(signalKItem.getId());

            viewHolder.textView.setText(signalKItem.getSData());
            viewHolder.textView.setTag(signalKItem.getId());


            viewHolder.imageView.setImageResource(R.drawable.signalk_icon);


            viewHolder.imageView.setTag(signalKItem.getId());


        }

        return convertView;
    }

    public SignalKAdapter(Context context, SignalKContent signalKContent) {
        mContext=context;
        this.signalKContent=signalKContent;
    }
}
