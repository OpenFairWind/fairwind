package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.gauges.AngleView;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 30/05/16.
 */
public class InclinometerView extends AngleView {
    public InclinometerView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public InclinometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public InclinometerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }


    @Override
    protected int getGaugeLayout() { return R.layout.inclinometer_view; }

    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {

        if (attrs!=null) {

            //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InclinometerView);

            String label="°";

            String path = FairWindModelBase.getNavAttitudeRollPath(vessel_uuid);
            setEvent(new FairWindEvent(path+".*",path,0, PathEvent.EventType.ADD,this));
            setLabel(label);

            setData(0.0);


        }
        Log.d(LOG_TAG,"/initializeViews");
    }
}
