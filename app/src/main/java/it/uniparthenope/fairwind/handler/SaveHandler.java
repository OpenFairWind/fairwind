package it.uniparthenope.fairwind.handler;

import android.os.Handler;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NavigableMap;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindEventListener;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;

/**
 * Created by raffaelemontella on 28/09/2017.
 */

public class SaveHandler extends HandlerBase {
    public static final String LOG_TAG="SAVE_HANDLER";


    private static long lastSave = System.currentTimeMillis();

    private long saveMillis=60000;



    public SaveHandler() {

    }

    @Override
    public void handle(SignalKModel signalKModel) {
        // Periodically save the document
        if (System.currentTimeMillis() - lastSave > saveMillis) {
            try {
                FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
                Log.d(LOG_TAG,"Document periodic saving...");
                SignalKModelFactory.save(fairWindModel);
                Log.d(LOG_TAG,"...done");
                lastSave = System.currentTimeMillis();
            } catch (IOException e) {
                Log.e(LOG_TAG,e.getMessage());
            }

        }
    }


}
