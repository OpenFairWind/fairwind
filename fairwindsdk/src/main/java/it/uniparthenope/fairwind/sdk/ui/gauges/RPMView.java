package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.ui.base.GaugeView;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 13/05/16.
 */
public class RPMView extends SingleDataView {
    public static final String LOG_TAG="RPM_VIEW";

    private int unit= Formatter.UNIT_RPM_X1;
    private String propulsionId;

    public RPMView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public RPMView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public RPMView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    @Override
    protected int getGaugeLayout() { return R.layout.rpm_view; }


    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RPMView);
            String sUnit=a.getString(R.styleable.RPMView_unit_rpm);
            if (sUnit!=null) {
                unit=Integer.parseInt(sUnit);
            }
            String sPropulsionId=a.getString(R.styleable.RPMView_propulsion_id);
            if (sPropulsionId!=null) {
                setPropulsionId(sPropulsionId);
            }
            String label = "RPM";
            setLabel(label);
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatRPM(unit, data, "n/a");
    }

    public void setPropulsionId(String propulsionId) {
        if (propulsionId!=null) {
            this.propulsionId = propulsionId;
            String path = FairWindModelBase.getPropulsionRevolutionsPath(vessel_uuid, propulsionId);
            setEvent(new FairWindEvent(path + ".*", path, 0, PathEvent.EventType.ADD, this));
            setData(0.0);
        }
    }

    public void onSetGauge(Double data) {
        GaugeView gaugeData=(GaugeView) findViewById(R.id.gauge_data);;
        if (gaugeData!=null) {
            gaugeData.setTargetValue((float)Math.toDegrees(data.floatValue()));

        }
    }
}
