package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.CompassSpeedView;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class SetDriftView extends CompassSpeedView {
    public SetDriftView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public SetDriftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public SetDriftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SetDriftView);

            String pathSet=FairWindModelBase.getCurrentSetPath(vessel_uuid,compassMode);
            String pathDrift=FairWindModelBase.getEnvCurrentDriftPath(vessel_uuid);
            String label="SET-DRIFT";

            setEvent(new FairWindEvent(pathSet+".*",pathSet,0, PathEvent.EventType.ADD,this));
            setEventSecondary(new FairWindEvent(pathDrift+".*",pathDrift,0, PathEvent.EventType.ADD,this));
            setLabel(label);
        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
