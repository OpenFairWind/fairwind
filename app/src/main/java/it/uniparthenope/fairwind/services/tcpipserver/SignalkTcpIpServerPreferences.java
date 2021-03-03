package it.uniparthenope.fairwind.services.tcpipserver;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class SignalkTcpIpServerPreferences extends DataListenerPreferences {

    private int serverPort=55555;


    public int getServerPort() { return serverPort; }

    public void setServerPort(int serverPort) { this.serverPort=serverPort; }

    public SignalkTcpIpServerPreferences(String name, String desc, int serverPort) {
        super(name, desc);
        this.serverPort=serverPort;

    }

    public SignalkTcpIpServerPreferences() {
        super();
        setName("SignalK Tcp/Ip Server");
        setDesc("Serve SignalK data over Tcp/Ip");
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        this.serverPort=json.at("serverPort",resources.getInteger(R.integer.pref_default_signalktcpipserver_port)).asInteger();
    }

    @Override
    public Json asJson() {
        Json json=super.asJson();

        return json;
    }
}
