package it.uniparthenope.fairwind.sdk.ui.base;


import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.FairWindView;
import it.uniparthenope.fairwind.sdk.util.StyleMode;


/**
 * Created by raffaelemontella on 07/05/16.
 */
public abstract class SingleDataView extends FairWindView implements PlotListener {
    public static final String LOG_TAG="SINGLE_DATA_VIEW";

    protected StyleMode mode=StyleMode.TEXT;

    protected String vessel_uuid="self";

    private FairWindEvent event;
    public void setEvent(FairWindEvent event) {
        if (!getRootView().isInEditMode()) {
            this.event = event;
            getFairWindModel().register(event);
        }
    }


    protected SimpleXYSeries dataSeries = null;
    protected static final int HISTORY_SIZE = 500;
    protected double xAxis=0;

    private long oldMillis=System.currentTimeMillis();

    public SingleDataView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public SingleDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public SingleDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context,attrs,defStyle);
    }


    private void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        Log.d(LOG_TAG,"initializeViews: ["+getId()+"]"+this.getClass().getName()+"-"+this.getChildCount());
        if (attrs!=null) {

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleDataView);
            String sVesselUuid = a.getString(R.styleable.SingleDataView_vessel_uuid);
            if (sVesselUuid==null) {
                sVesselUuid="self";
            }
            //sVesselUuid="urn:mrn:imo:mmsi:323713387";
            vessel_uuid = FairWindModelBase.fixSelfKey(sVesselUuid);
            //String self= SignalKConstants.self;
            //Log.d(LOG_TAG,"boat UUID -> "+self);

            String sMode = a.getString(R.styleable.SingleDataView_mode);
            if (sMode!=null) {
                mode = StyleMode.values()[Integer.parseInt(sMode)];
            }
            doInflate(context);
        }


    }

    protected int getTextLayout() { return R.layout.single_data_text1_view; }
    protected int getGaugeLayout() { return R.layout.single_data_gauge1_view; }
    protected int getPlotLayout() { return R.layout.single_data_plot1_view; }
    protected int getMeterLayout() { return R.layout.single_data_gauge1_view; }

    protected void doInflate(Context context) {
        Log.d(LOG_TAG,"doInflate: ["+getId()+"]"+this.getClass().getName()+"-"+this.getChildCount());

        int layout=R.layout.single_data_text1_view;
        switch (mode) {
            case TEXT:
                layout=getTextLayout();
                break;

            case GAUGE:
                layout=getGaugeLayout();
                break;

            case METER:
                layout=getMeterLayout();
                break;

            case PLOT:
                layout=getPlotLayout();
                break;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        layoutInflater.inflate(layout,this,true);

        switch (mode) {
            case PLOT:
                // initialize our XYPlot reference:
                XYPlot plot_data = (XYPlot)findViewById(R.id.plot_data);
                if (plot_data!=null) {

                    dataSeries = new SimpleXYSeries("Data");
                    plot_data.addSeries(dataSeries, new LineAndPointFormatter(Color.YELLOW, null, null, null));

                    plot_data.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
                    plot_data.setDomainStepValue(5);
                    plot_data.setTicksPerRangeLabel(5);
                    plot_data.setDomainLabel("Distance (m)");
                    plot_data.getDomainLabelWidget().pack();
                    plot_data.setRangeLabel("Depth (m)");
                    plot_data.getRangeLabelWidget().pack();

                    final PlotStatistics histStats = new PlotStatistics(1000, false);
                    plot_data.addListener(histStats);
                }
                break;
        }

    }



    public abstract String getFormattedValue(Double data);



    public void onSetText(Double data) {
        TextView txtData;
        txtData=(TextView) findViewById(R.id.txt_data);
        // Set view values
        if (txtData!=null) {
            txtData.setText(getFormattedValue(data));
        }
    }

    public void onSetGauge(Double data) {
        GaugeView gaugeData;
        gaugeData=(GaugeView) findViewById(R.id.gauge_data);;
        if (gaugeData!=null) {
            String textValue=getFormattedValue(data);
            float floatValue=0;
            try {
                floatValue = Float.parseFloat(textValue.split(" ")[0]);
            } catch (NumberFormatException e) {
                Log.d(LOG_TAG,e.getMessage());
            }
            gaugeData.setTargetValue(floatValue);
            gaugeData.setTextValue(textValue);
        }
    }

    public void onSetPlot(Double data) {
        // get rid the oldest sample in history:
        if ( dataSeries.size() > HISTORY_SIZE) {
            dataSeries.removeFirst();
        }
        long curMillis=System.currentTimeMillis();
        xAxis=xAxis+(curMillis-oldMillis)/1000;
        oldMillis=curMillis;

        dataSeries.addLast(xAxis,data);

        XYPlot plot_data = (XYPlot)findViewById(R.id.plot_data);
        if (plot_data!=null) {
            plot_data.redraw();
        }
    }

    public void setData(Double data) {
        switch (mode) {
            case TEXT:
                onSetText(data);
                break;
            case GAUGE:
                onSetGauge(data);
                break;
            case PLOT:
                onSetPlot(data);
                break;
        }

    }


    public void setLabel(String label) {
        TextView lblData = (TextView) findViewById(R.id.lbl_data);
        if (lblData!=null) {
            lblData.setText(label);
        }
    }

    @Override
    protected void onUpdate(FairWindEvent event) {
        Log.d(LOG_TAG,"onUpdate:"+event.getPath());

        if (this.event.isMatching(event)) {
            String path = event.getPathValue();
            Double data = getFairWindModel().getDoubleByPath(path);
            if (data != null) {
                setData(data);
            }
        }
    }

    @Override
    public void onBeforeDraw(Plot source, Canvas canvas) {

    }

    @Override
    public void onAfterDraw(Plot source, Canvas canvas) {

    }
}
