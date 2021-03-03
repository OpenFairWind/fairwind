package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.GaugeView;
import it.uniparthenope.fairwind.sdk.ui.base.TemperatureView;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 29/05/16.
 */
public class CoolantTemperatureView extends TemperatureView {
    private String propulsionId;

    public CoolantTemperatureView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public CoolantTemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public CoolantTemperatureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    public void setPropulsionId(String propulsionId) {
        if (propulsionId!=null) {
            this.propulsionId = propulsionId;
            String path = FairWindModelBase.getPropulsionCoolantTemperaturePath(vessel_uuid, propulsionId);
            setEvent(new FairWindEvent(path + ".*", path, 0, PathEvent.EventType.ADD, this));
        }
    }

    @Override
    protected int getGaugeLayout() { return R.layout.coolant_temperature_view; }


    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoolantTemperatureView);
            String sPropulsionId=a.getString(R.styleable.CoolantTemperatureView_propulsion_id);
            if (sPropulsionId!=null) {
                setPropulsionId(sPropulsionId);
            }
            setData(0.0);



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
