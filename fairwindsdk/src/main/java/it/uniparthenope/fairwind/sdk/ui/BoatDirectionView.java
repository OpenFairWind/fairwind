package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.CompassView;
import it.uniparthenope.fairwind.sdk.util.DirectionMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class BoatDirectionView extends CompassView {
    public static final String LOG_TAG="BOAT_DIRECTION_VIEW";

    protected DirectionMode directionMode=DirectionMode.COG;

    public BoatDirectionView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public BoatDirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public BoatDirectionView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BoatDirectionView);
            String sDirection = a.getString(R.styleable.BoatDirectionView_direction);
            if (sDirection!=null) {
                directionMode = DirectionMode.values()[Integer.parseInt(sDirection)];
                if (directionMode==DirectionMode.COG) {
                    Log.d(LOG_TAG,"cog");
                }
            }
            String[] pathAndLabel=FairWindModelBase.getDirectionPathAndLabel(vessel_uuid,directionMode,compassMode);
            setLabel(pathAndLabel[1]);
            setEvent(new FairWindEvent(pathAndLabel[0]+".*",pathAndLabel[0],0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
