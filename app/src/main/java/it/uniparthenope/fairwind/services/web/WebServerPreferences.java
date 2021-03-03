package it.uniparthenope.fairwind.services.web;

import android.content.res.Resources;

import java.io.File;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class WebServerPreferences extends DataListenerPreferences {

    private int portWebService=8080;
    private int portWebSocket=3000;
    private String wwwDocumentRoot="";

    public int getPortWebService() { return portWebService; }
    public int getPortWebSocket() { return portWebSocket; }
    public String getWwwDocumentRoot() { return wwwDocumentRoot; }

    public void setPortWebService(int portWebService) { this.portWebService=portWebService; }
    public void setPortWebSocket(int portWebSocket) { this.portWebSocket=portWebSocket; }
    public void setWwwDocumentRoot(String wwwDocumentRoot) {this.wwwDocumentRoot=wwwDocumentRoot; }

    public WebServerPreferences(String name, String desc, int portWebService, int portWebSocket) {
        super(name, desc);
        this.portWebService=portWebService;
        this.portWebSocket=portWebSocket;
    }

    public WebServerPreferences() {
        super();
        setName("Web Server");
        setDesc("Local web server, services and SignalK web socket server");
        this.portWebService=8080;
        this.portWebSocket=3000;
        File externalFilesDir= FairWindApplication.getInstance().getExternalFilesDir(null);
        String extStorageDirectory=externalFilesDir.getAbsolutePath()+File.separator;
        this.wwwDocumentRoot=extStorageDirectory+"www"+File.separator;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        this.portWebService=json.at("portWebService",resources.getInteger(R.integer.pref_default_webserver_portrest)).asInteger();
        this.portWebSocket=json.at("portWebSocket",resources.getInteger(R.integer.pref_default_webserver_portws)).asInteger();
        this.wwwDocumentRoot=json.at("wwwDocumentRoot","").toString().replace("\"","");
    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("portWebService",portWebService);
        json.set("portWebSocket",portWebSocket);
        json.set("wwwDocumentRoot",wwwDocumentRoot);
        return json;
    }
}
