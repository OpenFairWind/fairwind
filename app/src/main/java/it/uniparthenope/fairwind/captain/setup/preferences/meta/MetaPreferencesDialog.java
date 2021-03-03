package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.model.impl.PreferencesImpl;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Key;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.MetaPreferences;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Method;
import it.uniparthenope.fairwind.sdk.captain.setup.meta.Zone;


/**
 * Created by raffaelemontella on 07/02/2017.
 */

public class MetaPreferencesDialog extends DialogFragment  {
    public final static String LOG_TAG="METAPREF...DIALOG";

    private RecyclerView.Adapter adapter;
    private MetaPreferencesFragment metaPreferencesFragment;
    private MetaPreferences metaPreferences;
    private View view;

    private ArrayList<Zone> zones=new ArrayList<Zone>();
    public ArrayList<Zone> getZones() { return  zones; }

    private String units;

    public MetaPreferences getMetaPreferences() { return metaPreferences; }


    public void setMetaPreferences(MetaPreferencesFragment metaPreferencesFragment,
                                           RecyclerView.Adapter adapter, MetaPreferences metaPreferences) {
        this.metaPreferencesFragment=metaPreferencesFragment;
        this.adapter=adapter;
        this.metaPreferences = metaPreferences;
        metaPreferencesFragment.setMetaPreferencesDialog(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view=inflater.inflate(R.layout.dialog_metapreferences,null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int)(displayRectangle.width() * 0.9f));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Meta Setup");
        builder.setMessage("Define SignaK metadata")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        EditText editPath= (EditText) view.findViewById(R.id.edit_path);
                        EditText editDisplayName= (EditText)view.findViewById(R.id.edit_displayName);
                        EditText editShortName= (EditText)view.findViewById(R.id.edit_shortName);

                        Spinner spinWarnMethod= (Spinner) view.findViewById(R.id.spn_warnMethod);
                        EditText editWarnMessage= (EditText)view.findViewById(R.id.edit_warnMessage);
                        Spinner spinAlarmMethod= (Spinner) view.findViewById(R.id.spn_alarmMethod);
                        EditText editAlarmMessage= (EditText)view.findViewById(R.id.edit_alarmMessage);

                        metaPreferences.setPath(editPath.getText().toString());
                        metaPreferences.setDisplayName(editDisplayName.getText().toString());
                        metaPreferences.setShortName(editShortName.getText().toString());
                        metaPreferences.setWarnMessage(editWarnMessage.getText().toString());
                        metaPreferences.setAlarmMessage(editAlarmMessage.getText().toString());

                        metaPreferences.setWarnMethod(Method.values()[spinWarnMethod.getSelectedItemPosition()]);
                        metaPreferences.setAlarmMethod(Method.values()[spinAlarmMethod.getSelectedItemPosition()]);

                        metaPreferences.setZones(zones);

                        metaPreferences.setUnits(units);

                        metaPreferencesFragment.save();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


        // Create the AlertDialog object and return it
        EditText editPath= (EditText) view.findViewById(R.id.edit_path);

        EditText editDisplayName = (EditText)view.findViewById(R.id.edit_displayName);
        EditText editShortName = (EditText)view.findViewById(R.id.edit_shortName);

        Spinner spinWarnMethod= (Spinner)view.findViewById(R.id.spn_warnMethod);
        EditText editWarnMessage= (EditText)view.findViewById(R.id.edit_warnMessage);
        Spinner spinAlarmMethod= (Spinner)view.findViewById(R.id.spn_alarmMethod);
        EditText editAlarmMessage= (EditText)view.findViewById(R.id.edit_alarmMessage);

        String path=metaPreferences.getPath();
        if (path.endsWith("/meta")) {
            path=path.substring(0,path.lastIndexOf("/")-1);
        }
        editPath.setText(path);
        editDisplayName.setText(metaPreferences.getDisplayName());
        editShortName.setText(metaPreferences.getShortName());
        editWarnMessage.setText(metaPreferences.getWarnMessage());
        editAlarmMessage.setText(metaPreferences.getAlarmMessage());

        spinWarnMethod.setSelection(metaPreferences.getWarnMethod().ordinal());
        spinAlarmMethod.setSelection(metaPreferences.getAlarmMethod().ordinal());

        for(Zone zone:metaPreferences.getZones()) {
            zones.add(new Zone(zone));
        }

        units=metaPreferences.getUnits();

        Button buttonBrowse=(Button)view.findViewById(R.id.button_browse);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeysDialogFragment keysDialogFragment =new KeysDialogFragment();
                keysDialogFragment.setMetaPreferencesDialog(MetaPreferencesDialog.this);
                keysDialogFragment.show(getFragmentManager(),"keysDialogFragment");

            }
        });

        Button buttonSetZones=(Button)view.findViewById(R.id.button_set_zones);
        buttonSetZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZonesDialogFragment zonesDialogFragment =new ZonesDialogFragment();
                zonesDialogFragment.setMetaPreferencesDialog(MetaPreferencesDialog.this);
                zonesDialogFragment.show(getFragmentManager(),"zonesDialogFragment");
            }
        });


        return builder.create();
    }

    public void fetchKey(Key key) {
        EditText editPath= (EditText) view.findViewById(R.id.edit_path);
        editPath.setText(key.getPath());
        EditText editDisplayName=(EditText)view.findViewById(R.id.edit_displayName);
        editDisplayName.setText(key.getDescription());
        units=key.getUnits();
    }

    public void fetchZones(List<Zone> items) {
        zones.clear();
        for(Zone item:items) {
            zones.add(item);
        }
    }
}
