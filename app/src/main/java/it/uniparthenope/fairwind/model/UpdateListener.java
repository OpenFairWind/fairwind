package it.uniparthenope.fairwind.model;

import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 03/03/16.
 */
public interface UpdateListener {
    public void onUpdate(PathEvent pathEvent) throws UpdateException;
}
