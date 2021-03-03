package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.util.AttributeSet;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.ui.gauges.CoolantTemperatureView;
import it.uniparthenope.fairwind.sdk.ui.gauges.OilPressureView;
import it.uniparthenope.fairwind.sdk.ui.gauges.RPMView;

/**
 * Created by raffaelemontella on 14/05/16.
 */
public class EngineView extends FairWindView {


    private String propulsionId;
    public String getPropulsionId() { return  propulsionId; }


    public EngineView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public EngineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public EngineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.engine_view, this);
    }

    public void setPropulsionId(String propulsionId) {
        this.propulsionId=propulsionId;
        RPMView rpmView=(RPMView)findViewById(R.id.rpmeView);
        if (rpmView!=null) {
            rpmView.setPropulsionId(propulsionId);
        }

        CoolantTemperatureView coolantTemperatureView=(CoolantTemperatureView) findViewById(R.id.coolantTemperatureView);
        if (coolantTemperatureView!=null) {
            coolantTemperatureView.setPropulsionId(propulsionId);
        }
        OilPressureView oilPressureView=(OilPressureView) findViewById(R.id.oilPressureView);
        if (oilPressureView!=null) {
            oilPressureView.setPropulsionId(propulsionId);
        }

    }

    @Override
    protected void onUpdate(FairWindEvent event) {

    }
}
