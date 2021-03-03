package it.uniparthenope.fairwind.captain.mydata.waypoints;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.resources.waypoints.Waypoint;
import it.uniparthenope.fairwind.sdk.ui.EditLatitudeLongitude;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 05/10/2017.
 */

public class WaypointDialog  extends DialogFragment {
    public final static String LOG_TAG="METAPREF...DIALOG";

    private FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();

    private WaypointsRecyclerViewAdapter adapter;

    private View mView;

    private Waypoint waypoint;
    public void setWaypoint(Waypoint waypoint) {
        this.waypoint=waypoint;
    }

    public void setAdapter(WaypointsRecyclerViewAdapter adapter) {
        this.adapter=adapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView=inflater.inflate(R.layout.dialog_waypoint,null);
        final EditText editId = (EditText) mView.findViewById(it.uniparthenope.fairwind.sdk.R.id.editText_waypoint_id);
        final EditText editDesc = (EditText) mView.findViewById(it.uniparthenope.fairwind.sdk.R.id.editText_waypoint_desc);
        final EditLatitudeLongitude editLatitudeLongitude=(EditLatitudeLongitude)mView.findViewById(it.uniparthenope.fairwind.sdk.R.id.editLatitudelongitude);

        final Spinner spinnerGroup = (Spinner) mView.findViewById(R.id.spinner_group);
        spinnerGroup.setAdapter(new GroupAdapter(getActivity()));

        final Spinner spinnerType = (Spinner) mView.findViewById(R.id.spinner_type);
        spinnerType.setAdapter(new TypeAdapter(getActivity()));



        // Use the Builder class for convenient dialog construction

        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(mView);
        alertDialog.setTitle("Waypoint");
        alertDialog.setMessage("Waypoint details");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String waypointId=editId.getText().toString();
                        String waypointDesc=editDesc.getText().toString();
                        waypoint.setId(waypointId);
                        waypoint.setDescription(waypointDesc);
                        Position position=editLatitudeLongitude.getPosition();
                        waypoint.setPosition(position);
                        fairWindModel.getWaypoints().add(waypoint);

                        if (adapter!=null) {
                            adapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "Goto",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fairWindModel.getWaypoints().goTo(waypoint);
                        dialog.dismiss();
                    }
                });


        // Create the AlertDialog object and return it

        if (waypoint!=null) {
            editId.setText(Formatter.formatString(waypoint.getId(),"n/a"));
            editDesc.setText(Formatter.formatString(waypoint.getDescription(),"n/a"));
            Position position=waypoint.getPosition();
            editLatitudeLongitude.setPosition(position);
        }



        Button buttonDelete=(Button)mView.findViewById(it.uniparthenope.fairwind.sdk.R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fairWindModel.getWaypoints().remove(waypoint);
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }


}
