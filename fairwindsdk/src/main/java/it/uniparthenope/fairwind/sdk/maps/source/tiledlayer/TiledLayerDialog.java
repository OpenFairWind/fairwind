package it.uniparthenope.fairwind.sdk.maps.source.tiledlayer;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferencesDialog;

/**
 * Created by raffaelemontella on 06/08/16.
 */
public class TiledLayerDialog extends MapPreferencesDialog {
    /*
    public TiledLayerDialog(Context context) {
        super(context);
    }
    */

    @Override
    public int getLayoutId() {
        return R.layout.dialog_tiledlayer;
    }

    @Override
    public void onInit() {
        TiledLayerPreferences tiledLayerPreferences =(TiledLayerPreferences)mapPreferences;
        Spinner spinnerTileSize=(Spinner)view.findViewById(R.id.spinner_tileSize);
        Spinner spinnerMinZoom= (Spinner)view.findViewById(R.id.spinner_minZoom);
        Spinner spinnerMaxZoom= (Spinner)view.findViewById(R.id.spinner_maxZoom);

        spinnerMinZoom.setSelection(tiledLayerPreferences.getMinZoom());
        spinnerMaxZoom.setSelection(tiledLayerPreferences.getMaxZoom());
        //RecyclerView recyclerViewUrls=(RecyclerView)view.findViewById(R.id.list_urls);
        //RecyclerView recyclerViewParams=(RecyclerView)view.findViewById(R.id.list_params);


        EditText editTextUrls=(EditText)view.findViewById(R.id.editText_urls);
        EditText editTextParams=(EditText)view.findViewById(R.id.editText_params);

        String compareValue=String.format("%d", tiledLayerPreferences.getTileSize());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.tileSize, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTileSize.setAdapter(adapter);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            spinnerTileSize.setSelection(spinnerPosition);
        }

        String urls="";
        for (String url: tiledLayerPreferences.getUrls()) {
            urls+=url+"\n";
        }
        editTextUrls.setText(urls);

        String params="";
        for (String key: tiledLayerPreferences.getParams().keySet()) {
            params+=key+"="+ tiledLayerPreferences.getParams().get(key)+"\n";
        }
        editTextParams.setText(params);
    }

    @Override
    public void onFinish() {
        TiledLayerPreferences tiledLayerPreferences =(TiledLayerPreferences)mapPreferences;
        Spinner spinnerTileSize=(Spinner)view.findViewById(R.id.spinner_tileSize);
        Spinner spinnerMinZoom= (Spinner)view.findViewById(R.id.spinner_minZoom);
        Spinner spinnerMaxZoom= (Spinner)view.findViewById(R.id.spinner_maxZoom);

        tiledLayerPreferences.setMinZoom(spinnerMinZoom.getSelectedItemPosition());
        tiledLayerPreferences.setMaxZoom(spinnerMaxZoom.getSelectedItemPosition());


        //RecyclerView recyclerViewUrls=(RecyclerView)view.findViewById(R.id.list_urls);
        //RecyclerView recyclerViewParams=(RecyclerView)view.findViewById(R.id.list_params);
        EditText editTextUrls=(EditText)view.findViewById(R.id.editText_urls);
        EditText editTextParams=(EditText)view.findViewById(R.id.editText_params);

        tiledLayerPreferences.setTileSize(Integer.parseInt(spinnerTileSize.getSelectedItem().toString()));
        tiledLayerPreferences.getUrls().clear();
        String[] urls=editTextUrls.getText().toString().split("\n");
        for (String url:urls) {
            tiledLayerPreferences.getUrls().add(url);
        }
        String[] params=editTextParams.getText().toString().split("\n");
        for (String param:params) {
            String[] parts=param.split("&");
            if (parts.length>0) {
                for (String part : parts) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length==2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        tiledLayerPreferences.getParams().put(key, value);
                    }
                }
            } else {
                String[] keyValue = param.split("=");
                String key = keyValue[0];
                String value = keyValue[1];
                tiledLayerPreferences.getParams().put(key, value);
            }
        }

    }
}
