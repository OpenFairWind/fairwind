package it.uniparthenope.fairwind.services.logger;

import android.content.res.Resources;

import java.io.File;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class LoggerListenerPreferences extends DataListenerPreferences {

    private String boardLogPath="";
    private boolean upload=false;
    private long cutMillis=600000;
    private long fullMillis=60000;

    public String getBoardLogPath() { return boardLogPath; }
    public void setBoardLogPath(String boardLogPath) { this.boardLogPath=boardLogPath; }

    public boolean getUpload() { return upload; }
    public void setUpload(boolean upload) { this.upload=upload; }

    public long getCutMillis() { return cutMillis; }
    public void setCutMillis(long cutMillis) { this.cutMillis=cutMillis; }

    public long getFullMillis() { return fullMillis; }
    public void setFullMillis(long fullMillis) { this.fullMillis=fullMillis; }


    public LoggerListenerPreferences(String name, String desc, boolean upload, String boardLogPath, long cutMillis, long fullMillis) {
        super(name, desc);
        this.upload=upload;
        this.boardLogPath=boardLogPath;
        this.cutMillis=cutMillis;
        this.fullMillis=fullMillis;
    }

    public LoggerListenerPreferences() {
        super();
        setName("Logger Listener");
        setDesc("Manage local and cloud data logging");


        File externalFilesDir= FairWindApplication.getInstance().getExternalFilesDir(null);
        String defaultLogPath=externalFilesDir.getAbsolutePath()+File.separator+"logs";
        this.upload=false;
        this.boardLogPath=defaultLogPath;

    }
    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        File externalFilesDir= FairWindApplication.getInstance().getExternalFilesDir(null);
        String defaultLogPath=externalFilesDir.getAbsolutePath()+File.separator+"logs";
        this.upload=json.at("upload",false).asBoolean();
        this.boardLogPath=json.at("boardLogPath",defaultLogPath).asString().replace("\"","");
        this.cutMillis=json.at("cutMillis", 600000).asLong();
        this.fullMillis=json.at("fullMillis",60000).asLong();

    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("upload",upload);
        json.set("boardLogPath",boardLogPath);
        json.set("cutMillis",cutMillis);
        json.set("fullMillis",fullMillis);
        return json;
    }
}
