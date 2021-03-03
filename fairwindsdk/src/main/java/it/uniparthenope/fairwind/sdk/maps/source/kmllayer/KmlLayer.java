package it.uniparthenope.fairwind.sdk.maps.source.kmllayer;

import android.content.Context;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapOverlay;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

/**
 * Created by raffaelemontella on 11/08/16.
 */
public class KmlLayer  implements FairWindMapOverlay {
    public static final String LOG_TAG = "KmlLayer";

    private MapView mapView;
    private FairWindModel fairWindModel;
    private String url;
    private FolderOverlay overlay;

    public KmlLayer() {

    }

    @Override
    public void onAdd() {

    }

    @Override
    public void onRemove() {

    }

    @Override
    public void add() {
        mapView.getOverlays().add(overlay);
        onAdd();
        mapView.invalidate();
    }

    @Override
    public void remove() {
        onRemove();
        mapView.getOverlays().remove(overlay);
        mapView.invalidate();
    }

    @Override
    public void setMapPreferences(MapView mapView, FairWindModel fairWindModel, MapPreferences mapPreferences) {
        KmlLayerPreferences tiledLayerPreferences=(KmlLayerPreferences)mapPreferences;
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
        Context context=mapView.getContext();
        KmlDocument kmlDocument = new KmlDocument();
        kmlDocument.parseKMLUrl(url);
        overlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument);
    }
}