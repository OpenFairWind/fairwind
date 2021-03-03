package it.uniparthenope.fairwind.sdk.maps.core;

import android.graphics.drawable.Drawable;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.IconOverlay;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapOverlay;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

/**
 * Created by raffaelemontella on 14/04/2017.
 */

public class IconOverlay1 extends IconOverlay implements FairWindMapOverlay {
    public static final String LOG_TAG="ICONOVERLAY1";

    protected FairWindModel fairWindModel;
    protected MapView mapView;

    public IconOverlay1(MapView mapView, FairWindModel fairWindModel, IGeoPoint position, Drawable icon) {
        super(position,icon);
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
