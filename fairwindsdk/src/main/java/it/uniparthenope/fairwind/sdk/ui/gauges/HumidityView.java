package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.RatioView;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class HumidityView extends RatioView {

    private boolean internal=true;

    public HumidityView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public HumidityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public HumidityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }
    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HumidityView);
            internal = Boolean.parseBoolean(a.getString(R.styleable.HumidityView_internal));


            String label,path;

            if (internal) {
                path = FairWindModelBase.getEnvInsideHumidityPath(vessel_uuid);
                label="INT. HUMIDITY";
            } else {
                path = FairWindModelBase.getEnvOutsideHumidityPath(vessel_uuid);
                label="OUT. HUMIDITY";
            }
            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
            setLabel(label);

        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
