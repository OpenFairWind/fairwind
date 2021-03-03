package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.GaugeView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.WindMode;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 30/05/16.
 */
public class WindAngleView extends AngleView {

    public static final String LOG_TAG="ANGLE_VIEW";

    protected WindMode windMode;


    public WindAngleView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public WindAngleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public WindAngleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }


    @Override
    protected int getGaugeLayout() { return R.layout.wind_angle_view; }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WindAngleView);
            String sWindMode = a.getString(R.styleable.WindAngleView_wind);
            if (sWindMode!=null) {
                windMode = WindMode.values()[Integer.parseInt(sWindMode)];
            }
            String label=null;
            switch (windMode) {
                case APPARENT:
                    label="AWA";
                    break;
                case TRUE:
                    label="TWA";
                    break;
                case OVERGROUND:
                    label="TWA (o)";
                    break;
            }

            String path=FairWindModelBase.getWindAnglePath(vessel_uuid,windMode);
            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
            setLabel(label);




        }
        Log.d(LOG_TAG,"/initializeViews");
    }


    @Override
    public void onSetGauge(Double data) {
        Log.d(LOG_TAG,"onSteGauge");
        GaugeView gaugeData;
        gaugeData=(GaugeView) findViewById(R.id.gauge_data);;
        if (gaugeData!=null) {
            float angle=(float)Math.toDegrees(data.floatValue());
            if (angle>180 && angle<=360) {
                //angle=angle-360;
                angle=-(360-angle);
            }
            Log.d(LOG_TAG,"onSteGauge:"+angle);
            gaugeData.setTargetValue(angle);
            Double windSpeed=getFairWindModel().getApparentWindSpeed();
            gaugeData.setTextValue(Formatter.formatSpeed(Formatter.UNIT_SPEED_KNT,windSpeed,"n/a"));
        }
    }
}
