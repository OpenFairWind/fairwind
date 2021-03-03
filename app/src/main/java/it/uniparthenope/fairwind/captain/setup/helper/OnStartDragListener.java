package it.uniparthenope.fairwind.captain.setup.helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by raffaelemontella on 31/07/16.
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
