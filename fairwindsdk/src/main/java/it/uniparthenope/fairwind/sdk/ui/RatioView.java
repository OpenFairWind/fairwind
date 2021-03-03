package it.uniparthenope.fairwind.sdk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.util.Formatter;

/**
 * Created by raffaelemontella on 12/05/16.
 */
public class RatioView extends SingleDataView {
    public static final String LOG_TAG="RATIO_VIEW";

    private float min=0f;
    private float max=100f;


    public RatioView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public RatioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public RatioView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioView);
            String sMin = a.getString(R.styleable.RatioView_minRatio);
            if (sMin!=null) {
                min=Float.parseFloat(sMin);
            }

            String sMax = a.getString(R.styleable.RatioView_maxRatio);
            if (sMax!=null) {
                max=Float.parseFloat(sMax);
            }
        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatRatio(data, "n/a");
    }
}
