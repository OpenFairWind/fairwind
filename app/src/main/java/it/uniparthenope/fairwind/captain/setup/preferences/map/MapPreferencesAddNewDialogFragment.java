package it.uniparthenope.fairwind.captain.setup.preferences.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.source.groundoverlaylayer.GroundOverlayLayerPreferences;
import it.uniparthenope.fairwind.sdk.maps.source.kmllayer.KmlLayerPreferences;
import it.uniparthenope.fairwind.sdk.maps.source.tiledgeojsonlayer.TiledGeoJsonLayerPreferences;
import it.uniparthenope.fairwind.sdk.maps.source.tiledlayer.TiledLayerPreferences;

/**
 * Created by raffaelemontella on 12/09/16.
 */
public class MapPreferencesAddNewDialogFragment extends DialogFragment {
    public static final String LOG_TAG="...ADDNEW...FRAGMENT";

    private MapPreferencesFragment mapPreferencesFragment;

    public MapPreferencesAddNewDialogFragment() {

    }

    public void setMapPreferencesFragment(MapPreferencesFragment mapPreferencesFragment) {
        this.mapPreferencesFragment=mapPreferencesFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Map Layer");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                builder.getContext(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Tiled Bitmap (Pyramid)");
        arrayAdapter.add("Tiled GeoJson");
        arrayAdapter.add("Ground Overlay");
        arrayAdapter.add("KML/Shapefile");

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MapPreferences mapPreferences = null;
                switch (which) {
                    case 0:
                        mapPreferences=new TiledLayerPreferences();
                        break;

                    case 1:
                        mapPreferences=new TiledGeoJsonLayerPreferences();
                        break;

                    case 2:
                        mapPreferences=new GroundOverlayLayerPreferences();
                        break;

                    case 3:
                        mapPreferences=new KmlLayerPreferences();
                        break;

                }
                if (mapPreferences!=null) {
                    Log.d(LOG_TAG,"New className: " + mapPreferences.getClassName());
                    mapPreferencesFragment.addItem(mapPreferences);

                }
                dialog.dismiss();
            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }});

        // Create the AlertDialog object and return it
        return builder.create();

    }
}
