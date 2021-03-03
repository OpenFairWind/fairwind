package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperAdapter;
import it.uniparthenope.fairwind.captain.setup.helper.ItemTouchHelperViewHolder;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Key;

/**
 * Created by raffaelemontella on 27/02/2017.
 */

public class ChildrenRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemTouchHelperAdapter {
    public static final String LOG_TAG="KEYS..ADAPTER";

    private KeysDialogFragment keysDialogFragment;
    private List<Key> mValues;

    public ChildrenRecyclerViewAdapter(KeysDialogFragment keysDialogFragment, KeysContent keysContent) {
        this.keysDialogFragment = keysDialogFragment;
        setContent(keysContent);
    }

    public void setContent(KeysContent keysContent) {
        mValues = keysContent.getChildren();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_children_item, parent, false);
        return new ChildrenItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ChildrenItemViewHolder itemViewHolder=(ChildrenItemViewHolder)holder;
        itemViewHolder.mAdapter=this;
        itemViewHolder.mItem = mValues.get(position);

        String[] parts=mValues.get(position).getPath().split("/");
        itemViewHolder.mKey.setText(parts[parts.length-1]);

        itemViewHolder.mDescription.setText(mValues.get(position).getDescription());




        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                keysDialogFragment.onChildrenSelection(itemViewHolder.mItem);
                //}
            }
        });

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }


    public class ChildrenItemViewHolder extends RecyclerView.ViewHolder   implements ItemTouchHelperViewHolder {
        public static final String LOG_TAG="ITEM_VIEW_HOLDER";

        public  RecyclerView.Adapter mAdapter=null;
        public final View mView;
        public final TextView mKey;
        public final TextView mDescription;

        public Key mItem;

        public ChildrenItemViewHolder(View view) {
            super(view);
            mView = view;
            FrameLayout frameLayout=(FrameLayout)view.findViewById(R.id.itemChidldren);

            mKey = (TextView) frameLayout.findViewById(R.id.item_key);
            mDescription = (TextView) frameLayout.findViewById(R.id.item_description);
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
