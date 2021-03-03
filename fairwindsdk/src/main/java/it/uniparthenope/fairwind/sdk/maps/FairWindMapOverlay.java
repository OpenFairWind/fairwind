package it.uniparthenope.fairwind.sdk.maps;

import org.osmdroid.views.MapView;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;


/**
 * Created by raffaelemontella on 10/07/16.
 */
public interface FairWindMapOverlay {
    public void onAdd();
    public void onRemove();

    public void add();
    public void remove();

    void setMapPreferences(MapView mapView, FairWindModel fairWindModel, MapPreferences mapPreferences);
}
