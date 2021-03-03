package it.uniparthenope.fairwind.sdk.captain.setup.maps;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import it.uniparthenope.fairwind.sdk.R;


/**
 * Created by raffaelemontella on 01/08/16.
 */
public abstract class MapPreferencesDialog extends DialogFragment /*implements TextView.OnEditorActionListener */{
    public final static String LOG_TAG="KK";

    protected MapPreferences mapPreferences;

    public void setMapPreferences(RecyclerView.Adapter adapter, MapPreferences mapPreferences) {
        this.adapter=adapter;
        this.mapPreferences=mapPreferences;
    }

    public abstract int getLayoutId();
    public abstract void onInit();
    public abstract void onFinish();

    protected View view;
    private RecyclerView.Adapter adapter;

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
        builder.setTitle("Map Setup");
        builder.setMessage("Custom layer parameters")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        EditText editName= (EditText)view.findViewById(R.id.edit_name);
                        EditText editDesc= (EditText)view.findViewById(R.id.edit_desc);

                        EditText editSourceUrl= (EditText)view.findViewById(R.id.editText_sourceUrl);
                        EditText editCopyright= (EditText)view.findViewById(R.id.editText_copyright);

                        mapPreferences.setName(editName.getText().toString());
                        mapPreferences.setDesc(editDesc.getText().toString());
                        mapPreferences.setSourceUrl(editSourceUrl.getText().toString());
                        mapPreferences.setCopyright(editCopyright.getText().toString());
                        onFinish();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


        // Create the AlertDialog object and return it

        EditText editName= (EditText)view.findViewById(R.id.edit_name);
        EditText editDesc= (EditText)view.findViewById(R.id.edit_desc);
        EditText editSourceUrl= (EditText)view.findViewById(R.id.editText_sourceUrl);
        EditText editCopyright= (EditText)view.findViewById(R.id.editText_copyright);



        editName.setText(mapPreferences.getName());
        editDesc.setText(mapPreferences.getDesc());
        editSourceUrl.setText(mapPreferences.getSourceUrl());
        editCopyright.setText(mapPreferences.getCopyright());

        /*
        editName.setOnEditorActionListener(this);
        editDesc.setOnEditorActionListener(this);
        editSourceUrl.setOnEditorActionListener(this);
        editCopyright.setOnEditorActionListener(this);


        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(view, 0);
            }
        });

        editDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(view, 0);
            }
        });

        editSourceUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(view, 0);
            }
        });

        editCopyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(view, 0);
            }
        });
*/
        onInit();
        return builder.create();
    }
/*
    @Override
    public boolean onEditorAction(TextView v, int actionId,
                                  KeyEvent event) {
        if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            // Must return true here to consume event
            return true;

        }
        return false;
    }
    */
}
