package it.uniparthenope.fairwind.sdk.maps.source.tiledlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 17/07/16.
 */
public class TiledLayerPreferences extends MapPreferences {

    private ArrayList<String> urls=new ArrayList<String>();
    private LinkedHashMap<String,String> params=new LinkedHashMap<String, String>();

    private int tileSize=256;
    private int minZoom=0;
    private int maxZoom=19;
    private String imageEnding=".png";



    public TiledLayerPreferences() {
        super();
        setName("Tiled Layer");
        setDesc("Overlay a tiled layer on the map");
    }

    public TiledLayerPreferences(String name, String url) {
        super(name);
        urls.add(url);
    }

    public TiledLayerPreferences(String name, int minZoom, int maxZoom, int tileSize, String imageEnding, ArrayList<String> urls, LinkedHashMap<String,String> params) {
        super(name);
        this.minZoom=minZoom;
        this.maxZoom=maxZoom;
        this.tileSize=tileSize;
        this.urls=urls;
        this.params=params;
        this.imageEnding=imageEnding;
    }


    public TiledLayerPreferences(Json json) {
        super(json);
    }

    public void byJson(Json json) {
        super.byJson(json);

        this.tileSize=json.at("tileSize",256).asInteger();
        this.minZoom=json.at("minZoom",0).asInteger();
        this.maxZoom=json.at("maxZoom",20).asInteger();
        this.imageEnding=json.at("imageEnding",".png").toString().replace("\"","");


        if (json.at("urls")!=null) {
            List<Json> listUrls = json.at("urls").asJsonList();
            if (listUrls != null && listUrls.isEmpty() == false) {
                this.urls = new ArrayList<String>();
                for (Json itemUrl : listUrls) {
                    this.urls.add(itemUrl.toString().replace("\"",""));
                }
            }
        }
        if (json.at("params")!=null) {
            List<Json> listParams = json.at("params").asJsonList();
            if (listParams != null && listParams.isEmpty() == false) {
                this.params = new LinkedHashMap<String, String>();
                for (Json itemParam : listParams) {
                    Map<String,Json> mapParam=itemParam.asJsonMap();
                    String key= (String) mapParam.keySet().toArray()[0];
                    String value=mapParam.get(key).toString().replace("\"","");
                    this.params.put(key,value);
                }
            }
        }
    }



    public ArrayList<String> getUrls() { return urls; }
    public LinkedHashMap<String, String> getParams() { return params;}
    public int getTileSize() { return tileSize; }
    public int getMinZoom() { return minZoom; }
    public int getMaxZoom() { return maxZoom; }
    public String getImageEnding() { return this.imageEnding; }

    public void setUrls(ArrayList<String> urls) { this.urls=urls; }
    public void setParams(LinkedHashMap<String,String> params) { this.params=params; }

    public void setTileSize(int tileSize) { this.tileSize=tileSize; }
    public void setMinZoom(int minZoom) { this.minZoom=minZoom; }
    public void setMaxZoom(int maxZoom) { this.maxZoom=maxZoom; }
    public void setImageEnding(String imageEnding) { this.imageEnding=imageEnding; }


    public Json asJson() {
        Json json=super.asJson();
        json.set("tileSize",tileSize);
        json.set("minZoom", minZoom);
        json.set("maxZoom", maxZoom);
        json.set("imageEnding",imageEnding);

        if (urls!=null && urls.isEmpty()==false) {
            Json arrayUrls = Json.array();
            for (String url : urls) {
                arrayUrls.add(url);
            }
            json.set("urls", arrayUrls);
        }
        if (params!=null && params.isEmpty()==false) {
            Json arrayParams = Json.array();
            for (String key : params.keySet()) {
                Json jsonParam = Json.object();
                jsonParam.set(key, params.get(key));
                arrayParams.add(jsonParam);
            }
            json.set("params", arrayParams);
        }
        return json;
    }


}