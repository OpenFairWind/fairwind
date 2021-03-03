package it.uniparthenope.fairwind.services.signalkclient;

import android.content.res.Resources;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import mjson.Json;

/**
 * Created by raffaelemontella on 22/07/16.
 */
public class SignalkWebSocketClientPreferences extends DataListenerPreferences {

    private String uri;
    private String signalKUuid;
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri=uri; }
    public String getSignalKUuid() { return signalKUuid; }
    public void setUuid(String uuid) { this.signalKUuid=uuid; }

    public SignalkWebSocketClientPreferences(String name, String desc, String uri,String signalKUuid) {
        super(name, desc);
        this.uri=uri;
        this.signalKUuid=signalKUuid;
    }

    public SignalkWebSocketClientPreferences() {
        super();
        setName("SignalK Web Socket Client");
        setDesc("Serve SignalK data as web socket");
    }

    @Override
    public void byJson(Json json, Resources resources) {
        super.byJson(json,resources);
        String defaultUri=resources.getString(R.string.pref_default_signalkwebsocketclient_uri);
        uri=json.at("uri",defaultUri).toString().replace("\"","");
        signalKUuid=json.at("signalk_uuid","").toString().replace("\"","");

    }

    @Override
    public Json asJson() {
        Json json=super.asJson();
        json.set("uri",uri);
        json.set("signalk_uuid",signalKUuid);
        return json;
    }
}
