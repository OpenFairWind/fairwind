package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import java.util.ArrayList;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.captain.setup.helper.SimpleItemTouchHelperCallback;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Zone;

/**
 * Created by raffaelemontella on 09/02/2017.
 */

public class ZonesDialogFragment extends DialogFragment {
    public static final String LOG_TAG="...ADDNEW...FRAGMENT";

    private MetaPreferencesDialog metaPreferencesDialog;
    private ZonesContent zonesContent;
    private ZonesRecyclerViewAdapter zonesRecyclerViewAdapter;

    public ZonesDialogFragment() {

    }

    public void setMetaPreferencesDialog(MetaPreferencesDialog metaPreferencesDialog) {
        this.metaPreferencesDialog = metaPreferencesDialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        zonesContent=new ZonesContent(metaPreferencesDialog.getZones());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View zonesView = inflater.inflate(R.layout.dialog_zones, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        zonesView.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        zonesView.setMinimumHeight((int)(displayRectangle.height() * 0.9f));


        final RecyclerView recyclerView=(RecyclerView)zonesView.findViewById(R.id.list_zones);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Zones");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setView(zonesView);

        zonesRecyclerViewAdapter = new ZonesRecyclerViewAdapter(this, zonesContent.getItems());

        recyclerView.setLayoutManager(new LinearLayoutManager(builder.getContext()));
        recyclerView.setAdapter(zonesRecyclerViewAdapter);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(zonesRecyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        ImageButton addButton=(ImageButton)zonesView.findViewById(R.id.add_zone_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Zone zone=new Zone();
                zonesContent.addItem(zone);
                zonesRecyclerViewAdapter.notifyDataSetChanged();
            }
        });



        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                metaPreferencesDialog.fetchZones(zonesContent.getItems());
                dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }});

        // Create the AlertDialog object and return it
        Dialog dialog=builder.create();
        return dialog;

    }

    public void onListFragmentInteraction(Zone item) {

    }
}
