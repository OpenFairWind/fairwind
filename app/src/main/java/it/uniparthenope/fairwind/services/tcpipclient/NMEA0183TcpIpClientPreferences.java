package it.uniparthenope.fairwind.services.tcpipclient;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class NMEA0183TcpIpClientPreferences extends NMEA0183ListenerPreferences {

    private String host;
    private int port;

    public String getHost() { return host; }
    public int getPort() { return port; }
    public void setHost(String host) { this.host=host; }
    public void setPort(int port) { this.port=port; }

    public NMEA0183TcpIpClientPreferences() {
        super();
        setName("NMEA0183 Tcp/Ip Client");
        setDesc("Ingest NMEA0183 sencences from a Tcp/Ip server");
    }

    public NMEA0183TcpIpClientPreferences(String name, String desc, String host, int port) {
        super(name, desc);
        this.host=host;
        this.port=port;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        this.host=json.at("host",resources.getString(R.string.pref_default_nmea0183tcpipclientdatasource_host)).toString().replace("\"","");
        this.port=json.at("port",resources.getInteger(R.integer.pref_default_nmea0183tcpipclientdatasource_port)).asInteger();

    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("host",host);
        json.set("port",port);
        return json;
    }
}
