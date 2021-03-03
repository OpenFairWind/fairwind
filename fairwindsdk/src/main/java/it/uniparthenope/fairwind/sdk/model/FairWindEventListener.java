package it.uniparthenope.fairwind.sdk.model;

import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 30/04/16.
 */
public interface FairWindEventListener {
    public void onEvent(FairWindEvent event);
}
