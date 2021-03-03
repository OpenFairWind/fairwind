package it.uniparthenope.fairwind.sdk.model.resources.tracks;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.UUID;

import it.uniparthenope.fairwind.sdk.model.resources.Resource;
import it.uniparthenope.fairwind.sdk.model.resources.Resources;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;

import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class Tracks extends Resources {

    public static final String LOG_TAG="TRACKS";



    public Tracks(SignalKModel signalKModel) {
        super(signalKModel,resources+dot+"tracks","tracks");
    }


    @Override
    public Resource onNewResource(String uuid, Json jsonItem) {
        return new Track(uuid,jsonItem);
    }

}
