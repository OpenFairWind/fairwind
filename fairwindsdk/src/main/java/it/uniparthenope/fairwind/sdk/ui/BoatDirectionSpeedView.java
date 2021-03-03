package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.CompassSpeedView;
import it.uniparthenope.fairwind.sdk.util.DirectionMode;
import it.uniparthenope.fairwind.sdk.util.SpeedMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class BoatDirectionSpeedView extends CompassSpeedView {

    public static final String LOG_TAG="BOAT_DIR_SPEED_VIEW";

    protected SpeedMode speedMode=SpeedMode.SOG;
    protected DirectionMode directionMode=DirectionMode.COG;

    public BoatDirectionSpeedView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public BoatDirectionSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public BoatDirectionSpeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");
        doInflate(context);

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BoatDirectionSpeedView);
            String sDirection = a.getString(R.styleable.BoatDirectionSpeedView_direction);
            if (sDirection!=null) {
                directionMode = DirectionMode.values()[Integer.parseInt(sDirection)];
            }
            String sSpeed = a.getString(R.styleable.BoatDirectionSpeedView_speed);
            if (sSpeed!=null) {
                speedMode = SpeedMode.values()[Integer.parseInt(sSpeed)];
            }
            String[] pathAndLabel=FairWindModelBase.getDirectionPathAndLabel(vessel_uuid,directionMode,compassMode);
            String label=pathAndLabel[1];
            setEvent(new FairWindEvent(pathAndLabel[0]+".*",pathAndLabel[0],0, PathEvent.EventType.ADD,this));


            pathAndLabel=FairWindModelBase.getSpeedPathAndLabel(vessel_uuid,speedMode);
            label+=" "+pathAndLabel[1];
            setLabel(label);
            setEventSecondary(new FairWindEvent(pathAndLabel[0]+".*",pathAndLabel[0],0, PathEvent.EventType.ADD,this));
        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
