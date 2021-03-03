package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.CompassView;
import it.uniparthenope.fairwind.sdk.util.WindMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 17/06/16.
 */
public class WindDirectionView extends CompassView {
    public static final String LOG_TAG="WIND_DIRECTION_VIEW";

    private WindMode windMode;

    public WindDirectionView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public WindDirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public WindDirectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WindDirectionView);
            String sWindMode = a.getString(R.styleable.WindDirectionView_wind);
            if (sWindMode!=null) {
                windMode = WindMode.values()[Integer.parseInt(sWindMode)];
            }

            String label=null;
            String pathDirection=FairWindModelBase.getWindDirectionPath(this.vessel_uuid,compassMode,windMode);
            switch (windMode) {
                case APPARENT:
                    label= "AWD";
                    break;
                case TRUE:
                    label= "TWD";
                    break;
                case OVERGROUND:
                    label= "TWD (o)";
                    break;
            }

            setLabel(label);

            setEvent(new FairWindEvent(pathDirection+".*",pathDirection,0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
