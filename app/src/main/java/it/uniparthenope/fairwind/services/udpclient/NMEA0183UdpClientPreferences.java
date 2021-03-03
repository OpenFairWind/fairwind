package it.uniparthenope.fairwind.services.udpclient;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 01/05/2017.
 */

public class NMEA0183UdpClientPreferences extends NMEA0183ListenerPreferences {

    private String host;
    private int port;

    public String getHost() { return host; }
    public int getPort() { return port; }
    public void setHost(String host) { this.host=host; }
    public void setPort(int port) { this.port=port; }

    public NMEA0183UdpClientPreferences() {
        super();
        setName("NMEA0183 Udp Client");
        setDesc("Ingest NMEA0183 sencences from a Udp server");
    }

    public NMEA0183UdpClientPreferences(String name, String desc, String host, int port) {
        super(name, desc);
        this.host=host;
        this.port=port;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        this.host=json.at("host",resources.getString(R.string.pref_default_nmea0183udpclientdatasource_host)).toString().replace("\"","");
        this.port=json.at("port",resources.getInteger(R.integer.pref_default_nmea0183udpclientdatasource_port)).asInteger();

    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("host",host);
        json.set("port",port);
        return json;
    }
}
