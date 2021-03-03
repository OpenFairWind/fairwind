package it.uniparthenope.fairwind.services.web;

import android.util.Log;


import fi.iki.elonen.NanoWSD;
import it.uniparthenope.fairwind.FairWindApplication;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by raffaelemontella on 05/09/15.
 */
public class WebSocketWorker extends NanoWSD implements Runnable {
    // The Log tag
    private static final String LOG_TAG = "WEBSOCKET_WORKER";

    private int period=250;
    private boolean done=false;

    private ExecutorService mMockExecutor;

    private WebServer webServer;
    private SignalkWebSockets signalkWebSockets;
    SignalkWebSockets getSignalkWebSockets() { return signalkWebSockets; }

    public void update(PathEvent pathEvent) {
        if (signalkWebSockets.size()>0) {
            Log.d(LOG_TAG, "update:"+pathEvent.getPath());
            SignalKModel signalKModel = (SignalKModel) FairWindApplication.getFairWindModel();

            if (signalKModel.getData() != null && !signalKModel.getData().isEmpty() && signalkWebSockets != null && signalkWebSockets.size() > 0) {
                signalkWebSockets.update(pathEvent);
            }
        }
    }


    public WebSocketWorker(WebServer webServer, int port) {
        super(port);
        this.webServer = webServer;
        signalkWebSockets=new SignalkWebSockets();
        Log.d(LOG_TAG,"Created");
    }

    @Override
    public void run() {
        while(!done) {
                //What happens now?
                //update();
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG,e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        SignalkWebSocket signalkWebSocket=null;
        String uri=handshake.getUri();
        Log.d(LOG_TAG,"Handshaking with:"+uri);
        if (uri.startsWith(SignalKConstants.SIGNALK_WS)) {
            signalkWebSocket = new SignalkWebSocket(this, handshake);
            if (signalkWebSocket!=null) {
                signalkWebSockets.add(signalkWebSocket);
                Log.d(LOG_TAG, "openWebSocket");
                mMockExecutor = Executors.newSingleThreadExecutor();
                mMockExecutor.submit(this);
            } else {
                Log.d(LOG_TAG, "no openWebSocket");
            }
        } else {
            Log.d(LOG_TAG, "no correct url");
        }
        return signalkWebSocket;
    }


}
