package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.SpeedView;
import it.uniparthenope.fairwind.sdk.util.SpeedMode;
import it.uniparthenope.fairwind.sdk.util.StyleMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 17/06/16.
 */
public class BoatSpeedView extends SpeedView {
    public static final String LOG_TAG="BOAT_SPEED_VIEW";

    protected SpeedMode speedMode=SpeedMode.SOG;

    public BoatSpeedView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public BoatSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public BoatSpeedView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BoatSpeedView);
            String sSpeed = a.getString(R.styleable.BoatSpeedView_speed);
            if (sSpeed!=null) {
                speedMode = SpeedMode.values()[Integer.parseInt(sSpeed)];
            }

            if (speedMode==SpeedMode.SPEEDOMETER && mode==StyleMode.GAUGE) {
                Log.d(LOG_TAG,"speedometer/gauge");
            }

            String[] pathAndLabel=FairWindModelBase.getSpeedPathAndLabel(vessel_uuid,speedMode);
            setLabel(pathAndLabel[1]);
            setEvent(new FairWindEvent(pathAndLabel[0]+".*",pathAndLabel[0],0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }



}
