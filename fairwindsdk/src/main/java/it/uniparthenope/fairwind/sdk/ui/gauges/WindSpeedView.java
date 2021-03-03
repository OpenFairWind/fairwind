package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.SpeedView;
import it.uniparthenope.fairwind.sdk.util.WindMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 17/06/16.
 */
public class WindSpeedView extends SpeedView {
    public static final String LOG_TAG="WIND_SPEED_VIEW";

    private WindMode windMode;

    public WindSpeedView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public WindSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public WindSpeedView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WindSpeedView);
            String sWindMode = a.getString(R.styleable.WindSpeedView_wind);
            if (sWindMode!=null) {
                windMode = WindMode.values()[Integer.parseInt(sWindMode)];
            }

            String label="";
            String path=FairWindModelBase.getWindSpeedPath(vessel_uuid,windMode);
            switch (windMode) {
                case APPARENT:
                    label = "AWS";
                    break;
                case TRUE:
                    label = "TWS";
                    break;
                case OVERGROUND:
                    label = "TWS (o)";
                    break;
            }


            setLabel(label);

            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
