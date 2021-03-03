package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.PressureView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 16/06/16.
 */
public class AirPressureView extends PressureView {
    public static final String LOG_TAG="AIR_PRESSURE_VIEW";



    public AirPressureView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public AirPressureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public AirPressureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private FairWindEvent event;

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AirPressureView);
            String sUnit=a.getString(R.styleable.PressureView_pressure_unit);
            if (sUnit!=null) {
                unit = Integer.parseInt(sUnit);
            } else {
                unit= Formatter.UNIT_PRESSURE_HPA;
            }

            String path = FairWindModelBase.getEnvOutsidePressurePath(vessel_uuid);
            String label="AIR PRESS.";

            event=new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this);
            setEvent(event);
            setLabel(label);
        }

        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    protected void onUpdate(FairWindEvent event) {
        Log.d(LOG_TAG,"onUpdate:"+getId());
        if (this.event.isMatching(event)) {
            String path = event.getPathValue();
            Double data = getFairWindModel().getDoubleByPath(path);
            if (data != null) {
                setData(data);
            }
        }
    }
}
