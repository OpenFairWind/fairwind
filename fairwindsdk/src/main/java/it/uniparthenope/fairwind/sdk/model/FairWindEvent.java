package it.uniparthenope.fairwind.sdk.model;

import android.util.Log;

import java.util.regex.Pattern;

import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Created by raffaelemontella on 30/04/16.
 */
public class FairWindEvent extends PathEvent {
    private static final String LOG_TAG = "EVENT";

    private String pathValue;
    private FairWindEventListener listener;
    private Pattern pattern;

    public FairWindEvent(FairWindEvent fairWindEvent,PathEvent pathEvent) {
        super(pathEvent.getPath(),pathEvent.getRevision(),pathEvent.getType());
        pathValue=fairWindEvent.getPathValue();
        listener=fairWindEvent.getListener();

        pattern = Pattern.compile(Util.sanitizePath(Util.fixSelfKey(getPath().replace("$", "\\$"))).replace(".", "[.]").replace("*", ".*").replace("?", "."));

        Log.d(LOG_TAG,pattern.pattern());
    }


    public FairWindEvent(String path, String pathValue, int revision, EventType eventType, FairWindEventListener listener) {
        super(path,revision,eventType);
        this.pathValue=pathValue;
        this.listener=listener;

        pattern = Pattern.compile(Util.sanitizePath(Util.fixSelfKey(path.replace("$", "\\$"))).replace(".", "[.]").replace("*", ".*").replace("?", "."));

        Log.d(LOG_TAG,pattern.pattern());
    }

    public FairWindEventListener getListener() { return listener; }
    public boolean isMatching(PathEvent pathEvent) { return pattern.matcher(pathEvent.getPath()).matches() && getType()==pathEvent.getType(); }
    public String getPathValue() { return pathValue; }
}
