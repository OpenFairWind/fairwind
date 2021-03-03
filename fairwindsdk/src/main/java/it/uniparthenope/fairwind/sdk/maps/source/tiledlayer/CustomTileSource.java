package it.uniparthenope.fairwind.sdk.maps.source.tiledlayer;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by raffaelemontella on 13/07/16.
 */
public class CustomTileSource extends OnlineTileSourceBase {

    private HashMap<String,String> params;

    //constructor is default - I changed nothing here
    public CustomTileSource (String aName, int aZoomMinLevel, int aZoomMaxLevel,
                             int aTileSizePixels, String aImageFilenameEnding, String[] urls, LinkedHashMap<String,String> params) {
        super(
                aName,
                aZoomMinLevel,
                aZoomMaxLevel,
                aTileSizePixels,
                aImageFilenameEnding,
                urls);
        // TODO Auto-generated constructor stub
        this.params=params;
    }

    /**
     * returns the url for each tile, depending on zoom level
     */
    //this is where I changed the return statement to take the first url from the string array of urls
    @Override
    public String getTileURLString(MapTile aTile) {
        String url=getBaseUrl();
        /*
        String aImageFilenameEnding=imageFilenameEnding();
        if (aImageFilenameEnding!=null && aImageFilenameEnding.isEmpty()==false) {
            url+=aImageFilenameEnding;
        }
        */
        if (params!=null && params.isEmpty()==false) {
            url+="?";
            for(String key:params.keySet()) {
                url+=(key+"="+params.get(key)+"&");
            }
            url=url.substring(0,url.length()-1);
        }
        url=url.replace("{$z}",String.format("%d",aTile.getZoomLevel()));
        url=url.replace("{$x}",String.format("%d",aTile.getX()));
        url=url.replace("{$y}",String.format("%d",aTile.getY()));
        return url;
    }
}