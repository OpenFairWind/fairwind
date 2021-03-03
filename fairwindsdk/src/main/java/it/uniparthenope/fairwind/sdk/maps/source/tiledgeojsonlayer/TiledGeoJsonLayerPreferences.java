package it.uniparthenope.fairwind.sdk.maps.source.tiledgeojsonlayer;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;


/**
 * Created by raffaelemontella on 22/08/16.
 */
public class TiledGeoJsonLayerPreferences extends MapPreferences {

    public TiledGeoJsonLayerPreferences() {
        super();
        setName("Tiled GeoJson Layer");
        setDesc("Overlay a tiled GeoJson layer on the map");
    }
}
