package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.GaugeView;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 30/05/16.
 */
public class AmplifiedWindAngleView extends WindAngleView {

    public AmplifiedWindAngleView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public AmplifiedWindAngleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public AmplifiedWindAngleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }


    @Override
    protected int getGaugeLayout() { return R.layout.amplified_wind_angle_view; }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WindAngleView);

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

            String path= FairWindModelBase.getWindAnglePath(vessel_uuid,windMode);
            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
            setLabel(label);




        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    public void onSetGauge(Double data) {
        GaugeView gaugeData=(GaugeView) findViewById(R.id.gauge_data);;
        if (gaugeData!=null) {
            gaugeData.setTargetValue((float)Math.toDegrees(data.floatValue()));

        }
    }
}
