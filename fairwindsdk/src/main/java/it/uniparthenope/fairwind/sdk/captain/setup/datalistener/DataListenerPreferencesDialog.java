package it.uniparthenope.fairwind.sdk.captain.setup.datalistener;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.app.AlertDialog;


import it.uniparthenope.fairwind.sdk.R;

/**
 * Created by raffaelemontella on 26/07/16.
 */
public abstract class DataListenerPreferencesDialog extends DialogFragment /*implements TextView.OnEditorActionListener */{
    public final static String LOG_TAG="KK";

    private RecyclerView.Adapter adapter;
    private PreferenceFragment preferenceFragment;
    protected DataListenerPreferences dataListenerPreferences;


    public void setDataListenerPreferences(PreferenceFragment preferenceFragment,
            RecyclerView.Adapter adapter, DataListenerPreferences dataListenerPreferences) {
        this.preferenceFragment=preferenceFragment;
        this.adapter=adapter;
        this.dataListenerPreferences=dataListenerPreferences;
        ((DataListenerPreferencesFragment)preferenceFragment).setDataListenerPreferencesDialog(this);
    }




    protected PreferenceFragment getPreferenceFragment() { return preferenceFragment; }
    public abstract int getLayoutId();
    public abstract void onInit();
    public abstract void onFinish();

    protected View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view=inflater.inflate(getLayoutId(),null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int)(displayRectangle.width() * 0.9f));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Data Listener Service Setup");
        builder.setMessage("Service customization")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        EditText editName= (EditText)view.findViewById(R.id.edit_name);
                        EditText editDesc= (EditText)view.findViewById(R.id.edit_desc);

                        dataListenerPreferences.setName(editName.getText().toString());
                        dataListenerPreferences.setDesc(editDesc.getText().toString());
                        onFinish();
                        ((DataListenerPreferencesFragment)preferenceFragment).save();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


        // Create the AlertDialog object and return it

        EditText editName= (EditText)view.findViewById(R.id.edit_name);
        EditText editDesc= (EditText)view.findViewById(R.id.edit_desc);



        editName.setText(dataListenerPreferences.getName());
        editDesc.setText(dataListenerPreferences.getDesc());

        onInit();
        return builder.create();
    }

    public void startActivityForResult(Intent intent, int id) {
        preferenceFragment.startActivityForResult(intent,id);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"XX");
    }
}
