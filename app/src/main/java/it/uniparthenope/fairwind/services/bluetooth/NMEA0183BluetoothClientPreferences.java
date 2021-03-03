package it.uniparthenope.fairwind.services.bluetooth;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 23/07/16.
 */
public class NMEA0183BluetoothClientPreferences extends NMEA0183ListenerPreferences {

    private String paireDevice="";
    private String deviceConnected="";


    public String getPaireDevice() { return paireDevice; }
    public void setPaireDevice(String paireDevice) { this.paireDevice=paireDevice; }

    public String getDeviceConnected() { return deviceConnected; }
    public void setDeviceConnected(String deviceConnected) { this.deviceConnected=deviceConnected; }

    public NMEA0183BluetoothClientPreferences() {
        super();
        setName("NMEA0183 Bluetooth Client");
        setDesc("Connect to external Bluetooth GPS");
    }

    public NMEA0183BluetoothClientPreferences(String name, String desc, String address, String paireDevice, String deviceConnected) {
        super(name, desc);
        this.paireDevice=paireDevice;
        this.deviceConnected=deviceConnected;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        String defaultPaireDevice=resources.getString(R.string.perf_default_nmea0183bluetooth_paire_device);
        this.paireDevice=json.at("paireDevice",defaultPaireDevice).toString().replace("\"","");
        this.deviceConnected=json.at("deviceConnected",resources.getString(R.string.perf_default_nmea0183bluetooth_device_connected)).toString().replace("\"","");
    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("paireDevice",paireDevice);
        json.set("deviceConnected",deviceConnected);
        return json;
    }
}
