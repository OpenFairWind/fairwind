package it.uniparthenope.fairwind.services.web;

import android.util.Log;

import java.io.IOException;
import java.util.Vector;

import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 24/11/15.
 */
public class SignalkWebSockets extends Vector<SignalkWebSocket> {
    public final static String LOG_TAG="SIGNALK_WEBSOCKETS";

    private long lastUpdate=Long.MIN_VALUE;
    public long getLastUpdate() { return lastUpdate; }

    public void update(PathEvent pathEvent)  {
        Log.d(LOG_TAG,"update:"+pathEvent.getPath());
        for(SignalkWebSocket signalkWebSocket:this) {

            try {
                signalkWebSocket.update(pathEvent);
            } catch (IOException e) {
                this.remove(signalkWebSocket);
            }
        }
        lastUpdate=System.currentTimeMillis();
    }
}