package it.uniparthenope.fairwind.services.tcpipserver;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class NMEA0183TcpIpServerPreferences extends NMEA0183ListenerPreferences {


    private int serverPort=10110;
    public int getServerPort() { return serverPort; }
    public void setServerPort(int serverPort) { this.serverPort=serverPort; }

    public NMEA0183TcpIpServerPreferences() {
        super();
        setName("NMEA0183 Tcp/Ip Server");
        setDesc("Serve NMEA0183 sencences over Tcp/Ip");
    }

    public NMEA0183TcpIpServerPreferences(String name, String desc, int serverPort) {
        super(name, desc);
        this.serverPort=serverPort;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        serverPort=json.at("serverPort",resources.getInteger(R.integer.pref_default_nmea0183tcpipserver_port)).asInteger();

    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("serverPort",serverPort);
        return json;
    }
}
