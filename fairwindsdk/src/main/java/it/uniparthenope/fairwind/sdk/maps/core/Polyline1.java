package it.uniparthenope.fairwind.sdk.maps.core;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapOverlay;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

/**
 * Created by raffaelemontella on 10/07/16.
 */
public class Polyline1 extends Polyline implements FairWindMapOverlay {
    public static final String LOG_TAG="POLYLINE1";

    protected FairWindModel fairWindModel;
    protected MapView mapView;

    public Polyline1(MapView mapView,FairWindModel fairWindModel) {
        super(mapView.getContext());
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
    }

    public void onAdd() {}
    public void onRemove() {}

    public void add() {
        mapView.getOverlays().add(this);
        onAdd();
        mapView.invalidate();
    }

    public void remove() {
        onRemove();
        mapView.getOverlays().remove(this);
        mapView.invalidate();
    }

    @Override
    public void setMapPreferences(MapView mapView, FairWindModel fairWindModel, MapPreferences mapPreferences) {

    }

}
