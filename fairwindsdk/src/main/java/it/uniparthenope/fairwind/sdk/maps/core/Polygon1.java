package it.uniparthenope.fairwind.sdk.maps.core;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapOverlay;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

/**
 * Created by raffaelemontella on 13/04/2017.
 */

public class Polygon1 extends Polygon implements FairWindMapOverlay {
    public static final String LOG_TAG="POLYGON1";

    protected FairWindModel fairWindModel;
    protected MapView mapView;

    public Polygon1(MapView mapView,FairWindModel fairWindModel) {
        super(mapView.getContext());
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
    }

    @Override
    public void onAdd() {
    }

    @Override
    public void onRemove() {
    }

    @Override
    public void add() {
        mapView.getOverlays().add(this);
        onAdd();
        mapView.invalidate();
    }

    @Override
    public void remove() {
        onRemove();
        mapView.getOverlays().remove(this);
        mapView.invalidate();
    }

    @Override
    public void setMapPreferences(MapView mapView, FairWindModel fairWindModel, MapPreferences mapPreferences) {

    }



}
