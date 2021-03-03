package it.uniparthenope.fairwind.captain.mydata.files;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.mydata.FilesFragment;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;

/**
 * Created by raffaelemontella on 19/09/2017.
 */

public class FilesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="FILES..ADAPTER";


    private FilesFragment filesFragment;
    private FilesContent content;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public FilesRecyclerViewAdapter(FilesFragment filesFragment, FilesContent content) {
        this.filesFragment = filesFragment;
        this.content=content;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mydata_files_item, parent, false);

        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder=(ItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = content.getItems().get(position);


        long lastModified=content.getItems().get(position).getLastModified();
        String fileName=content.getItems().get(position).getFileName();

        int imageId;

        if (fileName.endsWith("/")) {
            imageId=R.drawable.folders;
        } else if (fileName.endsWith(".signalk.json")) {
            imageId=R.drawable.signalk_icon;
        } else if (fileName.endsWith(".nmea")) {
            imageId=R.drawable.filesnmea;
        } else if (fileName.endsWith(".aes.zip")) {
            imageId=R.drawable.files;
        } else {
            imageId=R.drawable.files;
        }


        itemViewHolder.mImage.setImageResource(imageId);
        itemViewHolder.mFileName.setText(fileName);

        String sLastModified="";
        String sSize="";
        if (fileName.endsWith("/")==false) {
            sLastModified=sdf.format(lastModified);
            long size = content.getItems().get(position).getSize();
            sSize = String.format("%d", Math.round(size * 1.0 / 1000.0));
        }
        itemViewHolder.mDateTime.setText(sLastModified);
        itemViewHolder.mSize.setText(sSize);




        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                filesFragment.onListFragmentInteraction(itemViewHolder.mItem);
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
        return content.getItems().size();
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
        public final TextView mFileName;
        public final TextView mDateTime;
        public final TextView mSize;

        public FilesItem mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;

            mImage=(ImageView)view.findViewById(R.id.item_image);
            mFileName = (TextView) view.findViewById(R.id.item_filename);
            mDateTime = (TextView) view.findViewById(R.id.item_datetime);
            mSize = (TextView) view.findViewById(R.id.item_size);

            view.setOnCreateContextMenuListener(this);

        }


        @Override
        public String toString() {
            return super.toString() + " '" + mFileName.getText() + "'";
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

            menu.setHeaderTitle(mItem.getFileName());
            menu.add(Menu.NONE, R.id.open_item, Menu.NONE, R.string.open);
            menu.add(Menu.NONE, R.id.share_item, Menu.NONE, R.string.share);
        }

    }
}
