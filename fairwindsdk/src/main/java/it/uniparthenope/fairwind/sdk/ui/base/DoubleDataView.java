package it.uniparthenope.fairwind.sdk.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;

/**
 * Created by raffaelemontella on 11/05/16.
 */
public abstract class DoubleDataView extends SingleDataView {
    public static final String LOG_TAG="DOUBLE_DATA_VIEW";

    private FairWindEvent eventSecondary;
    public void setEventSecondary(FairWindEvent eventSecondary) {
        if (!getRootView().isInEditMode()) {
            this.eventSecondary = eventSecondary;
            getFairWindModel().register(eventSecondary);
        }
    }

    public DoubleDataView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public DoubleDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public DoubleDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }


    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
    }


    protected int getTextLayout() { return R.layout.double_data_text1_view; }
    protected int getGaugeLayout() { return R.layout.double_data_gauge1_view; }
    protected int getPlotLayout() { return R.layout.single_data_plot1_view; }
    protected int getMeterLayout() { return R.layout.double_data_gauge1_view; }


    public abstract String getFormattedValueSecondary(Double data);

    @Override
    public void setData(Double data) {
        String value= getFormattedValue(data);
        TextView txtData=(TextView) findViewById(R.id.txt_data);
        // Set view values
        if (txtData!=null) {
            txtData.setText(value);
        }
    }

    public void setDataSecondary(Double data) {
        String value= getFormattedValueSecondary(data);
        TextView txtDataSecondary=(TextView) findViewById(R.id.txt_data_secondary);
        // Set view values
        if (txtDataSecondary!=null) {
            txtDataSecondary.setText(value);
        }
    }

    @Override
    protected void onUpdate(FairWindEvent event) {
        super.onUpdate(event);
        Log.d(LOG_TAG,"onUpdate:"+getId());
        if (eventSecondary.isMatching(event)) {
            String path=event.getPathValue();
            Log.d(LOG_TAG,"onUpdate:path1="+path);
            Double data=getFairWindModel().getDoubleByPath(path);
            if (data!=null) {
                setDataSecondary(data);
            }
        }

        Log.d(LOG_TAG,":onUpdate");
    }
}
