package it.uniparthenope.fairwind.sdk.ui.gauges;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.sdk.model.FairWindModelBase;
import it.uniparthenope.fairwind.sdk.ui.base.SingleDataView;
import it.uniparthenope.fairwind.sdk.util.DepthMode;
import it.uniparthenope.fairwind.sdk.util.Formatter;
import it.uniparthenope.fairwind.sdk.util.Position;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 30/04/16.
 */
public class DepthView extends SingleDataView {
    public static final String LOG_TAG="DEPTH_VIEW";

    private int unit=Formatter.UNIT_DEPTH_M;

    private Position position=null;

    private DepthMode depthMode;



    public DepthView(Context context) {
        super(context);
        initializeViews(context,null,0);
    }

    public DepthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context,attrs,0);
    }

    public DepthView(Context context, AttributeSet attrs, int defStyle) {
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

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DepthView);
            String sUnit=a.getString(R.styleable.DepthView_unit_depth);
            if (sUnit!=null) {
                unit=Integer.parseInt(sUnit);
            }
            String sDepthMode = a.getString(R.styleable.DepthView_depth);
            if (sDepthMode!=null) {
                depthMode = DepthMode.values()[Integer.parseInt(sDepthMode)];
            }




            String label="";
            switch (depthMode) {
                case BELOW_KEEL:
                    label="keel";
                    break;
                case BELOW_SURFACE:
                    label="surface";
                    break;

                case BELOW_TRANSDUCER:
                    label="transducer";
            }
            setLabel("Depth ("+label+")");

            setEvent(new FairWindEvent(FairWindModelBase.getDepthPath(vessel_uuid,depthMode)+".*",FairWindModelBase.getDepthPath(vessel_uuid,depthMode),0, PathEvent.EventType.ADD,this));

        }
        Log.d(LOG_TAG,"/initializeViews");
    }

    @Override
    public String getFormattedValue(Double data) {
        return Formatter.formatDepth(unit, data,"n/a");
    }

    private Double oldDataScale=Double.NaN;

    @Override
    public void onSetPlot(Double data) {
        FairWindModel fairWindModel=getFairWindModel();



        // Check if there is a valid position and a valid depth
        if (data!=null) {
            // Considers the depth negative
            data=Math.abs(data)*-1;


            // Check if the local position object is available
            if (position==null) {
                // The position is not available, create a current position
                position=fairWindModel.getNavPosition("self");
            } else {
                // Create a new position
                Position newPosition=fairWindModel.getNavPosition("self");

                // Evaluate the distance between the old and the new position
                float distStep=(float)position.distanceTo(newPosition);

                // Check if the distance is grater than zero (the boat is moving)
                if (distStep>0) {
                    // get rid the oldest sample in history:
                    if ( dataSeries.size() > HISTORY_SIZE) {
                        dataSeries.removeFirst();
                    }
                    xAxis=xAxis+distStep;
                    dataSeries.addLast(xAxis,data);

                    XYPlot plot_data = (XYPlot)findViewById(R.id.plot_data);
                    if (plot_data!=null) {
                        // The depth scales
                        double dataScales[]={-5,-10,-25,-50, -75,-100,-200,-500};

                        // Check if the depth is deeper then the last scale value
                        if (data<dataScales[dataScales.length-1]) {
                            Log.d(LOG_TAG,"Depth deeper than "+dataScales[dataScales.length-1]+":"+data);
                            // Set the automatic Y axis bounding
                            if (Double.isNaN(oldDataScale)==false) {
                                Log.d(LOG_TAG,"Y Axis bounding setted to automatic");
                                //plot.setRangeBoundaries(0,0,BoundaryMode.AUTO);
                                oldDataScale=Double.NaN;
                            }

                        } else {
                            // The depth is between 0 and the last scale value
                            // For each depth in the depth scales
                            for (double dataScale:dataScales) {
                                // Check if the measured depth is shallower then the current scale value
                                if (data>dataScale) {
                                    Log.d(LOG_TAG,"Data "+data+" greater than "+dataScale);
                                    // Set the minY value if needed
                                    if (oldDataScale.isNaN() || oldDataScale!=dataScale) {
                                        Log.d(LOG_TAG,"Set to depthScale:"+oldDataScale+"->"+dataScale);
                                        plot_data.setRangeBoundaries(dataScale,0, BoundaryMode.FIXED);
                                        oldDataScale=dataScale;
                                    }
                                    break;
                                }
                            }

                        }
                        Log.d(LOG_TAG,"X_Axis:"+xAxis);
                        plot_data.setDomainBoundaries(xAxis - 300, (xAxis + 100), BoundaryMode.FIXED);
                        plot_data.redraw();
                    }
                }
                position=newPosition;
            }
        }

    }

    private void setDepthMode(String sDepthMode, DepthMode defValue) {

        if (sDepthMode==null) {
            depthMode=defValue;
        } else if (sDepthMode.equalsIgnoreCase("below_keel")) {
            depthMode=DepthMode.BELOW_KEEL;
        } else if (sDepthMode.equalsIgnoreCase("below_surface")) {
            depthMode=DepthMode.BELOW_SURFACE;
        } else if (sDepthMode.equalsIgnoreCase("below_transducer")) {
            depthMode=DepthMode.BELOW_TRANSDUCER;
        } else{
            depthMode=defValue;
        }

    }

}
