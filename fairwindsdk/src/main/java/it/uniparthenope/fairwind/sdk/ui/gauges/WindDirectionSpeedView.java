package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.CompassSpeedView;
import it.uniparthenope.fairwind.sdk.util.WindMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class WindDirectionSpeedView extends CompassSpeedView {


    private WindMode windMode;

    public WindDirectionSpeedView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public WindDirectionSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public WindDirectionSpeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WindDirectionSpeedView);
            String sWindMode = a.getString(R.styleable.WindDirectionSpeedView_wind);
            if (sWindMode!=null) {
                windMode = WindMode.values()[Integer.parseInt(sWindMode)];
            }

            String label=null;
            String pathDirection=FairWindModelBase.getWindDirectionPath(vessel_uuid,compassMode,windMode);
            String pathSpeed=FairWindModelBase.getWindSpeedPath(vessel_uuid,windMode);
            switch (windMode) {
                case APPARENT:
                    switch (compassMode) {
                        case MAGNETIC:
                            label="AWD/AWS";
                            break;

                        case TRUE:
                            label="AWD (t)/AWS";
                            break;
                    }
                    break;
                case TRUE:
                    switch (compassMode) {
                        case MAGNETIC:
                            label="TWD/TWS";
                            break;

                        case TRUE:
                            label="TWD (t)/TWS";
                            break;
                    }
                    break;
                case OVERGROUND:
                    switch (compassMode) {
                        case MAGNETIC:
                            label="Ground Wind";
                            break;

                        case TRUE:
                            label="Ground Wind (t)";
                            break;
                    }
                    break;
            }


            setLabel(label);

            setEvent(new FairWindEvent(pathDirection+".*",pathDirection,0, PathEvent.EventType.ADD,this));
            setEventSecondary(new FairWindEvent(pathSpeed+".*",pathSpeed,0, PathEvent.EventType.ADD,this));
        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
