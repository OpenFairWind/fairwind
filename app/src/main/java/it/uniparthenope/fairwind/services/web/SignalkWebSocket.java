package it.uniparthenope.fairwind.services.web;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.handler.FullToMapConverter;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import static it.uniparthenope.fairwind.services.web.Subscription.POLICY_FIXED;
import static it.uniparthenope.fairwind.services.web.Subscription.POLICY_IDEAL;
import static it.uniparthenope.fairwind.services.web.Subscription.POLICY_INSTANT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PATH;
import static nz.co.fortytwo.signalk.util.SignalKConstants.UPDATES;
import static nz.co.fortytwo.signalk.util.SignalKConstants.attr;
import static nz.co.fortytwo.signalk.util.SignalKConstants.meta;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sourceRef;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;

/**
 * Created by raffaelemontella on 24/11/15.
 */
public class SignalkWebSocket extends NanoWSD.WebSocket   {
    private static final String LOG_TAG = "SIGNALK_WEBSOCKET";


    public final static int FORMAT_FULL=0;
    public final static int FORMAT_DELTA=1;


    private int period=250;
    private int format=FORMAT_DELTA;
    private int minPeriod=125;
    private int policy=POLICY_FIXED;


    public int getFormat() { return format; }


    private String context= SignalKConstants.vessels_dot_self;


    private final WebSocketWorker server;


    private boolean requireSubscription=false;
    private Subscriptions subscriptions;
    private long lastUpdate;
    public long getLastUpdate() { return lastUpdate; }

    private HashSet<String> pathEvents=new HashSet<>();

    public SignalkWebSocket(WebSocketWorker server, NanoHTTPD.IHTTPSession handshakeRequest)  {
        super(handshakeRequest);
        this.server = server;
        this.subscriptions=new Subscriptions(this);
        this.lastUpdate=System.currentTimeMillis();
        Log.d(LOG_TAG,"Created:"+handshakeRequest.getUri());
    }



    public void update(PathEvent pathEvent) throws IOException {
        Log.d(LOG_TAG,"Update:"+pathEvent.getPath());
        long millis=System.currentTimeMillis();
        //Is the subscription protocol active?
        if (requireSubscription==false) {
            Log.d(LOG_TAG,"NO subscription");
            // No, just send a full or an update
            JsonSerializer jsonSerializer = new JsonSerializer();

            String jsonString;

            FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();

            if (format == SignalkWebSocket.FORMAT_DELTA) {
                Log.d(LOG_TAG,"Delta");
                pathEvents.add(pathEvent.getPath().substring(0,pathEvent.getPath().lastIndexOf(".")));
                if (
                        (policy==POLICY_FIXED && (millis-lastUpdate>period)) ||
                                (policy==POLICY_INSTANT && (millis-lastUpdate>minPeriod )) ||
                                (policy==POLICY_IDEAL && (millis-lastUpdate>minPeriod ))
                        ) {
                    Log.d(LOG_TAG,"Prepare to send");
                    FullToDeltaConverter fullToDeltaConverter = new FullToDeltaConverter();
                    SignalKModel temp = Utils.getSubTreeByKeys(pathEvents);
                    if (temp!=null) {
                        Log.d(LOG_TAG,"Sending");
                        Json jsonFull = jsonSerializer.writeJson(temp);

                        List<Json> jsonDeltas = fullToDeltaConverter.handle(jsonFull);
                        if (jsonDeltas != null && !jsonDeltas.isEmpty()) {
                            for (Json jsonDelta : jsonDeltas) {
                                if (jsonDelta.at("updates").asJsonList().size()>0) {
                                    Utils.fixSource(jsonDelta);
                                    jsonString = jsonDelta.toString();
                                    Log.d(LOG_TAG, "S:delta " + jsonString);
                                    this.send(jsonString);
                                }
                            }
                        }
                    }
                    pathEvents.clear();
                    lastUpdate = millis;
                } else if ((millis-lastUpdate>1000)) {
                    Json jsonDelta=getHeartBeatDelta();
                    jsonString = jsonDelta.toString();
                    Log.d(LOG_TAG, "S:hearthbeat " + jsonString);
                    this.send(jsonString);
                    lastUpdate = millis;

                }
            } else {
                Log.d(LOG_TAG,"Full");
                if (millis-lastUpdate>1000) {
                    Json jsonFull = jsonSerializer.writeJson(fairWindModel);
                    if (jsonFull != null) {
                        jsonString = jsonFull.toString();
                        Log.d(LOG_TAG, "S:full " + jsonString);
                        this.send(jsonString);
                        lastUpdate = millis;
                    }
                }
            }
        } else if (requireSubscription==true && subscriptions.size()>0) {
            Log.d(LOG_TAG,"Subscription");
            subscriptions.update(pathEvent);
        } else {
            if (millis-lastUpdate>1000) {
                this.send("{ \"context\": \"vessels\",\"updates\": []}");
                lastUpdate = millis;
            }
        }
    }



    @Override
    protected void onOpen(){
        String uriString = this.getHandshakeRequest().getUri();
        Map<String,String> params=this.getHandshakeRequest().getParms();
        if (params.get("format")!=null) {
            if (params.get("format").equals("delta")) {
                format=FORMAT_DELTA;
            } else {
                format=FORMAT_FULL;
            }
        }
        if (params.get("context")!=null) {
            context=params.get("context");
        }
        if(params.get("requireSubscriptions")!=null) {
            if (params.get("requireSubscriptions").equals("true")) {
                requireSubscription = true;
            }
        }
        Log.d(LOG_TAG,"onOpen -> ["+uriString+"]");
        DateTime dateTimeNow= new DateTime(DateTimeZone.UTC);
        String jsonString="{\"name\":\"signalk-server\",\"version\":\"0.0.1\",\"timestamp\":\""+dateTimeNow+"\",\"self\":\""+SignalKConstants.self+"\",\"roles\":[\"master\",\"main\"]}";
        try {
            this.send(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        Log.d(LOG_TAG,"onClose -> [" + (initiatedByRemote ? "Remote" : "Self") + "] " + (code != null ? code : "UnknownCloseCode[" + code + "]")
                + (reason != null && !reason.isEmpty() ? ": " + reason : ""));

        if(subscriptions.size()>0) {
            for (Subscription subscription : subscriptions.values()) {
                subscriptions.remove(subscription.getContext()+subscription.getPath());
                Log.i(LOG_TAG, "subscription "+subscription.getPath()+" removed");
            }
        }
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
        Log.d(LOG_TAG," onMessage ->"+message.getTextPayload());

        if(requireSubscription) {
            Json json = Json.read(message.getTextPayload());
            if (json.has("context") && (json.has("subscribe") || json.has("unsubscribe"))) {
                try {
                    Log.d(LOG_TAG, "subscription arrived: "+json);
                    try {
                        subscriptions.parse(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SubscriptionException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) {
        Log.d(LOG_TAG, "P " + pong);

    }

    @Override
    protected void onException(IOException exception) {
        Log.d(LOG_TAG, "exception occured:"+exception);
    }

    @Override
    protected void debugFrameReceived(NanoWSD.WebSocketFrame frame) {
        Log.d(LOG_TAG, "DEBUGFRAME R " + frame);

    }

    @Override
    protected void debugFrameSent(NanoWSD.WebSocketFrame frame) {
        Log.d(LOG_TAG, "DEBUGFRAME S " + frame);

    }


    private Json getHeartBeatDelta(){
        Json delta = Json.object();
        delta.set(SignalKConstants.version, Util.getConfigProperty(SignalKConstants.version));
        delta.set(SignalKConstants.timestamp, Util.getIsoTimeString());
        delta.set(SignalKConstants.self_str, Util.getConfigProperty(SignalKConstants.self));
        return delta;
    }
}
