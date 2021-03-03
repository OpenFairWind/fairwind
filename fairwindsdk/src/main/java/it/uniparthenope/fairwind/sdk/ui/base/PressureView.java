package it.uniparthenope.fairwind.sdk.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 13/05/16.
 */
public class PressureView extends SingleDataView {
    public static final String LOG_TAG="PRESSURE_VIEW";

    protected int unit= Formatter.UNIT_PRESSURE_PA;

    public PressureView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public PressureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public PressureView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PressureView);
            String sUnit=a.getString(R.styleable.PressureView_pressure_unit);
            if (sUnit!=null) {
                unit = Integer.parseInt(sUnit);
            }
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatPressure(unit, data, "n/a");
    }
}
