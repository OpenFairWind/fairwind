package it.uniparthenope.fairwind.services.web;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.regex.Pattern;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.handler.JsonGetHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 05/02/16.
 */
public class Subscription  {
    private static final String LOG_TAG = "SUBSCRIPTION";

    public final static int FORMAT_FULL=0;
    public final static int FORMAT_DELTA=1;
    public final static int POLICY_INSTANT=0;
    public final static int POLICY_IDEAL=1;
    public final static int POLICY_FIXED=2;

    private Subscriptions subscriptions;

    private String context;
    private String path;
    private int period=1000;
    private int format=FORMAT_DELTA;
    private int minPeriod=1000;
    private int policy=POLICY_FIXED;
    private int machinePeriod = 250;

    private HashSet<String> pathEvents= new HashSet<>();

    private Pattern pattern;


    private long lastUpdate=0;
    //private SignalKModel lastSignalKObject = null;
    private Json lastJsonFull=null;

    public int getFormat() { return format; }
    public int getPolicy() { return policy; }
    public long getLastUpdate() { return lastUpdate; }
    public String getPath() { return path; }
    public String getContext() { return "\""+context+"\""; }

    private JsonGetHandler getHandler = new JsonGetHandler();
    private Json getJson;

    public Subscription(Subscriptions subscriptions, String context, Json json) throws SubscriptionException {
        this.context=context.replace("\"","");
        if (json.has("path")==false) {
            throw new SubscriptionException("Missing Path");
        }
        this.subscriptions=subscriptions;
        String myPath;
        path = myPath = json.at("path").toString();
        myPath = sanitizePath(myPath);
        Log.d(LOG_TAG, "path: "+myPath);
        pattern = regexPath(myPath);
        if (json.has("period")) { this.period=json.at("period").asInteger(); }
        if (json.has("minPeriod")) { this.minPeriod=json.at("minPeriod").asInteger(); }
        if (this.minPeriod<this.machinePeriod) this.minPeriod = this.machinePeriod;
        if (json.has("format")) {
            if (json.at("format").toString().toLowerCase().equals("\"delta\"")) {
                this.format = FORMAT_DELTA;
            } else {
                this.format = FORMAT_FULL;
            }
        }
        if (json.has("policy")) {
            if (json.at("policy").toString().equals("\"instant\"")) {
                this.policy = POLICY_INSTANT;
            } else if (json.at("policy").toString().equals("\"ideal\"")) {
                this.policy = POLICY_IDEAL;
            } else {
                this.policy = POLICY_FIXED;
            }
        }

        myPath=myPath.replace("/", SignalKConstants.dot);
        myPath=myPath.replace(".self", SignalKConstants.dot+SignalKConstants.self);
        if(myPath.startsWith(SignalKConstants.dot))myPath=myPath.substring(1);
        if(!myPath.endsWith("*") && !myPath.endsWith(".")) myPath = myPath + ".*";
        if(!myPath.endsWith("*") && myPath.endsWith(".")) myPath = myPath + "*";
        Log.i(LOG_TAG, "end path: "+myPath);
        getJson = Json.object();
        getJson.set(SignalKConstants.CONTEXT, this.context);
        Json getPath = Json.object();
        getPath.set(SignalKConstants.PATH,myPath);
        getPath.set(SignalKConstants.FORMAT,format);
        Json getArray = Json.array();
        getArray.add(getPath);
        getJson.set(SignalKConstants.GET, getArray);


        if (policy==POLICY_INSTANT || policy==POLICY_IDEAL) {
            try {
                PathEvent pathEvent=new PathEvent(myPath,1, PathEvent.EventType.ADD);
                update(pathEvent);
            } catch (IOException e) {
                Log.e(LOG_TAG,e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void update(PathEvent pathEvent) throws IOException{
        Log.d(LOG_TAG, "update... ");
        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        JsonSerializer jsonSerializer=new JsonSerializer();
        long millis=System.currentTimeMillis();


        pathEvents.add(pathEvent.getPath().substring(0,pathEvent.getPath().lastIndexOf(".")));
        if (
                (policy==POLICY_FIXED && (millis-lastUpdate>period)) ||
                        (policy==POLICY_INSTANT && (millis-lastUpdate>minPeriod )) ||
                        (policy==POLICY_IDEAL && (millis-lastUpdate>minPeriod ))
                ) {
            SignalKModel temp = Utils.getSubTreeByKeys(pathEvents);
            if (temp!=null) {
                Json jsonFull = jsonSerializer.writeJson(temp);
                if (jsonFull != null) {
                    jsonToSend(jsonFull);
                    lastJsonFull=jsonFull;
                } else {
                    if (lastJsonFull!=null && (policy == POLICY_FIXED || (policy == POLICY_IDEAL && (millis - lastUpdate > period)))) {
                        jsonToSend(lastJsonFull);
                        lastUpdate = millis;
                    }
                }
                pathEvents.clear();
            }
            if(policy!=POLICY_IDEAL) {
                lastUpdate = millis;
            }
        }
    }


    private void jsonToSend(Json jsonToSend){

        FullToDeltaConverter fullToDeltaConverter=new FullToDeltaConverter();
        String jsonString;
        if (format == SignalkWebSocket.FORMAT_DELTA) {
            Log.d(LOG_TAG,"Prepare to send");
            SignalKModel temp = SignalKModelFactory.getCleanInstance();

            List<Json> jsonDeltas = fullToDeltaConverter.handle(jsonToSend);
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
        } else {
            jsonString = jsonToSend.toString();
            Log.d(LOG_TAG, "S:full " + jsonString);
            send(jsonString);
        }
    }

    private void send(String s) {
        SignalkWebSocket signalkWebSocket = null;
        try {
            signalkWebSocket = subscriptions.getSignalkWebSocket();
            signalkWebSocket.send(s);
        } catch (IOException e) {
            Log.d(LOG_TAG, "exception: "+e.getMessage());
        }
    }

    public static Pattern regexPath(String newPath) {
        // regex it
        String regex = newPath.replaceAll(".", "[$0]").replace("[*]", ".*").replace("[?]", ".");
        return Pattern.compile(regex);
    }


    public static String sanitizePath(String newPath) {
        newPath = newPath.replace('/', '.').replace("\"","");
        if (newPath.startsWith(SignalKConstants.dot))
            newPath = newPath.substring(1);
        if (SignalKConstants.vessels_dot_self.equals(newPath)){
            newPath = SignalKConstants.vessels_dot_self;
        }
        newPath = newPath.replace(SignalKConstants.vessels_dot_self + SignalKConstants.dot, SignalKConstants.vessels_dot_self_dot);
        return newPath;
    }
}
