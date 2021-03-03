package it.uniparthenope.fairwind.services.file;

import android.content.res.Resources;

import java.io.File;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.services.NMEA0183ListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class NMEA0183PlayerPreferences extends NMEA0183ListenerPreferences {

    private String fileName="";
    private int millis=500;

    public String getFileName() { return  fileName; }
    public int getMillis() { return millis; }
    public void setFileName(String fileName) { this.fileName=fileName; }
    public void setMillis(int millis) { this.millis=millis; }

    public NMEA0183PlayerPreferences() {
        super();
        setName("NMEA0183 Player");
        setDesc("Playback a NMEA0183 log file");
        File externalFilesDir= FairWindApplication.getInstance().getExternalFilesDir(null);
        String defaultFileName=externalFilesDir.getAbsolutePath()+File.separator+"nmea"+File.separator+"demo.nmea";
        this.fileName=defaultFileName;
        this.millis=500;
    }

    public NMEA0183PlayerPreferences(String name, String desc, String fileName, int millis) {
        super(name, desc);
        this.fileName=fileName;
        this.millis=millis;
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        File externalFilesDir= FairWindApplication.getInstance().getExternalFilesDir(null);
        String defaultFileName=externalFilesDir.getAbsolutePath()+File.separator+"nmea"+File.separator+"demo.nmea";
        fileName=json.at("fileName",defaultFileName).asString().replace("\"","");
        millis=json.at("millis",resources.getInteger(R.integer.pref_default_nmea0183playerdatasource_millis)).asInteger();
    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("fileName",fileName);
        json.set("millis",millis);
        return json;
    }
}
