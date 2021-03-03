package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.CompassSpeedView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class WptInfoView extends CompassSpeedView {
    public static final String LOG_TAG="WPTINFO_VIEW";


    public WptInfoView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public WptInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public WptInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompassSpeedView);

            String label="";

            String pathSpeed=FairWindModelBase.getNavPerformanceVelocityMadeGoodToWaypointPath(this.vessel_uuid);
            String pathCompass=FairWindModelBase.getBearingTrackPath(this.vessel_uuid,compassMode);
            switch (compassMode) {
                case MAGNETIC:
                    label="WPT INFO";
                    break;
                case TRUE:
                    label="WPT INFO (t)";
                    break;
            }

            setLabel(label);

            setEvent(new FairWindEvent(pathCompass+".*",pathCompass,0, PathEvent.EventType.ADD,this));
            setEventSecondary(new FairWindEvent(pathSpeed+".*",pathSpeed,0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatDirection(data, "n/a");
    }

    @Override
    public String getFormattedValueSecondary(Double data) {
        return Formatter.formatSpeed(Formatter.UNIT_SPEED_KNT, data, "n/a");
    }
}
