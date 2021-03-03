package it.uniparthenope.fairwind.services.usbserial;

import android.content.res.Resources;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class NMEA0183UsbSerialClientPreferences extends NMEA0183ListenerPreferences {
    private String deviceInfo="";
    private int baudRate=115200;
    private int stopBits= UsbSerialPort.STOPBITS_1;
    private int dataBits=UsbSerialPort.DATABITS_8;
    private int parity=UsbSerialPort.PARITY_NONE;
    private boolean dataTerminalReady=false;
    private boolean requestToSend=false;

    public String getDeviceInfo() { return deviceInfo; }
    public int getBaudRate() { return baudRate; }
    public int getStopBits() { return stopBits; }
    public int getDataBits() { return dataBits; }
    public int getParity() { return  parity; }
    public boolean getDataTerminalReady() { return dataTerminalReady; }
    public boolean getRequestToSend() { return requestToSend; }

    public void setDeviceInfo(String deviceInfo) { this.deviceInfo=deviceInfo; }
    public void setBaudRate(int baudRate) { this.baudRate=baudRate; }
    public void setStopBits(int stopBits) { this.stopBits=stopBits; }
    public void setDataBits(int dataBits) { this.dataBits=dataBits; }
    public void setParity(int parity) { this.parity=parity; }
    public void setDataTerminalReady(boolean dataTerminalReady) { this.dataTerminalReady=dataTerminalReady; }
    public void setRequestToSend(boolean requestToSend) { this.requestToSend=requestToSend; }

    public NMEA0183UsbSerialClientPreferences(String name, String desc, String deviceInfo, int baudRate, int stopBits, int dataBits, int parity, boolean dataTerminalReady, boolean requestToSend ){
        super(name,desc);
        this.deviceInfo=deviceInfo;
        this.baudRate=baudRate;
        this.stopBits=stopBits;
        this.dataBits=dataBits;
        this.parity=parity;
        this.dataTerminalReady=dataTerminalReady;
        this.requestToSend=requestToSend;
    }

    public NMEA0183UsbSerialClientPreferences() {
        super();
        setName("NMEA0183 Usb Serial Client");
        setDesc("Ingest NMEA0183 sentences from a usb serial port");
        this.deviceInfo="";
        this.baudRate=115200;
        this.stopBits=1;
        this.dataBits=8;
        this.parity=0;
        this.dataTerminalReady=false;
        this.requestToSend=false;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        this.deviceInfo=json.at("deviceInfo","").toString().replace("\"","");
        this.baudRate=json.at("baudRate",resources.getString(R.string.pref_default_nmea0183serialdatasource_baudrate)).asInteger();
        this.stopBits=json.at("stopBits",resources.getString(R.string.pref_default_nmea0183serialdatasource_stopbits)).asInteger();
        this.dataBits=json.at("dataBits",resources.getString(R.string.pref_default_nmea0183serialdatasource_databits)).asInteger();
        this.parity=json.at("parity",resources.getString(R.string.pref_default_nmea0183serialdatasource_paritybit)).asInteger();
        this.dataTerminalReady=json.at("dataTerminalReady",false).asBoolean();
        this.requestToSend=json.at("requestToSend",false).asBoolean();
    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("deviceInfo",deviceInfo);
        json.set("baudRate",baudRate);
        json.set("stopBits",stopBits);
        json.set("dataBits",dataBits);
        json.set("parity",parity);
        json.set("dataTerminalReady",dataTerminalReady);
        json.set("requestToSend",requestToSend);
        return json;
    }
}
