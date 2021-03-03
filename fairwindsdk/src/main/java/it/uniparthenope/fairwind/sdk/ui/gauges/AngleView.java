package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.ui.base.GaugeView;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 01/05/16.
 */
public class AngleView extends SingleDataView {
    public static final String LOG_TAG="ANGLE_VIEW";

    public AngleView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public AngleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public AngleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews");

        if (attrs!=null) {
            //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AngleView);
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatAngle(data,"n/a");
    }



    public void onSetGauge(Double data) {
        GaugeView gaugeData;
        gaugeData=(GaugeView) findViewById(R.id.gauge_data);;
        if (gaugeData!=null) {
            float angle=(float)Math.toDegrees(data.floatValue());
            gaugeData.setTargetValue(angle);
            gaugeData.setTextValue(getFormattedValue(data));
        }
    }
}
