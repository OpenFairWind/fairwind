package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;

import it.uniparthenope.fairwind.sdk.ui.base.DoubleDataView;
import it.uniparthenope.fairwind.sdk.util.CompassMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 01/05/16.
 */
public class CompassSpeedView extends DoubleDataView {
    public static final String LOG_TAG="COMPASS_SPEED_VIEW";

    protected int unit= Formatter.UNIT_SPEED_KNT;
    protected CompassMode compassMode;

    public CompassSpeedView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public CompassSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public CompassSpeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompassSpeedView);
            String sCompassMode = a.getString(R.styleable.CompassSpeedView_compass);
            if (sCompassMode!=null) {
                compassMode=CompassMode.values()[Integer.parseInt(sCompassMode)];
            }
            String sUnit = a.getString(R.styleable.CompassSpeedView_unit_speed);
            if (sUnit!=null) {
                unit=Integer.parseInt(sUnit);
            }



            /*


            String pathSpeed=null;
            String pathCompass=null;
            String label=null;

            switch (viewDataType) {


                case BEARING_VMG2WPT:
                    pathSpeed=FairWindModelBase.getNavPerformanceVelocityMadeGoodToWaypointPath(this.vessel_uuid);
                    pathCompass=FairWindModelBase.getBearingTrackPath(this.vessel_uuid,compassMode);
                    switch (compassMode) {
                        case MAGNETIC:
                            label="BEARING";
                            break;
                        case TRUE:
                            label="BEARIMG (t)";
                            break;
                    }
                    label+="/VMG to WPT";
                    break;

                case BEARING_VMG:
                    pathSpeed=FairWindModelBase.getNavPerformanceVelocityMadeGoodPath(this.vessel_uuid);
                    pathCompass=FairWindModelBase.getBearingTrackPath(this.vessel_uuid,compassMode);
                    switch (compassMode) {
                        case MAGNETIC:
                            label="BEARING";
                            break;
                        case TRUE:
                            label="BEARIMG (t)";
                            break;
                    }
                    label+="/VMG";
                    break;

                case WIND_ANGLE_SPEED:
                    pathCompass=FairWindModelBase.getWindAnglePath(vessel_uuid,windMode);
                    pathSpeed=FairWindModelBase.getWindSpeedPath(vessel_uuid,windMode);
                    switch (windMode) {
                        case TRUE:
                            label="TWA/TWS";
                            break;
                        case APPARENT:
                            label="AWA/AWS";
                            break;
                        case OVERGROUND:
                            label="TWA/TWS (o)";
                    }

                    break;




            }
            Log.d(LOG_TAG,"Label:"+label);
            setLabel(label);

            setEvent(new FairWindEvent(pathCompass+".*",pathCompass,0, PathEvent.EventType.ADD,this));
            setEventSecondary(new FairWindEvent(pathSpeed+".*",pathSpeed,0, PathEvent.EventType.ADD,this));
            */
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatDirection(data, "n/a");
    }

    @Override
    public String getFormattedValueSecondary(Double data) {
        return Formatter.formatSpeed(unit, data, "n/a");
    }


}
