package it.uniparthenope.fairwind.sdk.maps.source.tiledlayer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.GEMFFileArchive;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapOverlay;
import it.uniparthenope.fairwind.sdk.maps.FairWindMapView;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;

/**
 * Created by raffaelemontella on 14/07/16.
 */
public class TiledLayer implements FairWindMapOverlay {
    public static final String LOG_TAG="TILED_LAYER";

    private MapView mapView;
    private FairWindModel fairWindModel;
    private ArrayList<String> urls;
    private LinkedHashMap<String, String> params;
    private TilesOverlay overlay;
    private ITileSource tileSource;

    public TiledLayer() {

    }

    public TiledLayer(MapView mapView, FairWindModel fairWindModel,String name, int minZoom, int maxZoom, int tileSize, String imageEnding, ArrayList<String> urls, LinkedHashMap<String,String> params) {
        init(mapView,fairWindModel,name,minZoom,maxZoom,tileSize,imageEnding, urls,params);
    }

    public TiledLayer(MapView mapView, FairWindModel fairWindModel, TiledLayerPreferences tiledLayerPreferences) {
        init(
                mapView,
                fairWindModel,
                tiledLayerPreferences.getName(),
                tiledLayerPreferences.getMinZoom(),
                tiledLayerPreferences.getMaxZoom(),
                tiledLayerPreferences.getTileSize(),
                tiledLayerPreferences.getImageEnding(),
                tiledLayerPreferences.getUrls(),
                tiledLayerPreferences.getParams()
        );
    }

    private void init(MapView mapView, FairWindModel fairWindModel, String name, int minZoom, int maxZoom, int tileSize, String imageEnding, ArrayList<String> urls, LinkedHashMap<String,String> params) {
        this.mapView=mapView;
        this.fairWindModel=fairWindModel;
        this.urls=urls;
        this.params=params;

        final Context context=mapView.getContext();
        final Context applicationContext = context.getApplicationContext();
        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(applicationContext);

        tileSource = new CustomTileSource(
                name,
                minZoom, maxZoom,
                tileSize, imageEnding,
                urls.toArray(new String[0]),
                params);

        // Create a file cache modular provider
        final TileWriter tileWriter = new TileWriter();

        final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
                registerReceiver, tileSource);

        // Create a download modular tile provider
        final NetworkAvailabliltyCheck networkAvailabliltyCheck = new NetworkAvailabliltyCheck(context);
        final MapTileDownloader downloaderProvider = new MapTileDownloader(
                tileSource, tileWriter, networkAvailabliltyCheck);

        MapTileModuleProviderBase[] mapTileModuleProviderBase=new MapTileModuleProviderBase[]
                {
                    fileSystemProvider,
                        downloaderProvider
                };

        // Create a custom tile provider array with the custom tile source and the custom tile providers
        final MapTileProviderArray tileProvider = new MapTileProviderArray(tileSource, registerReceiver,mapTileModuleProviderBase);

        overlay = new TilesOverlay(tileProvider, context);
        overlay.setLoadingBackgroundColor(Color.TRANSPARENT);
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
        TiledLayerPreferences tiledLayerPreferences=(TiledLayerPreferences)mapPreferences;
        init(
                mapView,
                fairWindModel,
                tiledLayerPreferences.getName(),
                tiledLayerPreferences.getMinZoom(),
                tiledLayerPreferences.getMaxZoom(),
                tiledLayerPreferences.getTileSize(),
                tiledLayerPreferences.getImageEnding(),
                tiledLayerPreferences.getUrls(),
                tiledLayerPreferences.getParams()
        );
    }

    public ITileSource getTileSource() { return  tileSource; }
}
