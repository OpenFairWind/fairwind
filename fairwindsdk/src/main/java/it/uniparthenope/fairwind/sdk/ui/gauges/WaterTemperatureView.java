package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.TemperatureView;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class WaterTemperatureView extends TemperatureView {
    public WaterTemperatureView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public WaterTemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public WaterTemperatureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterTemperatureView);


            String path = FairWindModelBase.getEnvWaterTemperaturePath(vessel_uuid);
            String label="WATER TEMP.";

            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
            setLabel(label);
        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
