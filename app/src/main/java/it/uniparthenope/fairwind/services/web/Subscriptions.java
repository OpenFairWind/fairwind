package it.uniparthenope.fairwind.services.web;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import mjson.Json;
import nz.co.fortytwo.signalk.model.event.PathEvent;


/**
 * Created by raffaelemontella on 05/02/16.
 */
public class Subscriptions extends HashMap<String, Subscription> {

    private static final String LOG_TAG = "SUBSCRIPTIONS";
    private SignalkWebSocket signalkWebSocket;
    public SignalkWebSocket getSignalkWebSocket() { return signalkWebSocket; }

    public Subscriptions(SignalkWebSocket signalkWebSocket) {
        this.signalkWebSocket=signalkWebSocket;
    }

    public void parse(Json json) throws SubscriptionException, IOException {
        Json context=json.at("context");
        if (context==null) {
            throw new SubscriptionException("Missing context");
        }
        Json subscribes=json.at("subscribe");
        if(subscribes!=null){
            if (!subscribes.isArray()) {
                subscribes=Json.array();
                subscribes.add(json.at("subscribe"));
            }
            for (Json subscribe:subscribes.asJsonList()) {
                subscribe(context.toString(),subscribe);
            }
        }
        Json unsubscribes=json.at("unsubscribe");
        if(unsubscribes!=null) {
            if (!unsubscribes.isArray()) {
                unsubscribes = Json.array();
                unsubscribes.add(json.at("unsubscribe"));
            }
            for (Json unsubscribe : unsubscribes.asJsonList()) {
                unsubscribe(context.toString(), unsubscribe);
            }
        }
    }

    private void subscribe(String context, Json json) throws SubscriptionException, IOException {
        if (json.has("path")) {
            String path=context+json.at("path").toString();
            Subscription subscription=get(path);
            if (subscription==null) {
                subscription=new Subscription(this, context, json);
                put(path,subscription);

            } else {
                throw new SubscriptionException("Subscription already active");
            }
        } else {
            throw new SubscriptionException("Missing path");
        }


    }

    private void unsubscribe(String context, Json json) throws SubscriptionException {
        if (json.has("path")==true) {
            String path = context+json.at("path").toString();
            Subscription subscription = get(path);
            if (subscription != null) {
                remove(subscription.getContext()+subscription.getPath());
            } else {
                throw new SubscriptionException("Subscription not active");
            }
        } else {
            throw new SubscriptionException("Missing path");
        }
    }

    public void update(PathEvent pathEvent) throws IOException {
        for (Subscription subscription:this.values()) {
            subscription.update(pathEvent);
        }
    }
}
