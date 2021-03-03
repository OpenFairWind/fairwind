package it.uniparthenope.fairwind.services.udpserver;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 01/05/2017.
 */

public class NMEA0183UdpServerPreferences extends NMEA0183ListenerPreferences {


    private int serverPort=2000;
    public int getServerPort() { return serverPort; }
    public void setServerPort(int serverPort) { this.serverPort=serverPort; }

    public NMEA0183UdpServerPreferences() {
        super();
        setName("NMEA0183 Udp Server");
        setDesc("Serve NMEA0183 sencences over Udp");
    }

    public NMEA0183UdpServerPreferences(String name, String desc, int serverPort) {
        super(name, desc);
        this.serverPort=serverPort;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        serverPort=json.at("serverPort",resources.getInteger(R.integer.pref_default_nmea0183udpserver_port)).asInteger();

    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("serverPort",serverPort);
        return json;
    }
}
